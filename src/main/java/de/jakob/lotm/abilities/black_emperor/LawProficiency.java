package de.jakob.lotm.abilities.black_emperor;

import de.jakob.lotm.abilities.core.ToggleAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Map;

import static de.jakob.lotm.util.BeyonderData.getSequence;

public class LawProficiency extends ToggleAbility {

    public LawProficiency(String id) {
        super(id);
    }

    @Override
    public float getSpiritualityCost() {
        return 1.5f;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return Map.of("black_emperor", 9);
    }

    @Override
    public void tick(Level level, LivingEntity entity) {
        if (level.isClientSide) return;

        for (LivingEntity target : level.getEntitiesOfClass(
                LivingEntity.class,
                entity.getBoundingBox().inflate(6))) {

            if (target == entity) continue;

            int targetSeq = getSequence(target);
            int selfSeq = getSequence(entity);

            if (targetSeq < selfSeq) {
                target.getPersistentData().remove("lotm_law_violated");
                continue;
            }

            boolean armorViolation = target.getArmorValue() < 10f;
            boolean healthViolation = target.getHealth() < target.getMaxHealth() * 0.3f;
            boolean speedViolation = target.getDeltaMovement().length() > 0.25f;

            boolean isViolating = armorViolation || healthViolation || speedViolation;

            if (isViolating) {
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1));
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0));
                target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
                target.getPersistentData().putBoolean("lotm_law_violated", true);
            } else {
                target.getPersistentData().remove("lotm_law_violated");
            }
        }
    }

    @Override
    public void start(Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            entity.getPersistentData().putBoolean("lotm_law_active", true);
            entity.sendSystemMessage(Component.literal("§6Law Proficiency: ON"));
        }
    }

    @Override
    public void stop(Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            entity.getPersistentData().remove("lotm_law_active");

            for (LivingEntity target : level.getEntitiesOfClass(
                    LivingEntity.class,
                    entity.getBoundingBox().inflate(6))) {
                target.getPersistentData().remove("lotm_law_violated");
            }

            entity.sendSystemMessage(Component.literal("§cLaw Proficiency: OFF"));
        }
    }
}





