package de.jakob.lotm.abilities.death;

import de.jakob.lotm.abilities.PhysicalEnhancementsAbility;
import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.abilities.core.interaction.InteractionHandler;
import de.jakob.lotm.damage.ModDamageTypes;
import de.jakob.lotm.util.data.Location;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.ParticleUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
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

public class PaleEyeAbility extends Ability {

    // Full-black dust for the beam
    private static final DustParticleOptions BLACK_DUST =
            new DustParticleOptions(new Vector3f(0.02f, 0.0f, 0.05f), 1.6f);
    private static final DustParticleOptions DARK_VOID_DUST =
            new DustParticleOptions(new Vector3f(0.1f, 0.0f, 0.15f), 1.2f);

    private static final int WITHER_DURATION = 20 * 60; // 1 minute
    private static final int BEAM_TICKS = 10;           // how long the beam persists visually
    private static final int TARGET_RANGE = 30;

    public PaleEyeAbility(String id) {
        super(id, 2400f);
        canBeCopied = false;
        canBeUsedInArtifact = false;
        canBeShared = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("death", 3));
    }

    // Sequence 3 max spirituality is 5000; cost is half = 2500
    private static final float SPIRITUALITY_COST = BeyonderData.getMaxSpirituality(3) * 0.5f;

    @Override
    protected float getSpiritualityCost() {
        return SPIRITUALITY_COST;
    }

    @Override
    public void onAbilityUse(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (InteractionHandler.isInteractionPossibleStrictlyHigher(new Location(entity.position(), serverLevel), "purification", BeyonderData.getSequence(entity), -1)) return;

        LivingEntity target = AbilityUtil.getTargetEntity(entity, TARGET_RANGE, 1.5f, true);
        if (target == null) {
            if (entity instanceof net.minecraft.world.entity.player.Player player) {
                player.sendSystemMessage(
                        Component.translatable("ability.lotmcraft.pale_eye.no_target")
                                .withStyle(ChatFormatting.DARK_GRAY));
            }
            return;
        }

        int casterSeq = BeyonderData.getSequence(entity);
        int targetSeq = BeyonderData.getSequence(target);

        spawnBeam(serverLevel, entity, target);

        // Sound: ghostly wither shriek
        level.playSound(null, entity.blockPosition(),
                SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 2.0f, 0.4f);

        // Wither effect regardless of outcome
        target.addEffect(new MobEffectInstance(MobEffects.WITHER,
                WITHER_DURATION, 1, false, true, true));

        PhysicalEnhancementsAbility.suppressRegen(target, 10_000);

        if (targetSeq >= casterSeq + 2) {
            // Target is 2+ sequences weaker — instant death
            ModDamageTypes.trueDamage(target, Float.MAX_VALUE, serverLevel, entity);
            if (entity instanceof net.minecraft.world.entity.player.Player player) {
                player.sendSystemMessage(
                        Component.translatable("ability.lotmcraft.pale_eye.death_gaze")
                                .withStyle(ChatFormatting.DARK_PURPLE));
            }
        } else {
            // Target is same, 1 sequence weaker, or stronger — deal scaled damage
            // At same sequence: 50% of max HP
            // Each sequence the caster is weaker (target seq < caster seq) reduces damage by 40%
            int seqDiff = casterSeq - targetSeq; // positive = caster is weaker
            float damageMultiplier = 0.5f - (seqDiff * 0.2f);
            if (damageMultiplier <= 0) {
                if (entity instanceof net.minecraft.world.entity.player.Player player) {
                    player.sendSystemMessage(
                            Component.translatable("ability.lotmcraft.pale_eye.too_strong")
                                    .withStyle(ChatFormatting.DARK_RED));
                }
                return;
            }

            float damage = target.getMaxHealth() * damageMultiplier;
            ModDamageTypes.trueDamage(target, damage, serverLevel, entity);

            if (entity instanceof net.minecraft.world.entity.player.Player player) {
                player.sendSystemMessage(
                        Component.translatable("ability.lotmcraft.pale_eye.gaze_struck")
                                .withStyle(ChatFormatting.DARK_AQUA));
            }
        }
    }

    /**
     * Draws a persistent black pillar/beam from the caster's eye to the target
     * for BEAM_TICKS ticks.
     */
    private void spawnBeam(ServerLevel level, LivingEntity caster, LivingEntity target) {
        ServerScheduler.scheduleForDuration(0, 1, BEAM_TICKS, () -> {
            if (!caster.isAlive() || !target.isAlive()) return;

            Vec3 eyePos = caster.getEyePosition();
            Vec3 targetCenter = target.getEyePosition();

            // Primary solid beam
            ParticleUtil.drawParticleLine(level, BLACK_DUST, eyePos, targetCenter, 0.4, 3);
            // Secondary thinner beam with slight offset for pillar feel
            ParticleUtil.drawParticleLine(level, DARK_VOID_DUST, eyePos, targetCenter, 0.5, 2, 0.1);
            // Soul particles along the beam
            ParticleUtil.drawParticleLine(level, ParticleTypes.SOUL, eyePos, targetCenter, 0.8, 1);

            // Burst at target hit point
            Vec3 tc = target.position().add(0, target.getBbHeight() / 2.0, 0);
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    tc.x, tc.y, tc.z, 4, 0.3, 0.3, 0.3, 0.02);
        }, level);
    }
}
