package de.jakob.lotm.abilities.death;

import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.damage.ModDamageTypes;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.AllyUtil;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class NationOfTheDeadAbility extends Ability {

    private static final int RADIUS = 15;
    private static final int DURATION_TICKS = 20 * 100; // 1 minute 40 seconds
    private static final int INSTANT_KILL_THRESHOLD = 5; // weaker than seq 5 → instant kill

    // Damage per second at same sequence: 1% of max HP
    private static final float BASE_DPS_PERCENT = 0.01f;
    // Per sequence difference adjustment
    private static final float PER_SEQ_STEP = 0.005f;

    private static final DustParticleOptions DEATH_DUST =
            new DustParticleOptions(new Vector3f(0.05f, 0.0f, 0.1f), 1.8f);

    public NationOfTheDeadAbility(String id) {
        super(id, 20 * 180f); // 3-minute cooldown
        canBeCopied = false;
        canBeReplicated = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("death", 2));
    }

    @Override
    protected float getSpiritualityCost() {
        return 3000;
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

        ServerScheduler.scheduleForDuration(0, 1, DURATION_TICKS, () -> {
            Vec3 center = entity.position();

            // Visual: sphere of death particles every 10 ticks, ring every tick
            if (ticks.get() % 10 == 0) {
                ParticleUtil.spawnSphereParticles(serverLevel, ParticleTypes.SOUL, center, RADIUS, 80);
            }
            ParticleUtil.spawnCircleParticles(serverLevel, DEATH_DUST, center,
                    new Vec3(0, 1, 0), RADIUS, 24);

            AbilityUtil.getNearbyEntities(entity, serverLevel, center, RADIUS).forEach(target -> {
                if (AllyUtil.areAllies(entity, target)) return;

                int targetSeq = BeyonderData.getSequence(target);

                // Instant kill: weaker than seq 5 (seq > 5, i.e. 6, 7, 8, 9)
                if (targetSeq > INSTANT_KILL_THRESHOLD) {
                    target.hurt(
                            ModDamageTypes.source(serverLevel, ModDamageTypes.BEYONDER_GENERIC, entity),
                            Float.MAX_VALUE);
                    return;
                }

                // Persistent debuffs while inside the domain (refreshed every tick)
                target.addEffect(new MobEffectInstance(MobEffects.WITHER,
                        40, 1, false, false, false));
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                        40, 0, false, false, false));

                // Damage once per second (every 20 ticks)
                if (ticks.get() % 20 != 0) return;

                // seqDiff > 0: target is weaker → more damage
                // seqDiff < 0: target is stronger → less damage
                int seqDiff = targetSeq - casterSeq;
                float damagePercent = BASE_DPS_PERCENT + (seqDiff * PER_SEQ_STEP);

                if (damagePercent <= 0) return; // target too strong to be harmed

                float damage = target.getMaxHealth() * damagePercent;
                target.hurt(
                        ModDamageTypes.source(serverLevel, ModDamageTypes.BEYONDER_GENERIC, entity),
                        damage);
            });

            ticks.getAndIncrement();
        }, serverLevel);
    }
}
