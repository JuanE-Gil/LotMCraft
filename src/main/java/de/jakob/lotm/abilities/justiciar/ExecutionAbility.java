package de.jakob.lotm.abilities.justiciar;

import com.google.common.util.concurrent.AtomicDouble;
import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.HashMap;

public class ExecutionAbility extends Ability {

    public ExecutionAbility(String id) {
        super(id, 60f, "execution");
        interactionRadius = 20;
        hasOptimalDistance = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("justiciar", 4));
    }

    @Override
    public float getSpiritualityCost() {
        return 600;
    }

    @Override
    public void onAbilityUse(Level level, LivingEntity caster) {
        if (level.isClientSide) return;
        ServerLevel serverLevel = (ServerLevel) level;

        // 70% fail chance
        if (random.nextDouble() < 0.7) {
            if (caster instanceof ServerPlayer player) {
                player.sendSystemMessage(Component.literal("[Execution] Your Verdict has Failed.")
                        .withStyle(ChatFormatting.RED));
            }
            return;
        }

        LivingEntity target = AbilityUtil.getTargetEntity(caster, 20, 1.5f);
        if (target == null) {
            if (caster instanceof ServerPlayer player) {
                player.sendSystemMessage(Component.literal("[Execution] No valid target.")
                        .withStyle(ChatFormatting.RED));
            }
            return;
        }

        playGuillotineAnimation(serverLevel, target);
    }

    private static void playGuillotineAnimation(ServerLevel serverLevel, LivingEntity target) {
        final double tx = target.getX();
        final double ty = target.getY();
        final double tz = target.getZ();

        // Blade descends from Y+8 down to Y+0 over 40 ticks (0.2 per tick)
        AtomicDouble bladeY = new AtomicDouble(ty + 8.0);

        ServerScheduler.scheduleForDuration(0, 1, 40,
                () -> {
                    double by = bladeY.get();

                    // Two vertical LARGE_SMOKE pillars at X±1 (static frame)
                    for (double pillarY = ty; pillarY <= ty + 8.0; pillarY += 0.4) {
                        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, tx - 1, pillarY, tz, 1, 0, 0, 0, 0);
                        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, tx + 1, pillarY, tz, 1, 0, 0, 0, 0);
                    }

                    // Horizontal ASH blade bar
                    for (double bx = tx - 1.0; bx <= tx + 1.0; bx += 0.15) {
                        serverLevel.sendParticles(ParticleTypes.ASH, bx, by, tz, 1, 0, 0, 0, 0);
                    }

                    bladeY.addAndGet(-0.2);
                },
                () -> {
                    // TOTEM_OF_UNDYING burst at target position
                    serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                            target.getX(), target.getY() + 1.0, target.getZ(),
                            60, 0.5, 0.5, 0.5, 0.3);

                    // Kill bypassing revival
                    LawAbility.SOLACE_KILLED.add(target.getUUID());
                    if (target instanceof ServerPlayer) {
                        target.hurt(serverLevel.damageSources().magic(), Float.MAX_VALUE);
                    } else {
                        target.kill();
                    }
                    ServerScheduler.scheduleDelayed(1, () -> LawAbility.SOLACE_KILLED.remove(target.getUUID()));
                },
                serverLevel
        );
    }
}
