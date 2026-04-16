package de.jakob.lotm.abilities.justiciar;

import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.abilities.core.AbilityUsedEvent;
import de.jakob.lotm.particle.ModParticles;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.ParticleUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;

import java.util.HashMap;
import java.util.Map;

public class VerdictExileAbility extends Ability {

    public VerdictExileAbility(String id) {
        super(id, 12f, "exile");
        interactionRadius = 25;
        hasOptimalDistance = false;
        postsUsedAbilityEventManually = true;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("justiciar", 6));
    }

    @Override
    protected float getSpiritualityCost() {
        return 200;
    }

    @Override
    public void onAbilityUse(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        ServerLevel serverLevel = (ServerLevel) level;

        LivingEntity target = AbilityUtil.getTargetEntity(entity, 25, 1.4f);
        if (target == null) {
            if (entity instanceof ServerPlayer sp) {
                sp.sendSystemMessage(Component.literal("No target found for Verdict: Exile").withStyle(ChatFormatting.RED));
            }
            return;
        }

        // Launch target upward — use absolute Y velocity so it always launches regardless of current motion
        Vec3 current = target.getDeltaMovement();
        target.setDeltaMovement(current.x, Math.max(current.y, 0) + 3.5, current.z);
        target.hurtMarked = true;

        // Apply Slow Falling
        target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0, false, false));

        // Spawn cone-shaped gold/red particles
        Vec3 look = entity.getLookAngle();
        Vec3 origin = entity.getEyePosition();

        for (double radius = 0.5; radius <= 4.0; radius += 0.5) {
            for (double angle = 0; angle < Math.PI * 2; angle += 0.3) {
                // Build perpendicular basis from look direction
                Vec3 perp1 = new Vec3(-look.z, 0, look.x).normalize();
                Vec3 perp2 = look.cross(perp1).normalize();

                Vec3 offset = perp1.scale(Mth.cos((float) angle) * radius)
                        .add(perp2.scale(Mth.sin((float) angle) * radius));
                Vec3 particlePos = origin.add(look.scale(radius)).add(offset);

                if (angle % 0.6 < 0.31) {
                    ParticleUtil.spawnParticles(serverLevel, ModParticles.GOLDEN_NOTE.get(),
                            particlePos, 1, 0.05, 0.1);
                } else {
                    ParticleUtil.spawnParticles(serverLevel, ModParticles.HOLY_FLAME.get(),
                            particlePos, 1, 0.05, 0.1);
                }
            }
        }

        NeoForge.EVENT_BUS.post(new AbilityUsedEvent(serverLevel, entity.position(), entity, this, interactionFlags, 25, 20 * 2));
    }
}
