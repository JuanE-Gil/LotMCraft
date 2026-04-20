package de.jakob.lotm.abilities.justiciar;

import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.events.WorldJudgmentHandler;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.helper.AbilityUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldJudgmentAbility extends Ability {

    public WorldJudgmentAbility(String id) {
        super(id, 60f * 3);
        hasOptimalDistance = true;
        optimalDistance = 20f;
        canBeCopied = false;
        canBeUsedByNPC = false;
        canBeReplicated = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("justiciar", 0));
    }

    @Override
    protected float getSpiritualityCost() {
        return 500;
    }

    @Override
    public void onAbilityUse(Level level, LivingEntity entity) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        int color = BeyonderData.pathwayInfos.get("justiciar").color();
        UUID casterUUID = entity.getUUID();

        // Re-cast: release active judgment
        if (WorldJudgmentHandler.JUDGED_BY_CASTER.containsKey(casterUUID)) {
            UUID targetUUID = WorldJudgmentHandler.JUDGED_BY_CASTER.remove(casterUUID);
            WorldJudgmentHandler.JUDGMENT_TIER.remove(targetUUID);

            if (entity instanceof ServerPlayer sp) {
                sp.sendSystemMessage(Component.literal("[World Judgment] Judgment has been released.")
                        .withStyle(ChatFormatting.GOLD));
            }
            LivingEntity target = serverLevel.getEntity(targetUUID) instanceof LivingEntity le ? le : null;
            if (target instanceof ServerPlayer tsp) {
                tsp.sendSystemMessage(Component.literal("[World Judgment] You have been released from World Judgment.")
                        .withStyle(ChatFormatting.GOLD));
            }
            return;
        }

        // First cast: designate target
        LivingEntity target = AbilityUtil.getTargetEntity(entity, 30, 5);
        if (target == null || target == entity) {
            AbilityUtil.sendActionBar(entity, Component.literal("No target found.").withColor(color));
            return;
        }

        WorldJudgmentHandler.JUDGED_BY_CASTER.put(casterUUID, target.getUUID());
        WorldJudgmentHandler.JUDGMENT_TIER.put(target.getUUID(), 0);

        if (entity instanceof ServerPlayer sp) {
            sp.sendSystemMessage(Component.literal("[World Judgment] " + target.getName().getString()
                    + " is now under World Judgment.").withStyle(ChatFormatting.GOLD));
        }
        if (target instanceof ServerPlayer tsp) {
            tsp.sendSystemMessage(Component.literal("[World Judgment] You have been placed under World Judgment. Obey the Laws.")
                    .withStyle(ChatFormatting.DARK_RED));
        }
    }
}
