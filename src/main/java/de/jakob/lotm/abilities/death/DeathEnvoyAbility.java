package de.jakob.lotm.abilities.death;

import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.effect.ModEffects;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.ParticleUtil;
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

public class DeathEnvoyAbility extends Ability {

    private static final int RADIUS = 10;
    private static final int DURATION = 20 * 20; // 20 seconds
    private static final int SPIRIT_CALLED_DURATION = 20 * 10; // 10 seconds

    private static final DustParticleOptions SOUL_DUST =
            new DustParticleOptions(new Vector3f(0.15f, 0.85f, 0.75f), 1.8f);
    private static final DustParticleOptions DARK_DUST =
            new DustParticleOptions(new Vector3f(0.05f, 0.0f, 0.2f), 1.4f);

    public DeathEnvoyAbility(String id) {
        super(id, 20f); // 1-second cooldown
        canBeCopied = false;
        canBeReplicated = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("death", 5));
    }

    @Override
    protected float getSpiritualityCost() {
        return 400;
    }

    @Override
    public void onAbilityUse(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        Vec3 center = entity.position().add(0, 0.5, 0);

        // --- Audible cry: ghostly wither + soul sounds ---
        level.playSound(null, entity.blockPosition(),
                SoundEvents.WITHER_AMBIENT, SoundSource.PLAYERS, 3.0f, 0.5f);
        level.playSound(null, entity.blockPosition(),
                SoundEvents.SOUL_ESCAPE.value(), SoundSource.PLAYERS, 2.5f, 0.7f);

        // --- Visual: expanding ring burst then floating particles ---
        ParticleUtil.spawnCircleParticles(serverLevel, SOUL_DUST,
                center, RADIUS, 80);
        ParticleUtil.spawnCircleParticles(serverLevel, DARK_DUST,
                center, RADIUS * 0.6, 50);
        ParticleUtil.spawnSphereParticles(serverLevel, ParticleTypes.SOUL,
                center, RADIUS, 120);

        // Inner soul fire burst at the caster
        for (int i = 0; i < 30; i++) {
            double ox = (random.nextDouble() - 0.5) * 2;
            double oy = random.nextDouble() * 2;
            double oz = (random.nextDouble() - 0.5) * 2;
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    center.x + ox, center.y + oy, center.z + oz,
                    1, 0, 0.05, 0, 0.02);
        }

        int casterSeq = BeyonderData.getSequence(entity);

        // --- Apply effects to all nearby entities ---
        for (LivingEntity target : AbilityUtil.getNearbyEntities(entity, serverLevel, center, RADIUS)) {
            int targetSeq = BeyonderData.getSequence(target);
            // Skip targets that are 2+ sequences stronger (lower sequence number)
            if (targetSeq - casterSeq <= -2) continue;

            // Weakness II
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,
                    DURATION, 1, false, true, true));
            // Slowness III
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                    DURATION, 2, false, true, true));
            // Freeze ticks (visual freezing)
            target.setTicksFrozen(target.getTicksRequiredToFreeze() + DURATION);
            // Spirit Called
            target.addEffect(new MobEffectInstance(ModEffects.SPIRIT_CALLED,
                    SPIRIT_CALLED_DURATION, 0, false, true, true));
        }
    }
}
