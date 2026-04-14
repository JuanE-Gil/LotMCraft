package de.jakob.lotm.abilities.death;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.damage.ModDamageTypes;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.AllyUtil;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DivineKingdomAbility extends Ability {

    private static final int RADIUS = 35;
    private static final int DOMAIN_DURATION_TICKS = 20 * 60 * 3;  // 3 minutes

    /** Base countdown in seconds at same sequence */
    private static final int BASE_COUNTDOWN_SECONDS = 45;
    /** Seconds removed from the countdown per sequence the target is weaker */
    private static final int COUNTDOWN_REDUCTION_PER_SEQ = 5;

    /** Damage debuff at same sequence: 30% reduction = multiply outgoing damage by 0.7 */
    private static final float BASE_DEBUFF_MULTIPLIER = 0.70f;
    /** Each sequence the target is weaker reduces the multiplier further by this amount */
    private static final float DEBUFF_SCALE_PER_SEQ = 0.05f;

    /** Durability drained per second (every 20 ticks) per equipment slot */
    private static final int DURABILITY_DRAIN_PER_SECOND = 3;

    private static final String MODIFIER_KEY = "divine_kingdom_debuff";

    private static final DustParticleOptions PALE_DUST =
            new DustParticleOptions(new Vector3f(0.85f, 0.85f, 0.95f), 1.6f);
    private static final DustParticleOptions VOID_DUST =
            new DustParticleOptions(new Vector3f(0.05f, 0.0f, 0.1f), 1.2f);

    // Track per-entity countdown timers: entity UUID -> seconds remaining
    private static final Map<UUID, Integer> countdowns = new ConcurrentHashMap<>();

    public DivineKingdomAbility(String id) {
        super(id, 20 * 300f); // 5-minute cooldown
        canBeCopied = false;
        canBeReplicated = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("death", 1));
    }

    @Override
    protected float getSpiritualityCost() {
        return BeyonderData.getMaxSpirituality(1) * 0.5f;
    }

    @Override
    public void onAbilityUse(Level level, LivingEntity caster) {
        if (level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        int casterSeq = BeyonderData.getSequence(caster);

        // Activation sound and announcement
        level.playSound(null, caster.blockPosition(),
                SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 4.0f, 0.3f);
        level.playSound(null, caster.blockPosition(),
                SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 3.0f, 0.5f);

        if (caster instanceof ServerPlayer player) {
            player.sendSystemMessage(Component.translatable("ability.lotmcraft.divine_kingdom.activated")
                    .withStyle(ChatFormatting.WHITE));
        }

        AtomicInteger ticks = new AtomicInteger(0);

        ServerScheduler.scheduleForDuration(0, 1, DOMAIN_DURATION_TICKS, () -> {
            Vec3 center = caster.position();
            int t = ticks.getAndIncrement();

            // ----------------------------------------------------------------
            // Visuals — ring + sphere pulses
            // ----------------------------------------------------------------
            ParticleUtil.spawnCircleParticles(serverLevel, PALE_DUST, center,
                    new Vec3(0, 1, 0), RADIUS, 32);
            if (t % 10 == 0) {
                ParticleUtil.spawnSphereParticles(serverLevel, ParticleTypes.END_ROD, center, RADIUS, 60);
                ParticleUtil.spawnSphereParticles(serverLevel, VOID_DUST, center, RADIUS * 0.5, 40);
            }

            // ----------------------------------------------------------------
            // Destroy projectiles inside the domain
            // ----------------------------------------------------------------
            AABB domainBox = AABB.ofSize(center, RADIUS * 2, RADIUS * 2, RADIUS * 2);
            List<Projectile> projectiles = serverLevel.getEntitiesOfClass(Projectile.class, domainBox);
            for (Projectile projectile : projectiles) {
                // Only discard projectiles not fired by the caster or allies
                var shooter = projectile.getOwner();
                if (shooter instanceof LivingEntity shooterLiving) {
                    if (shooterLiving == caster || AllyUtil.areAllies(caster, shooterLiving)) continue;
                }
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                        projectile.getX(), projectile.getY(), projectile.getZ(),
                        3, 0.1, 0.1, 0.1, 0.01);
                projectile.discard();
            }

            // ----------------------------------------------------------------
            // Per-entity effects
            // ----------------------------------------------------------------
            List<LivingEntity> entitiesInRange = AbilityUtil.getNearbyEntities(caster, serverLevel, center, RADIUS);
            Set<UUID> inRangeIds = new HashSet<>();

            entitiesInRange.forEach(target -> {
                if (AllyUtil.areAllies(caster, target)) return;

                inRangeIds.add(target.getUUID());

                int targetSeq = BeyonderData.getSequence(target);
                int seqDiff = targetSeq - casterSeq; // positive = target is weaker

                // --- Damage debuff (scales with sequence difference) ---
                float debuffMultiplier = BASE_DEBUFF_MULTIPLIER - (seqDiff * DEBUFF_SCALE_PER_SEQ);
                debuffMultiplier = Math.max(0.0f, Math.min(1.0f, debuffMultiplier));
                BeyonderData.addModifier(target, MODIFIER_KEY, debuffMultiplier);

                // --- Durability drain every second ---
                if (t % 20 == 0) {
                    drainDurability(target, serverLevel);
                }

                // --- Countdown: only tick while inside, initialize on first entry ---
                if (t % 20 == 0) {
                    int current = countdowns.computeIfAbsent(target.getUUID(), k -> {
                        int base = BASE_COUNTDOWN_SECONDS - (seqDiff * COUNTDOWN_REDUCTION_PER_SEQ);
                        return Math.max(1, base);
                    });

                    int newCount = current - 1;
                    countdowns.put(target.getUUID(), newCount);

                    // Show countdown on target's action bar
                    if (target instanceof ServerPlayer targetPlayer) {
                        Component msg = newCount > 0
                                ? Component.literal("☠ Divine Kingdom: " + newCount + "s ☠")
                                        .withStyle(ChatFormatting.WHITE)
                                : Component.literal("☠ Divine Kingdom: 0s ☠")
                                        .withStyle(ChatFormatting.DARK_GRAY);
                        AbilityUtil.sendActionBar(targetPlayer, msg);
                    }

                    if (newCount <= 0) {
                        downEntity(target, caster, serverLevel);
                        countdowns.remove(target.getUUID());
                    }
                }
            });

            // Remove countdown and debuff for entities that left the domain this tick
            countdowns.keySet().removeIf(uuid -> {
                if (inRangeIds.contains(uuid)) return false;
                // Clean up the damage modifier for the entity that left
                net.minecraft.world.entity.Entity e = serverLevel.getEntity(uuid);
                if (e instanceof LivingEntity living) {
                    BeyonderData.removeModifier(living, MODIFIER_KEY);
                }
                return true;
            });

        }, () -> {
            // On domain expiry — clean up all modifiers and countdowns
            AbilityUtil.getNearbyEntities(caster, serverLevel, caster.position(), RADIUS).forEach(target ->
                    BeyonderData.removeModifier(target, MODIFIER_KEY));
            countdowns.clear();

            if (caster instanceof ServerPlayer player) {
                player.sendSystemMessage(Component.translatable("ability.lotmcraft.divine_kingdom.expired")
                        .withStyle(ChatFormatting.GRAY));
            }
        }, serverLevel);
    }

    // -------------------------------------------------------------------------
    // Durability drain — affects all equipment slots
    // -------------------------------------------------------------------------

    private static void drainDurability(LivingEntity target, ServerLevel level) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack item = target.getItemBySlot(slot);
            if (!item.isEmpty() && item.isDamageableItem()) {
                item.hurtAndBreak(DURABILITY_DRAIN_PER_SECOND, target, slot);
            }
        }
        // Also drain held items for players (inventory hotbar items not in equipment slots)
        if (target instanceof ServerPlayer player) {
            ItemStack offhand = player.getOffhandItem();
            if (!offhand.isEmpty() && offhand.isDamageableItem()) {
                offhand.hurtAndBreak(DURABILITY_DRAIN_PER_SECOND, player, EquipmentSlot.OFFHAND);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Downed state — heavy incapacitation
    // -------------------------------------------------------------------------

    private static void downEntity(LivingEntity target, LivingEntity caster, ServerLevel level) {
        target.hurt(ModDamageTypes.source(level, ModDamageTypes.BEYONDER_GENERIC, caster),
                Float.MAX_VALUE);

        level.playSound(null, target.blockPosition(),
                SoundEvents.WITHER_DEATH, SoundSource.PLAYERS, 1.5f, 0.6f);
        ParticleUtil.spawnSphereParticles(level, ParticleTypes.SOUL, target.position().add(0, 1, 0), 2, 60);
    }
}
