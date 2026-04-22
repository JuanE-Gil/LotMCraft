package de.jakob.lotm.abilities.fool;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.attachments.FoolingComponent;
import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.rendering.effectRendering.EffectManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

import java.util.HashMap;
import java.util.Map;


public class FoolingAbility extends Ability {

    private static final int EFFECT_DURATION_TICKS = 20 * 60 * 5; // 5 minutes
    private static final double AOE_RADIUS = 15.0;

    public FoolingAbility(String id) {
        super(id, 30f); // 30 second cooldown
        hasOptimalDistance = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
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

        serverLevel.playSound(null,
                entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.AMETHYST_CLUSTER_BREAK,
                entity.getSoundSource(), 1.5f, 0.6f);

        List<LivingEntity> nearby = serverLevel.getEntitiesOfClass(
                LivingEntity.class,
                entity.getBoundingBox().inflate(AOE_RADIUS),
                e -> e != entity
        );
        for (LivingEntity target : nearby) {
            FoolingComponent component = target.getData(ModAttachments.FOOLING_COMPONENT);
            component.setTicksRemaining(EFFECT_DURATION_TICKS);
        }

        if(entity instanceof ServerPlayer player)
            EffectManager.playEffect(EffectManager.Effect.FOOLING, entity.getX(), entity.getY(), entity.getZ(), player);

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
