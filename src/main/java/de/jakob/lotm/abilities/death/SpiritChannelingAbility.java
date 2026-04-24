package de.jakob.lotm.abilities.death;

import com.google.common.util.concurrent.AtomicDouble;
import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.abilities.core.interaction.InteractionHandler;
import de.jakob.lotm.util.data.Location;
import de.jakob.lotm.damage.ModDamageTypes;
import de.jakob.lotm.network.PacketHandler;
import de.jakob.lotm.network.packets.toClient.SyncSpiritChannelingPacket;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.DamageLookup;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@EventBusSubscriber(modid = LOTMCraft.MOD_ID)

public class SpiritChannelingAbility extends SelectableAbility {

    public enum SpiritType { FROST_GHOST, EARTH_SPIRIT }

    /** Spirits captured per player (server-side). */
    private static final HashMap<UUID, SpiritType> capturedSpirits = new HashMap<>();

    /** Players who have the Glacial Aegis active (next hit negated). */
    private static final Set<UUID> glacialAegisActive = new HashSet<>();

    private static final String[] BASE_MODES = {
            "ability.lotmcraft.spirit_channeling.get_spirit",
            "ability.lotmcraft.spirit_channeling.release_spirit"
    };

    private static final String[] FROST_MODES = {
            "ability.lotmcraft.spirit_channeling.get_spirit",
            "ability.lotmcraft.spirit_channeling.release_spirit",
            "ability.lotmcraft.spirit_channeling.frozen_domain",
            "ability.lotmcraft.spirit_channeling.glacial_aegis"
    };

    private static final String[] EARTH_MODES = {
            "ability.lotmcraft.spirit_channeling.get_spirit",
            "ability.lotmcraft.spirit_channeling.release_spirit",
            "ability.lotmcraft.spirit_channeling.stone_restrainment",
            "ability.lotmcraft.spirit_channeling.earthen_fist",
            "ability.lotmcraft.spirit_channeling.quicksand",
            "ability.lotmcraft.spirit_channeling.earth_heal"
    };

    private static final DustParticleOptions FROST_DUST = new DustParticleOptions(new Vector3f(0.5f, 0.85f, 1.0f), 2.0f);
    private static final DustParticleOptions EARTH_DUST = new DustParticleOptions(new Vector3f(0.55f, 0.38f, 0.18f), 2.0f);
    private static final DustParticleOptions EARTH_DUST_SMALL = new DustParticleOptions(new Vector3f(0.55f, 0.38f, 0.18f), 1.2f);
    private static final DustParticleOptions STONE_DUST = new DustParticleOptions(new Vector3f(0.5f, 0.5f, 0.5f), 1.5f);

