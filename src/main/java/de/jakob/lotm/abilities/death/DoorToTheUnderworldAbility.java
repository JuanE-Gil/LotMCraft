package de.jakob.lotm.abilities.death;

import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.abilities.core.interaction.InteractionHandler;
import de.jakob.lotm.entity.ModEntities;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.data.Location;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.helper.subordinates.SubordinateUtils;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class DoorToTheUnderworldAbility extends SelectableAbility {

    private static final int DURATION_TICKS = 20 * 60; // 1 minute
    private static final int SPAWN_INTERVAL = 20 * 4;
    private static final double PORTAL_RADIUS = 3.5;

    private static final String[] MODES = {
            "ability.lotmcraft.door_to_the_underworld.open",
            "ability.lotmcraft.door_to_the_underworld.release"
    };

    /** Tracks all mobs summoned per player so they can be despawned. */
    private static final HashMap<UUID, List<Mob>> summonedMobs = new HashMap<>();

    /** Tracks the active portal scheduler task per player so it can be cancelled. */
    private static final HashMap<UUID, UUID> activePortalTasks = new HashMap<>();

    private static final DustParticleOptions SOUL_DUST =
            new DustParticleOptions(new Vector3f(0.15f, 0.85f, 0.75f), 1.5f);
    private static final DustParticleOptions DARK_DUST =
            new DustParticleOptions(new Vector3f(0.05f, 0.0f, 0.15f), 2.0f);

    public DoorToTheUnderworldAbility(String id) {
        super(id, 10f, "death");
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("death", 5));
    }

    @Override
    protected float getSpiritualityCost() {
        return 600;
    }

    @Override
    protected String[] getAbilityNames() {
        return MODES;
    }

    @Override
    public void useAbility(ServerLevel serverLevel, LivingEntity entity, boolean consumeSpirituality, boolean hasToHaveAbility, boolean hasToMeetRequirements) {
        // Release sub-ability bypasses cooldown and spirituality cost
        if (getSelectedAbilityIndex(entity.getUUID()) == 1) {
            onAbilityUse(serverLevel, entity);
            return;
        }
        super.useAbility(serverLevel, entity, consumeSpirituality, hasToHaveAbility, hasToMeetRequirements);
    }

    @Override
    protected void castSelectedAbility(Level level, LivingEntity entity, int selectedAbility) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!(entity instanceof ServerPlayer player)) return;

        switch (selectedAbility) {
            case 0 -> {
                if (InteractionHandler.isInteractionPossibleStrictlyHigher(new Location(entity.position(), serverLevel), "purification", BeyonderData.getSequence(entity), -1)) return;
                openPortal(serverLevel, player);
            }
            case 1 -> release(player);
        }
    }

    private void openPortal(ServerLevel serverLevel, ServerPlayer player) {
        Vec3 look = new Vec3(player.getLookAngle().x, 0, player.getLookAngle().z).normalize();
        Vec3 portalCenter = player.position()
                .add(look.x * 4, PORTAL_RADIUS, look.z * 4);

        // right = perpendicular to look in the horizontal plane; up = world up
        Vec3 right = new Vec3(-look.z, 0, look.x);
        Vec3 up = new Vec3(0, 1, 0);

        AtomicInteger spawnTick = new AtomicInteger(0);

        UUID taskId = ServerScheduler.scheduleForDuration(0, 1, DURATION_TICKS, () -> {
            int tick = spawnTick.getAndIncrement();
            drawPortal(serverLevel, portalCenter, right, up, tick);

            if (tick % SPAWN_INTERVAL == 0) {
                spawnWave(serverLevel, player, portalCenter);
            }
        }, () -> activePortalTasks.remove(player.getUUID()), serverLevel, () -> 1.0);

        activePortalTasks.put(player.getUUID(), taskId);
    }

    private void release(ServerPlayer player) {
        UUID taskId = activePortalTasks.remove(player.getUUID());
        List<Mob> mobs = summonedMobs.remove(player.getUUID());

        if (taskId == null && (mobs == null || mobs.isEmpty())) {
            AbilityUtil.sendActionBar(player, Component.translatable("ability.lotmcraft.door_to_the_underworld.none_summoned").withColor(0xFF334f23));
            return;
        }

        if (taskId != null) {
            ServerScheduler.cancel(taskId);
        }

        if (mobs != null) {
            for (Mob mob : mobs) {
                if (mob.isAlive()) mob.discard();
            }
        }

        AbilityUtil.sendActionBar(player, Component.translatable("ability.lotmcraft.door_to_the_underworld.released").withColor(0xFF334f23));
    }

    // -------------------------------------------------------------------------
    // Visual
    // -------------------------------------------------------------------------

    private void drawPortal(ServerLevel level, Vec3 center, Vec3 right, Vec3 up, int tick) {
        // Outer rim — soul fire flames around the circle edge
        int rimPoints = 48;
        for (int i = 0; i < rimPoints; i++) {
            double t = (2 * Math.PI * i) / rimPoints;
            double cosT = Math.cos(t) * PORTAL_RADIUS;
            double sinT = Math.sin(t) * PORTAL_RADIUS;
            Vec3 rimPos = center
                    .add(right.scale(cosT))
                    .add(up.scale(sinT));
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    rimPos.x, rimPos.y, rimPos.z, 1, 0, 0, 0, 0);
        }

        // Animated swirling inner fill — reverse portal particles
        double swirl = tick * 0.12;
        for (int i = 0; i < 20; i++) {
            double angle = swirl + (2 * Math.PI * i) / 20.0;
            double r = random.nextDouble() * (PORTAL_RADIUS - 0.3);
            Vec3 fillPos = center
                    .add(right.scale(Math.cos(angle) * r))
                    .add(up.scale(Math.sin(angle) * r));
            level.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    fillPos.x, fillPos.y, fillPos.z, 1, 0, 0, 0, 0.04);
        }

        // Soul particles drifting upward along the rim
        if (tick % 2 == 0) {
            double t = random.nextDouble() * 2 * Math.PI;
            Vec3 soulPos = center
                    .add(right.scale(Math.cos(t) * PORTAL_RADIUS))
                    .add(up.scale(Math.sin(t) * PORTAL_RADIUS));
            level.sendParticles(ParticleTypes.SOUL,
                    soulPos.x, soulPos.y, soulPos.z, 1, 0, 0.05, 0, 0.02);
        }

        // Teal dust ring slightly outside the rim
        if (tick % 3 == 0) {
            int dustPoints = 16;
            for (int i = 0; i < dustPoints; i++) {
                double t = (2 * Math.PI * i) / dustPoints;
                double r = PORTAL_RADIUS + 0.3;
                Vec3 dustPos = center
                        .add(right.scale(Math.cos(t) * r))
                        .add(up.scale(Math.sin(t) * r));
                level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        dustPos.x, dustPos.y, dustPos.z, 1, 0, 0, 0, 0);
            }
        }

        // Occasional smoke at the base of the portal
        if (tick % 5 == 0) {
            Vec3 base = center.add(up.scale(-PORTAL_RADIUS));
            for (int i = 0; i < 3; i++) {
                Vec3 smokePos = base.add(right.scale((random.nextDouble() - 0.5) * PORTAL_RADIUS));
                level.sendParticles(ParticleTypes.LARGE_SMOKE,
                        smokePos.x, smokePos.y, smokePos.z, 1, 0, 0.05, 0, 0.01);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Spawn
    // -------------------------------------------------------------------------

    private void spawnWave(ServerLevel level, ServerPlayer player, Vec3 portalCenter) {
        spawnMob(level, player, portalCenter, pickUndead(level));
        spawnMob(level, player, portalCenter, pickSpirit(level));
    }

    private void spawnMob(ServerLevel level, ServerPlayer player, Vec3 portalCenter, Mob mob) {
        if (mob == null) return;

        // Spread mobs horizontally around the portal base
        double angle = random.nextDouble() * Math.PI * 2;
        double r = random.nextDouble() * (PORTAL_RADIUS - 0.5);
        Vec3 spawnPos = portalCenter.add(Math.cos(angle) * r, 0, Math.sin(angle) * r);

        net.minecraft.core.BlockPos blockPos = net.minecraft.core.BlockPos.containing(spawnPos);
        while (blockPos.getY() > level.getMinBuildHeight() && level.getBlockState(blockPos).isAir()) {
            blockPos = blockPos.below();
        }
        Vec3 groundPos = Vec3.atBottomCenterOf(blockPos.above());

        mob.setPos(groundPos.x, groundPos.y, groundPos.z);
        level.addFreshEntity(mob);

        SubordinateUtils.turnEntityIntoSubordinate(mob, player, false);

        summonedMobs.computeIfAbsent(player.getUUID(), k -> new ArrayList<>()).add(mob);

        ParticleUtil.spawnSphereParticles(level, DARK_DUST, groundPos.add(0, 1, 0), 0.8, 20);
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                groundPos.x, groundPos.y + 0.5, groundPos.z, 8, 0.3, 0.4, 0.3, 0.05);
    }

    private Mob pickUndead(ServerLevel level) {
        return switch (random.nextInt(6)) {
            case 0 -> new Zombie(EntityType.ZOMBIE, level);
            case 1 -> new Skeleton(EntityType.SKELETON, level);
            case 2 -> new Husk(EntityType.HUSK, level);
            case 3 -> new Drowned(EntityType.DROWNED, level);
            case 4 -> new Stray(EntityType.STRAY, level);
            default -> new WitherSkeleton(EntityType.WITHER_SKELETON, level);
        };
    }

    private Mob pickSpirit(ServerLevel level) {
        return switch (random.nextInt(5)) {
            case 0 -> (Mob) ModEntities.SPIRIT_GHOST.get().create(level);
            case 1 -> (Mob) ModEntities.SPIRIT_DERVISH_ENTITY.get().create(level);
            case 2 -> (Mob) ModEntities.SPIRIT_BUBBLES_ENTITY.get().create(level);
            case 3 -> (Mob) ModEntities.SPIRIT_BLUE_WIZARD.get().create(level);
            default -> (Mob) ModEntities.SPIRIT_TRANSLUCENT_WIZARD.get().create(level);
        };
    }
}
