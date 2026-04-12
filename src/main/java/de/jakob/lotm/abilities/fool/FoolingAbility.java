package de.jakob.lotm.abilities.fool;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.effect.ModEffects;
import de.jakob.lotm.util.helper.AbilityUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

/**
 * Fooling — Fool Sequence 0 exclusive ability.
 *
 * Blasts every entity within 15 blocks with the Fooling status effect for
 * 5 minutes (6 000 ticks).  While Fooling is active:
 *   - Every 3rd ability input is silently ignored.
 *   - Movement / view is continuously inverted (front ↔ back, left ↔ right).
 *   - Ability-bar hotkeys trigger a random ability from the target's own
 *     pathway instead of the one they have assigned.
 */
public class FoolingAbility extends Ability {

    private static final int EFFECT_DURATION_TICKS = 20 * 60 * 5; // 5 minutes
    private static final double AOE_RADIUS = 15.0;

    public FoolingAbility(String id) {
        super(id, 30f); // 30 second cooldown
        hasOptimalDistance = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        // Sequence 0 means the requirement value is 0 — only Fool Sequence 0
        // satisfies the check in Ability#hasAbility (getRequirements().get("fool") >= sequence).
        return new HashMap<>(Map.of("fool", 0));
    }

    @Override
    public float getSpiritualityCost() {
        return 50;
    }

    @Override
    public void onAbilityUse(Level level, LivingEntity entity) {
        if (level.isClientSide) return;

        ServerLevel serverLevel = (ServerLevel) level;

        // Visual + audio feedback
        serverLevel.playSound(null,
                entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.AMETHYST_CLUSTER_BREAK,
                entity.getSoundSource(), 1.5f, 0.6f);

        // Spawn a flash of jester-themed particles around the caster
        AbilityUtil.addPotionEffectToNearbyEntities(
                serverLevel,
                entity,
                AOE_RADIUS,
                entity.position(),
                new MobEffectInstance(ModEffects.FOOLING, EFFECT_DURATION_TICKS, 0, false, true, true)
        );

        // Decorative particle ring so players can see the AoE boundary
        double radius = AOE_RADIUS;
        int particleCount = 80;
        for (int i = 0; i < particleCount; i++) {
            double angle = (2 * Math.PI / particleCount) * i;
            double px = entity.getX() + radius * Math.cos(angle);
            double pz = entity.getZ() + radius * Math.sin(angle);
            serverLevel.sendParticles(
                    ParticleTypes.ENCHANT,
                    px, entity.getY() + 1.0, pz,
                    3, 0, 0.5, 0, 0.05
            );
        }
    }
}