    public SpiritChannelingAbility(String id) {
        super(id, 20f);
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("death", 7));
    }

    @Override
    protected float getSpiritualityCost() {
        return 300;
    }

    @Override
    protected String[] getAbilityNames() {
        return BASE_MODES;
    }

    /** Returns the mode list for a specific player based on their captured spirit. */
    private String[] getAbilityNamesForPlayer(UUID uuid) {
        // On the client the server map is empty — use the synced ordinal from ClientSpiritCache instead
        if (capturedSpirits.isEmpty() && net.neoforged.fml.loading.FMLEnvironment.dist == net.neoforged.api.distmarker.Dist.CLIENT) {
            int ordinal = de.jakob.lotm.util.ClientSpiritCache.getSpiritTypeOrdinal();
            if (ordinal >= 0) {
                SpiritType[] values = SpiritType.values();
                if (ordinal < values.length) {
                    return switch (values[ordinal]) {
                        case FROST_GHOST -> FROST_MODES;
                        case EARTH_SPIRIT -> EARTH_MODES;
                    };
                }
            }
            return BASE_MODES;
        }
        SpiritType type = capturedSpirits.get(uuid);
        if (type == null) return BASE_MODES;
        return switch (type) {
            case FROST_GHOST -> FROST_MODES;
            case EARTH_SPIRIT -> EARTH_MODES;
        };
    }

    @Override
    public String[] getAbilityNamesCopy() {
        return getAbilityNamesForPlayer(null);
    }

    @Override
    public void onAbilityUse(Level level, LivingEntity entity) {
        if (level.isClientSide) return;

        if (InteractionHandler.isInteractionPossibleStrictlyHigher(new Location(entity.position(), (ServerLevel) level), "purification", de.jakob.lotm.util.BeyonderData.getSequence(entity), -1)) return;

        String[] names = getAbilityNamesForPlayer(entity.getUUID());
        int idx = selectedAbilities.getOrDefault(entity.getUUID(), 0);
        // Clamp index to current mode list size
        if (idx >= names.length) {
            idx = 0;
            selectedAbilities.put(entity.getUUID(), 0);
        }
        castSelectedAbility(level, entity, idx);
    }

    @Override
    protected void castSelectedAbility(Level level, LivingEntity entity, int abilityIndex) {
        if (level.isClientSide) return;

        String[] names = getAbilityNamesForPlayer(entity.getUUID());
        if (abilityIndex >= names.length) return;
        String mode = names[abilityIndex];

        switch (mode) {
            case "ability.lotmcraft.spirit_channeling.get_spirit" -> getSpirit(level, entity);
            case "ability.lotmcraft.spirit_channeling.release_spirit" -> releaseSpirit(level, entity);
            case "ability.lotmcraft.spirit_channeling.frozen_domain" -> frozenDomain(level, entity);
            case "ability.lotmcraft.spirit_channeling.glacial_aegis" -> glacialAegis(level, entity);
            case "ability.lotmcraft.spirit_channeling.stone_restrainment" -> stoneRestrainment(level, entity);
            case "ability.lotmcraft.spirit_channeling.earthen_fist" -> earthenFist(level, entity);
            case "ability.lotmcraft.spirit_channeling.quicksand" -> quicksand(level, entity);
            case "ability.lotmcraft.spirit_channeling.earth_heal" -> earthHeal(level, entity);
        }
    }

    // -------------------------------------------------------------------------
    // nextAbility / previousAbility overrides to use per-player mode list
    // -------------------------------------------------------------------------

    @Override
    public void nextAbility(LivingEntity entity) {
        String[] names = getAbilityNamesForPlayer(entity.getUUID());
        if (names.length == 0) return;

        int idx = selectedAbilities.getOrDefault(entity.getUUID(), 0);
        idx = (idx + 1) % names.length;
        selectedAbilities.put(entity.getUUID(), idx);
        // Sync to server
        de.jakob.lotm.network.PacketHandler.sendToServer(new de.jakob.lotm.network.packets.toServer.AbilitySelectionPacket(getId(), idx));
    }

    @Override
    public void previousAbility(LivingEntity entity) {
        String[] names = getAbilityNamesForPlayer(entity.getUUID());
        if (names.length == 0) return;

        int idx = selectedAbilities.getOrDefault(entity.getUUID(), 0);
        idx = (idx - 1 + names.length) % names.length;
        selectedAbilities.put(entity.getUUID(), idx);
        de.jakob.lotm.network.PacketHandler.sendToServer(new de.jakob.lotm.network.packets.toServer.AbilitySelectionPacket(getId(), idx));
    }

    @Override
    public String getSelectedAbility(LivingEntity entity) {
        String[] names = getAbilityNamesForPlayer(entity.getUUID());
        if (names.length == 0) return "";
        int idx = selectedAbilities.getOrDefault(entity.getUUID(), 0);
        if (idx >= names.length) idx = 0;
        return names[idx];
    }

    @Override
    public void setSelectedAbility(ServerPlayer player, int selectedAbility) {
        String[] names = getAbilityNamesForPlayer(player.getUUID());
        if (names.length == 0) return;
        if (selectedAbility < 0 || selectedAbility >= names.length) return;
        selectedAbilities.put(player.getUUID(), selectedAbility);
    }

    // -------------------------------------------------------------------------
    // Core: Get / Release Spirit
    // -------------------------------------------------------------------------

    private void getSpirit(Level level, LivingEntity entity) {
        if (capturedSpirits.containsKey(entity.getUUID())) {
            AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.spirit_channeling.already_have").withColor(0xFF334f23));
            return;
        }

        // 75% at sequence 6, 50% otherwise
        int seq = de.jakob.lotm.util.BeyonderData.getSequence(entity);
        float successChance = (seq <= 6) ? 0.75f : 0.50f;
        if (random.nextFloat() >= successChance) {
            AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.spirit_channeling.failed").withColor(0xFF334f23));
            return;
        }

        SpiritType type = random.nextBoolean() ? SpiritType.FROST_GHOST : SpiritType.EARTH_SPIRIT;
        capturedSpirits.put(entity.getUUID(), type);

        if (entity instanceof net.minecraft.server.level.ServerPlayer sp) {
            PacketHandler.sendToPlayer(sp, new SyncSpiritChannelingPacket(type.ordinal()));
        }

        String nameKey = type == SpiritType.FROST_GHOST
                ? "ability.lotmcraft.spirit_channeling.got_frost_ghost"
                : "ability.lotmcraft.spirit_channeling.got_earth_spirit";
        AbilityUtil.sendActionBar(entity, Component.translatable(nameKey).withColor(0xFF334f23));
    }

    private void releaseSpirit(Level level, LivingEntity entity) {
        SpiritType type = capturedSpirits.remove(entity.getUUID());
        // Reset selection so they don't land on a now-invalid index
        selectedAbilities.put(entity.getUUID(), 0);

        if (entity instanceof net.minecraft.server.level.ServerPlayer sp) {
            PacketHandler.sendToPlayer(sp, new SyncSpiritChannelingPacket(-1));
        }

        if (type == null) {
            AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.spirit_channeling.no_captured").withColor(0xFF334f23));
            return;
        }

        String spiritNameKey = "ability.lotmcraft.spirit_channeling.spirit_name." + type.name().toLowerCase();
        AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.spirit_channeling.released", Component.translatable(spiritNameKey)).withColor(0xFF334f23));
    }

    // -------------------------------------------------------------------------
    // Frost Ghost abilities
    // -------------------------------------------------------------------------

    private void frozenDomain(Level level, LivingEntity entity) {
        if (level.isClientSide) return;

        int casterSeq = de.jakob.lotm.util.BeyonderData.getSequence(entity);
        Vec3 startPos = entity.position();
        AtomicDouble radius = new AtomicDouble(0.5);

        ServerScheduler.scheduleForDuration(0, 2, 20 * 3, () -> {
            ParticleUtil.spawnParticles((ServerLevel) level, FROST_DUST, startPos.add(0, 1, 0), 60, radius.get(), 0.3, radius.get(), 0);
            ParticleUtil.spawnParticles((ServerLevel) level, ParticleTypes.SNOWFLAKE, startPos.add(0, 0.5, 0), 30, radius.get(), 0.2, radius.get(), 0);

            for (LivingEntity nearby : AbilityUtil.getNearbyEntities(entity, (ServerLevel) level, startPos, radius.get() + 0.5)) {
                int targetSeq = de.jakob.lotm.util.BeyonderData.getSequence(nearby);
                if (targetSeq <= casterSeq - 1) continue;

                nearby.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 100, false, false, false));
                nearby.addEffect(new MobEffectInstance(MobEffects.JUMP, 60, 128, false, false, false));
                nearby.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 5, 2, false, false, false));
                nearby.setTicksFrozen(nearby.getTicksRequiredToFreeze() + 40);
            }

            radius.addAndGet(0.5);
        }, null, (ServerLevel) level, () -> 1.0);
    }

    private void glacialAegis(Level level, LivingEntity entity) {
        if (level.isClientSide) return;

        if (glacialAegisActive.contains(entity.getUUID())) {
            AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.spirit_channeling.aegis_already_active").withColor(0xFF334f23));
            return;
        }

        glacialAegisActive.add(entity.getUUID());

        // Visual: ring of frost particles around the player
        ServerScheduler.scheduleForDuration(0, 1, 20 * 10, () -> {
            if (!glacialAegisActive.contains(entity.getUUID())) return;
            Vec3 pos = entity.position().add(0, 1, 0);
            for (int i = 0; i < 8; i++) {
                double angle = (2 * Math.PI * i) / 8;
                Vec3 particlePos = pos.add(Math.cos(angle) * 0.8, 0, Math.sin(angle) * 0.8);
                ParticleUtil.spawnParticles((ServerLevel) level, FROST_DUST, particlePos, 1, 0.05);
            }
        }, () -> glacialAegisActive.remove(entity.getUUID()), (ServerLevel) level, () -> 1.0);

        AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.spirit_channeling.aegis_active").withColor(0xFF334f23));
    }

    // -------------------------------------------------------------------------
    // Earth Spirit abilities
    // -------------------------------------------------------------------------

    private void stoneRestrainment(Level level, LivingEntity entity) {
        if (level.isClientSide) return;

        LivingEntity target = AbilityUtil.getTargetEntity(entity, 20, 1.5f);
        if (target == null) {
            AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.spirit_channeling.no_target").withColor(0xFF334f23));
            return;
        }

        int casterSeq = de.jakob.lotm.util.BeyonderData.getSequence(entity);
        int targetSeq = de.jakob.lotm.util.BeyonderData.getSequence(target);
        if (targetSeq <= casterSeq - 1) return;

        // Encased in stone: cannot move, takes suffocation-style damage
        AtomicBoolean done = new AtomicBoolean(false);

        ServerScheduler.scheduleForDuration(0, 2, 20 * 4* (int) Math.max(multiplier(entity)/4,1), () -> {
            if (target.isDeadOrDying()) {
                done.set(true);
                return;
            }

            // Prevent movement
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 100, false, false, false));
            target.addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 128, false, false, false));

            // Suffocation damage every 2 ticks (same as in wall)
            target.hurt(ModDamageTypes.source(level, ModDamageTypes.BEYONDER_GENERIC, entity), 1.0f);
            target.invulnerableTime = 0;

            // Stone particles around target
            ParticleUtil.spawnParticles((ServerLevel) level, STONE_DUST, target.position().add(0, target.getEyeHeight() / 2, 0),
                    10, 0.4, target.getEyeHeight() / 2, 0.4, 0);
            ParticleUtil.spawnParticles((ServerLevel) level, EARTH_DUST_SMALL, target.position().add(0, target.getEyeHeight() / 2, 0),
                    6, 0.3, target.getEyeHeight() / 2, 0.3, 0);
        }, null, (ServerLevel) level, () -> 1.0);
    }

    private void earthenFist(Level level, LivingEntity entity) {
        if (level.isClientSide) return;

        Vec3 startPos = entity.getEyePosition();
        Vec3 direction = entity.getLookAngle().normalize();
        AtomicBoolean hasHit = new AtomicBoolean(false);

        // Launch two fists with slight left/right spread using the right vector
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right = new Vec3(
                direction.z * up.y - direction.y * up.z,
                direction.x * up.z - direction.z * up.x,
                direction.y * up.x - direction.x * up.y
        ).normalize();

        for (int fist = 0; fist < 2; fist++) {
            double spread = (fist == 0) ? -0.15 : 0.15;
            Vec3 fistDir = direction.add(right.scale(spread)).normalize();
            final Vec3 finalFistDir = fistDir;

            final int[] tick = {0};
            Vec3 initialPos = startPos.add(finalFistDir.scale(1.5));
            final Vec3[] currentPos = {initialPos};

            ServerScheduler.scheduleForDuration(0, 1, 40, () -> {
                if (hasHit.get()) return;
                tick[0]++;

                Vec3 pos = currentPos[0];

                // Earth particles forming fist shape
                ParticleUtil.spawnParticles((ServerLevel) level, EARTH_DUST, pos, 8, 0.3);
                ParticleUtil.spawnParticles((ServerLevel) level, STONE_DUST, pos, 4, 0.2);

                // Check for hit
                if (AbilityUtil.damageNearbyEntities((ServerLevel) level, entity, 1.2f*(int) Math.max(multiplier(entity)/4,1),
                        DamageLookup.lookupDamage(7, 0.5)*(int) Math.max(multiplier(entity)/4,1), pos, true, false,
                        ModDamageTypes.source(level, ModDamageTypes.BEYONDER_GENERIC, entity))) {
                    hasHit.set(true);
                    // Impact burst
                    ParticleUtil.spawnParticles((ServerLevel) level, EARTH_DUST, pos, 30, 0.5);
                    ParticleUtil.spawnParticles((ServerLevel) level, ParticleTypes.EXPLOSION, pos, 3, 0.3);
                    return;
                }

                // Stop at solid blocks
                if (!level.getBlockState(net.minecraft.core.BlockPos.containing(pos)).isAir()) {
                    hasHit.set(true);
                    ParticleUtil.spawnParticles((ServerLevel) level, EARTH_DUST, pos, 20, 0.4);
                    return;
                }

                currentPos[0] = pos.add(finalFistDir.scale(0.8));
            }, null, (ServerLevel) level, () -> 1.0);
        }
    }

    private void quicksand(Level level, LivingEntity entity) {
        if (level.isClientSide) return;

        LivingEntity target = AbilityUtil.getTargetEntity(entity, 25*(int) Math.max(multiplier(entity)/4,1), 1.5f);
        if (target == null) {
            AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.spirit_channeling.no_target").withColor(0xFF334f23));
            return;
        }

        int casterSeq = de.jakob.lotm.util.BeyonderData.getSequence(entity);
        Vec3 center = target.position().add(0, 0.25, 0);

        ServerScheduler.scheduleForDuration(0, 2, 200, () -> {
            if (target.isDeadOrDying()) return;

            // Swirling earth/sand particles on the ground around target
            for (int i = 0; i < 12; i++) {
                double angle = (2 * Math.PI * i) / 12 + (level.getGameTime() * 0.05);
                double r = 2.0 + random.nextDouble() * 2.0;
                Vec3 particlePos = center.add(Math.cos(angle) * r, 0, Math.sin(angle) * r);
                ParticleUtil.spawnParticles((ServerLevel) level, EARTH_DUST_SMALL, particlePos, 2, 0.1);
            }

            // Heavy slowness on entities in the area (radius 5), skip 2+ sequences stronger
            for (LivingEntity nearby : AbilityUtil.getNearbyEntities(entity, (ServerLevel) level, center, 5.0*(int) Math.max(multiplier(entity)/4,1))) {
                int targetSeq = de.jakob.lotm.util.BeyonderData.getSequence(nearby);
                if (targetSeq <= casterSeq - 1) continue;

                nearby.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 4, false, false, false));
                // Sink into ground
                if (level.getBlockState(net.minecraft.core.BlockPos.containing(
                        nearby.getX(), nearby.getY() + 0.75, nearby.getZ())).isAir()) {
                    nearby.setDeltaMovement(nearby.getDeltaMovement().x, -0.1, nearby.getDeltaMovement().z);
                }
            }
        }, null, (ServerLevel) level, () -> 1.0);
    }

    private void earthHeal(Level level, LivingEntity entity) {
        if (level.isClientSide) return;

        float maxHealth = entity.getMaxHealth();
        float healAmount = maxHealth * 0.10f;
        entity.heal(healAmount);

        // Particle visual
        Vec3 pos = entity.position().add(0, entity.getEyeHeight() / 2, 0);
        ParticleUtil.spawnParticles((ServerLevel) level, EARTH_DUST, pos, 25, 0.4, entity.getEyeHeight() / 2, 0.4, 0);
        ParticleUtil.spawnParticles((ServerLevel) level, ParticleTypes.HEART, pos.add(0, 0.5, 0), 5, 0.3);
    }

    @SubscribeEvent
    public static void onGlacialAegisBlock(LivingIncomingDamageEvent event) {
        if (glacialAegisActive.contains(event.getEntity().getUUID())) {
            glacialAegisActive.remove(event.getEntity().getUUID());
            event.setCanceled(true);
        }
    }

    public static boolean hasCapturedSpirit(UUID playerUUID) {
        return capturedSpirits.containsKey(playerUUID);
    }

    public static SpiritType getCapturedSpiritType(UUID playerUUID) {
        return capturedSpirits.get(playerUUID);
    }
}
