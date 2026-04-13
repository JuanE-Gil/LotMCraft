package de.jakob.lotm.effect;

import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.damage.ModDamageTypes;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class LoosingControlEffect extends MobEffect {
    protected LoosingControlEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    Random random = new Random();

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {
        if(livingEntity.level().isClientSide) {
            float yaw = random.nextFloat() * 360f - 180f;
            float pitch = random.nextFloat() * 60f - 30f;

            livingEntity.setYRot(yaw);
            livingEntity.setXRot(pitch);

            livingEntity.yBodyRot = yaw;
            livingEntity.yHeadRot = yaw;
            return true;
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
