package de.jakob.lotm.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

/**
 * Fooling effect applied by the Fool sequence 0 ability.
 *
 * - Movement keys are inverted each tick: forward↔back, left↔right, jump↔sneak.
 * - Every 3rd distinct key press (any key) is suppressed entirely.
 * - Ability hotkeys trigger a random ability server-side instead of the bound one.
 *
 * Client-side behaviour lives in ClientFoolingCache.
 * Server-side input counter lives in ServerFoolingCache.
 */
public class FoolingEffect extends MobEffect {

    protected FoolingEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    private static final int STUN_INTERVAL_TICKS = 100; // every 5 seconds
    private static final int STUN_DURATION_TICKS  = 20;  // stun lasts 1 second

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.level().isClientSide) return true;

        MobEffectInstance instance = livingEntity.getEffect(ModEffects.FOOLING);
        if (instance != null && instance.getDuration() % STUN_INTERVAL_TICKS == 0) {
            livingEntity.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN, STUN_DURATION_TICKS, 254, false, false, false));
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
