package de.jakob.lotm.abilities.death;

import de.jakob.lotm.abilities.PhysicalEnhancementsAbility;
import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.abilities.core.interaction.InteractionHandler;
import de.jakob.lotm.damage.ModDamageTypes;
import de.jakob.lotm.network.PacketHandler;
import de.jakob.lotm.network.packets.toClient.SyncCullAbilityPacket;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.data.Location;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.AllyUtil;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class NationOfTheDeadAbility extends Ability {

    private static final int RADIUS = 15;
    private static final int DURATION_TICKS = 20 * 100; // 1 minute 40 seconds

    // Damage per second at same sequence: 1% of max HP
    private static final float BASE_DPS_PERCENT = 0.03f;
    // Per sequence difference adjustment
    private static final float PER_SEQ_STEP = 0.005f;

    private static final DustParticleOptions DEATH_DUST =
            new DustParticleOptions(new Vector3f(0.05f, 0.0f, 0.1f), 1.8f);

    public NationOfTheDeadAbility(String id) {
        super(id, 180f, "death");
        canBeCopied = false;
        canBeReplicated = false;
        cannotBeStolen = true;
        canBeUsedInArtifact = false;
        canBeShared = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("death", 2));
    }

    @Override
    protected float getSpiritualityCost() {
        return 8000;
    }

    @Override
    public void onAbilityUse(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        int casterSeq = BeyonderData.getSequence(entity);

        // Domain activation sound
        level.playSound(null, entity.blockPosition(),
                SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 3.0f, 0.4f);

        AtomicInteger ticks = new AtomicInteger(0);
        Set<UUID> overlayActive = new HashSet<>();
        AtomicReference<UUID> taskId = new AtomicReference<>();

        taskId.set(ServerScheduler.scheduleForDuration(0, 1, DURATION_TICKS, () -> {
            Vec3 center = entity.position();
            Location loc = new Location(center, serverLevel);

            // Only Flaring Sun, Pure White Light, Sword of Justice, Divine Kingdom Manifestation can cancel Nation of the Dead
            if (InteractionHandler.isInteractionPossibleStrictlyHigher(loc, "purification_holy", casterSeq, -1)) {
                overlayActive.forEach(uuid -> {
                    ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(uuid);
                    if (player != null) PacketHandler.sendToPlayer(player, new SyncCullAbilityPacket(false));
                });
                overlayActive.clear();
                ServerScheduler.cancel(taskId.get());
                return;
            }

            boolean unshadowedActive = false;

            // Visual: sphere of death particles every 10 ticks, ring every tick
            if (ticks.get() % 10 == 0) {
                ParticleUtil.spawnSphereParticles(serverLevel, ParticleTypes.SOUL, center, RADIUS, 80);
            }
            ParticleUtil.spawnCircleParticles(serverLevel, DEATH_DUST, center,
                    new Vec3(0, 1, 0), RADIUS, 24);

            Set<UUID> inRangeThisTick = new HashSet<>();

            AbilityUtil.getNearbyEntities(entity, serverLevel, center, RADIUS).forEach(target -> {
                if (AllyUtil.areAllies(entity, target)) return;

                int targetSeq = BeyonderData.getSequence(target);
                int seqDiff = targetSeq - casterSeq; // positive = target is weaker

                // Unshadowed Domain protects this target — skip all effects
                if (unshadowedActive) return;

                // Instant kill: target is 2+ sequences weaker
                if (seqDiff >= 2) {
                    ModDamageTypes.trueDamage(target, Float.MAX_VALUE, serverLevel, entity);
                    return;
                }

                // Black overlay for players inside the domain
                if (target instanceof ServerPlayer targetPlayer) {
                    inRangeThisTick.add(targetPlayer.getUUID());
                    if (!overlayActive.contains(targetPlayer.getUUID())) {
                        overlayActive.add(targetPlayer.getUUID());
                        PacketHandler.sendToPlayer(targetPlayer, new SyncCullAbilityPacket(true));
                    }
                }

                // Suppress regen while inside the domain (refreshed every tick)
                PhysicalEnhancementsAbility.suppressRegen(target, 2000);

                // Persistent debuffs while inside the domain (refreshed every tick)
                target.addEffect(new MobEffectInstance(MobEffects.WITHER,
                        40, 1, false, false, false));
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                        40, 0, false, false, false));

                // Damage once per second (every 20 ticks)
                if (ticks.get() % 20 != 0) return;

                // seqDiff > 0: target is weaker → more damage
                // seqDiff < 0: target is stronger → less damage
                float damagePercent = BASE_DPS_PERCENT + (seqDiff * PER_SEQ_STEP);

                if (damagePercent <= 0) return; // target too strong to be harmed

                float damage = target.getMaxHealth() * damagePercent;
                ModDamageTypes.trueDamage(target, damage, serverLevel, entity);
            });

            // Remove overlay for players who left the domain this tick
            overlayActive.removeIf(uuid -> {
                if (inRangeThisTick.contains(uuid)) return false;
                ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(uuid);
                if (player != null) PacketHandler.sendToPlayer(player, new SyncCullAbilityPacket(false));
                return true;
            });

            ticks.getAndIncrement();
        }, serverLevel));

        // Clean up overlay for all remaining players when duration ends
        overlayActive.forEach(uuid -> {
            ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(uuid);
            if (player != null) PacketHandler.sendToPlayer(player, new SyncCullAbilityPacket(false));
        });
    }
}
