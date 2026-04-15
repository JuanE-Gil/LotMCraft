package de.jakob.lotm.abilities.death;

import de.jakob.lotm.abilities.PhysicalEnhancementsAbility;
import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.damage.ModDamageTypes;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HandOfDeathAbility extends SelectableAbility {

    private static final int WITHER_DURATION  = 20 * 30; // 30 seconds
    private static final int EFFECT_AMPLIFIER = 1;

    // Tracks active Left Hand marks: target UUID -> scheduled task UUID
    private static final Map<UUID, UUID> activeMarks = new ConcurrentHashMap<>();

    public HandOfDeathAbility(String id) {
        super(id, 20f); // 1-second cooldown
        canBeCopied = false;
        canBeReplicated = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("death", 3));
    }

    @Override
    protected float getSpiritualityCost() {
        return 400;
    }

    @Override
    protected String[] getAbilityNames() {
        return new String[]{
                "ability.lotmcraft.hand_of_death.left",
                "ability.lotmcraft.hand_of_death.right_self",
                "ability.lotmcraft.hand_of_death.right_others"
        };
    }

    @Override
    protected void castSelectedAbility(Level level, LivingEntity entity, int abilityIndex) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        switch (abilityIndex) {
            case 0 -> leftHand(serverLevel, entity);
            case 1 -> rightHandSelf(serverLevel, entity);
            case 2 -> rightHandOthers(serverLevel, entity);
        }
    }

    // -------------------------------------------------------------------------
    // Left Hand — mark target for 30s, then deal 25% max HP damage
    // -------------------------------------------------------------------------

    private void leftHand(ServerLevel level, LivingEntity caster) {
        LivingEntity target = AbilityUtil.getTargetEntity(caster, 30, 1.5f, true);
        if (target == null) {
            sendMessage(caster, "ability.lotmcraft.hand_of_death.no_target", ChatFormatting.DARK_GRAY);
            return;
        }

        // Cancel any existing mark on this target
        UUID existing = activeMarks.remove(target.getUUID());
        if (existing != null) {
            ServerScheduler.cancel(existing);
        }

        // Apply effects for 30 seconds
        target.addEffect(new MobEffectInstance(MobEffects.WITHER,
                WITHER_DURATION, EFFECT_AMPLIFIER, false, true, true));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,
                WITHER_DURATION, EFFECT_AMPLIFIER, false, true, true));
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,
                WITHER_DURATION, EFFECT_AMPLIFIER, false, true, true));

        sendMessage(caster, "ability.lotmcraft.hand_of_death.left_applied", ChatFormatting.DARK_PURPLE);

        // Capture sequence values now — target's sequence may change before deferred execution
        int casterSeq = BeyonderData.getSequence(caster);
        int targetSeq = BeyonderData.getSequence(target);
        // seqDiff > 0: target is weaker; seqDiff < 0: target is stronger
        int seqDiff = targetSeq - casterSeq;
        float damageMultiplier = Math.max(0f, 0.25f + (seqDiff * 0.10f));

        // Schedule damage at the end of the 30 seconds
        LivingEntity targetRef = target;
        UUID taskId = ServerScheduler.scheduleDelayed(WITHER_DURATION, () -> {
            activeMarks.remove(targetRef.getUUID());
            if (!targetRef.isAlive()) return;

            float damage = targetRef.getMaxHealth() * damageMultiplier;
            PhysicalEnhancementsAbility.suppressRegen(targetRef, 10_000);
            ModDamageTypes.trueDamage(targetRef, damage, level, caster);
        }, level);

        activeMarks.put(target.getUUID(), taskId);
    }

    // -------------------------------------------------------------------------
    // Right Hand (Self) — heal caster for 25% of their max HP
    // -------------------------------------------------------------------------

    private void rightHandSelf(ServerLevel level, LivingEntity caster) {
        float heal = caster.getMaxHealth() * 0.25f;
        caster.heal(heal);
        sendMessage(caster, "ability.lotmcraft.hand_of_death.right_self_healed", ChatFormatting.DARK_GREEN);
    }

    // -------------------------------------------------------------------------
    // Right Hand (Others) — heal a targeted entity for 25% of their max HP
    // -------------------------------------------------------------------------

    private void rightHandOthers(ServerLevel level, LivingEntity caster) {
        LivingEntity target = AbilityUtil.getTargetEntity(caster, 30, 1.5f, true);
        if (target == null) {
            sendMessage(caster, "ability.lotmcraft.hand_of_death.no_target", ChatFormatting.DARK_GRAY);
            return;
        }

        float heal = target.getMaxHealth() * 0.25f;
        target.heal(heal);
        sendMessage(caster, "ability.lotmcraft.hand_of_death.right_others_healed", ChatFormatting.DARK_GREEN);
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    private static void sendMessage(LivingEntity entity, String key, ChatFormatting color) {
        if (entity instanceof ServerPlayer player) {
            player.sendSystemMessage(Component.translatable(key).withStyle(color));
        }
    }
}
