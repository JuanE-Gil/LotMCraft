package de.jakob.lotm.abilities.death;

import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.entity.ModEntities;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.helper.subordinates.SubordinateUtils;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DoorToTheUnderworldAbility extends Ability {

    private static final int DURATION_TICKS = 20 * 60; // 1 minute
    // Spawn one wave every 4 seconds, so 15 waves over 60 seconds
    private static final int SPAWN_INTERVAL = 20 * 4;
    private static final double PORTAL_RADIUS = 3.5;
    private static final double PORTAL_HEIGHT = 5.0;

    private static final DustParticleOptions SOUL_DUST =
            new DustParticleOptions(new Vector3f(0.15f, 0.85f, 0.75f), 1.5f);
    private static final DustParticleOptions DARK_DUST =
            new DustParticleOptions(new Vector3f(0.05f, 0.0f, 0.15f), 2.0f);

    public DoorToTheUnderworldAbility(String id) {
        super(id, 20 * 60 * 5f); // 5-minute cooldown
        canBeCopied = false;
        canBeReplicated = false;
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
    public void onAbilityUse(Level level, LivingEntity entity) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!(entity instanceof ServerPlayer player)) return;

        // Place the portal 3 blocks in front of the caster
        Vec3 look = entity.getLookAngle().normalize();
        Vec3 portalCenter = entity.position()
                .add(look.x * 4, 0, look.z * 4)
                .add(0, 1, 0); // lift slightly off ground

        AtomicBoolean done = new AtomicBoolean(false);
        AtomicInteger spawnTick = new AtomicInteger(0);
        Location portalLocation = new Location(portalCenter, serverLevel);

        // Main loop: visual + spawn
        ServerScheduler.scheduleForDuration(0, 1, DURATION_TICKS, () -> {
            int tick = spawnTick.getAndIncrement();

            // --- Portal visuals every tick ---
            drawPortal(serverLevel, portalCenter, tick);

            // --- Spawn wave every SPAWN_INTERVAL ticks ---
            if (tick % SPAWN_INTERVAL == 0) {
                spawnWave(serverLevel, player, portalCenter);
            }

        }, null, serverLevel, () -> 1.0);
    }

    // -------------------------------------------------------------------------
    // Visual: large oval portal made of concentric rings + inner fill
    // -------------------------------------------------------------------------

    private void drawPortal(ServerLevel level, Vec3 center, int tick) {
        // Outer ring (XZ plane circle at multiple heights — forms oval)
        for (double t = 0; t < Math.PI * 2; t += Math.PI / 24) {
            double x = Math.cos(t) * PORTAL_RADIUS;
            double z = Math.sin(t) * PORTAL_RADIUS;

            // Bottom, middle, and top rings
            for (double yOff : new double[]{0, PORTAL_HEIGHT / 2.0, PORTAL_HEIGHT}) {
                Vec3 ringPos = center.add(x, yOff, z);
                level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        ringPos.x, ringPos.y, ringPos.z, 1, 0, 0, 0, 0);
            }

            // Vertical edge pillars
            double yOff = (tick % 4) * (PORTAL_HEIGHT / 4.0);
            Vec3 edgePos = center.add(x, yOff, z);
            level.sendParticles(ParticleTypes.SOUL,
                    edgePos.x, edgePos.y, edgePos.z, 1, 0, 0.05, 0, 0.02);
        }

        // Inner swirling fill — portal core
        double swirl = tick * 0.15;
        for (int i = 0; i < 20; i++) {
            double angle = swirl + (Math.PI * 2 * i) / 20.0;
            double r = random.nextDouble() * (PORTAL_RADIUS - 0.5);
            double yFill = random.nextDouble() * PORTAL_HEIGHT;
            Vec3 fillPos = center.add(
                    Math.cos(angle) * r,
                    yFill,
                    Math.sin(angle) * r
            );
            level.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    fillPos.x, fillPos.y, fillPos.z, 1, 0, 0, 0, 0.05);
        }

        // Occasional smoke puffs at the base
        if (tick % 5 == 0) {
            for (int i = 0; i < 4; i++) {
                double angle = random.nextDouble() * Math.PI * 2;
                double r = random.nextDouble() * PORTAL_RADIUS;
                Vec3 smokePos = center.add(Math.cos(angle) * r, 0, Math.sin(angle) * r);
                level.sendParticles(ParticleTypes.LARGE_SMOKE,
                        smokePos.x, smokePos.y, smokePos.z, 1, 0, 0.1, 0, 0.02);
            }
        }

        // Teal soul dust ring at mid-height for eerie glow
        if (tick % 3 == 0) {
            ParticleUtil.spawnCircleParticles(level, SOUL_DUST,
                    center.add(0, PORTAL_HEIGHT / 2.0, 0), PORTAL_RADIUS + 0.3, 16);
        }
    }

    // -------------------------------------------------------------------------
    // Spawn: one undead mob + one spirit per wave, from inside the portal
    // -------------------------------------------------------------------------

    private void spawnWave(ServerLevel level, ServerPlayer player, Vec3 portalCenter) {
        // Pick a random spawn point inside the portal oval
        spawnMob(level, player, portalCenter, pickUndead(level));
        spawnMob(level, player, portalCenter, pickSpirit(level));
    }

    private void spawnMob(ServerLevel level, ServerPlayer player, Vec3 portalCenter, Mob mob) {
        if (mob == null) return;

        double angle = random.nextDouble() * Math.PI * 2;
        double r = random.nextDouble() * (PORTAL_RADIUS - 0.5);
        double y = random.nextDouble() * PORTAL_HEIGHT;

        Vec3 spawnPos = portalCenter.add(Math.cos(angle) * r, y, Math.sin(angle) * r);

        // Find ground below spawn point
        net.minecraft.core.BlockPos blockPos = net.minecraft.core.BlockPos.containing(spawnPos);
        while (blockPos.getY() > level.getMinBuildHeight() && level.getBlockState(blockPos).isAir()) {
            blockPos = blockPos.below();
        }
        Vec3 groundPos = Vec3.atBottomCenterOf(blockPos.above());

        mob.setPos(groundPos.x, groundPos.y, groundPos.z);
        level.addFreshEntity(mob);

        // Make it follow and fight for the player (tamed-wolf AI)
        SubordinateUtils.turnEntityIntoSubordinate(mob, player);

        // Spawn effect particles
        ParticleUtil.spawnSphereParticles(level, DARK_DUST, groundPos.add(0, 1, 0), 0.8, 20);
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                groundPos.x, groundPos.y + 0.5, groundPos.z, 8, 0.3, 0.4, 0.3, 0.05);
    }

    /** Picks a random vanilla undead mob type. */
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

    /** Picks a random spirit entity from the mod's spirit roster. */
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
