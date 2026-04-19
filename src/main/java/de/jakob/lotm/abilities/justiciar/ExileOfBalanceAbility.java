package de.jakob.lotm.abilities.justiciar;

import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.abilities.core.AbilityUsedEvent;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.AllyUtil;
import de.jakob.lotm.util.helper.RingEffectManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ExileOfBalanceAbility extends Ability {

    public static final Map<UUID, Long> EXILED_ENTITIES = new ConcurrentHashMap<>();

    private static final double ZONE_RADIUS = 40.0;
    private static final int MIN_EXILE_DURATION = 200;   // 10 seconds
    private static final int MAX_EXILE_DURATION = 2400;  // 2 minutes

    public ExileOfBalanceAbility(String id) {
        super(id, 60f);
        interactionRadius = 40;
        hasOptimalDistance = false;
        postsUsedAbilityEventManually = true;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("justiciar", 2));
    }

    @Override
    protected float getSpiritualityCost() {
        return 600;
    }

    @Override
    public void onAbilityUse(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        ServerLevel serverLevel = (ServerLevel) level;

        List<LivingEntity> nearby = AbilityUtil.getNearbyEntities(entity, serverLevel, entity.position(), (int) ZONE_RADIUS);

        // Split into caster's side (caster + allies) and enemy side (beyonders not allied)
        List<LivingEntity> casterSide = new ArrayList<>();
        List<LivingEntity> enemySide = new ArrayList<>();

        casterSide.add(entity);

        for (LivingEntity e : nearby) {
            if (e == entity) continue;
            if (!BeyonderData.isBeyonder(e)) continue;
            if (AllyUtil.areAllies(entity, e)) {
                casterSide.add(e);
            } else {
                enemySide.add(e);
            }
        }

        // Power score: sum of (10 - sequence) per beyonder
        int casterScore = casterSide.stream()
                .filter(BeyonderData::isBeyonder)
                .mapToInt(e -> 10 - BeyonderData.getSequence(e))
                .sum();
        int enemyScore = enemySide.stream()
                .mapToInt(e -> 10 - BeyonderData.getSequence(e))
                .sum();

        // Fizzle if sides are within 10% of each other
        int total = casterScore + enemyScore;
        if (total == 0 || Math.abs(casterScore - enemyScore) <= total * 0.10) {
            if (entity instanceof net.minecraft.server.level.ServerPlayer sp) {
                sp.sendSystemMessage(Component.literal("The sides are already balanced.")
                        .withStyle(ChatFormatting.RED));
            }
            return;
        }

        // Identify dominant side and exile members until scores are roughly equal
        boolean enemyDominant = enemyScore > casterScore;
        List<LivingEntity> dominantSide = enemyDominant ? enemySide : new ArrayList<>(casterSide);
        if (!enemyDominant) dominantSide.remove(entity); // never exile caster

        // Sort by sequence ascending (lowest seq = highest power = exiled first)
        dominantSide.sort(Comparator.comparingInt(BeyonderData::getSequence));

        int weakerScore = enemyDominant ? casterScore : enemyScore;
        int dominantScore = enemyDominant ? enemyScore : casterScore;

        long gameTime = serverLevel.getGameTime();
        int exiledCount = 0;

        for (LivingEntity target : dominantSide) {
            if (dominantScore <= weakerScore + (int)(weakerScore * 0.10)) break;
            int power = 10 - BeyonderData.getSequence(target);
            int durationTicks = MIN_EXILE_DURATION + random.nextInt(MAX_EXILE_DURATION - MIN_EXILE_DURATION + 1);
            EXILED_ENTITIES.put(target.getUUID(), gameTime + durationTicks);
            dominantScore -= power;
            exiledCount++;

            int durationSeconds = durationTicks / 20;
            if (target instanceof net.minecraft.server.level.ServerPlayer sp) {
                sp.sendSystemMessage(Component.literal("[Exile of Balance] You have been removed from this battle for " + durationSeconds + " seconds.")
                        .withStyle(ChatFormatting.GOLD));
            }
        }

        if (exiledCount == 0) {
            if (entity instanceof net.minecraft.server.level.ServerPlayer sp) {
                sp.sendSystemMessage(Component.literal("The sides are already balanced.")
                        .withStyle(ChatFormatting.RED));
            }
            return;
        }

        Vec3 center = entity.position();

        // Pale blue/white ring
        RingEffectManager.createRingForAll(center, (float) ZONE_RADIUS, 40,
                0.75f, 0.85f, 1.0f, 0.8f, 2f, 4f, serverLevel);

        Component message = Component.literal("[Exile of Balance] has been declared")
                .withStyle(ChatFormatting.GOLD);
        serverLevel.getServer().getPlayerList().getPlayers().forEach(p -> {
            if (p.level().equals(serverLevel) && p.distanceTo(entity) <= ZONE_RADIUS) {
                p.sendSystemMessage(message);
            }
        });

        NeoForge.EVENT_BUS.post(new AbilityUsedEvent(serverLevel, center, entity, this, interactionFlags, ZONE_RADIUS, 20 * 2));
    }
}
