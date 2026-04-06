package de.jakob.lotm.abilities.visionary;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.effect.ModEffects;
import de.jakob.lotm.entity.ModEntities;
import de.jakob.lotm.entity.custom.BeyonderNPCEntity;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.*;

@EventBusSubscriber(modid = LOTMCraft.MOD_ID)
public class DreamWeaveAbility extends SelectableAbility {
    private static final Map<UUID, List<BeyonderNPCEntity>> VICTIM_MOBS = new HashMap<>();

    public DreamWeaveAbility(String id) {
        super(id, 10f);
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("visionary", 3));
    }

    @Override
    public float getSpiritualityCost() {
        return 750;
    }

    @Override
    protected String[] getAbilityNames() {
        return new String[]{
                "ability.lotmcraft.dream_weave.strong",
                "ability.lotmcraft.dream_weave.weak",
        };
    }

    @Override
    protected void castSelectedAbility(Level level, LivingEntity entity, int abilityIndex) {
        switch (abilityIndex) {
            case 0 -> strong(level, entity);
            case 1 -> weak(level, entity);
        }
    }

    // Spawns 1 mob, 1 sequence below the caster
    private void strong(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        LivingEntity target = AbilityUtil.getTargetEntity(entity, 20, 2);
        if (target == null) {
            AbilityUtil.sendActionBar(entity,
                    Component.translatable("ability.lotmcraft.frenzy.no_target").withColor(0xFFff124d));
            return;
        }

        int mobSeq = Math.min(BeyonderData.getSequence(entity) + 1, 9);
        Vec3 center = target.position();

        List<String> pathways = new ArrayList<>(BeyonderData.implementedPathways);
        String pathway = pathways.get(random.nextInt(pathways.size()));

        BeyonderNPCEntity mob = new BeyonderNPCEntity(
                ModEntities.BEYONDER_NPC.get(), serverLevel, true, pathway, mobSeq);
        mob.setPos(center.x, center.y, center.z);
        mob.getPersistentData().putBoolean("VoidSummoned", true);
        mob.setPuppetWarrior(true);
        mob.setMaxLifetimeIfPuppet(20 * 5);
        mob.setTarget(target);

        serverLevel.addFreshEntity(mob);

        VICTIM_MOBS.computeIfAbsent(target.getUUID(), k -> new ArrayList<>()).add(mob);

        ServerScheduler.scheduleDelayed(20 * 5, () -> {
            if (!mob.isRemoved()) mob.discard();
            removeMob(target.getUUID(), mob);
        });
    }

    // Spawns 3 mobs, 3 sequences below the caster
    private void weak(Level level, LivingEntity entity) {
        if (level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        LivingEntity target = AbilityUtil.getTargetEntity(entity, 20, 2);
        if (target == null) {
            AbilityUtil.sendActionBar(entity,
                    Component.translatable("ability.lotmcraft.frenzy.no_target").withColor(0xFFff124d));
            return;
        }

        int mobSeq = Math.min(BeyonderData.getSequence(entity) + 3, 9);
        Vec3 center = target.position();
        double spawnRadius = 3.0;

        List<BeyonderNPCEntity> mobs = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            double angle = Math.toRadians(i * 120.0);
            double spawnX = center.x + spawnRadius * Math.cos(angle);
            double spawnZ = center.z + spawnRadius * Math.sin(angle);

            List<String> pathways = new ArrayList<>(BeyonderData.implementedPathways);
            String pathway = pathways.get(random.nextInt(pathways.size()));

            BeyonderNPCEntity mob = new BeyonderNPCEntity(
                    ModEntities.BEYONDER_NPC.get(), serverLevel, true, pathway, mobSeq);
            mob.setPos(spawnX, center.y, spawnZ);
            mob.getPersistentData().putBoolean("VoidSummoned", true);
            mob.setPuppetWarrior(true);
            mob.setMaxLifetimeIfPuppet(20 * 5);
            mob.setTarget(target);

            serverLevel.addFreshEntity(mob);
            mobs.add(mob);
        }

        VICTIM_MOBS.computeIfAbsent(target.getUUID(), k -> new ArrayList<>()).addAll(mobs);

        ServerScheduler.scheduleDelayed(20 * 5, () -> {
            for (BeyonderNPCEntity mob : mobs) {
                if (!mob.isRemoved()) mob.discard();
            }
            removeAllMobs(target.getUUID(), mobs);
        });
    }


    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        if (event.getEntity().level().isClientSide) return;
        if (!event.getEffect().is(ModEffects.LOOSING_CONTROL)) return;

        UUID victimUUID = event.getEntity().getUUID();
        List<BeyonderNPCEntity> mobs = VICTIM_MOBS.remove(victimUUID);
        if (mobs == null) return;

        for (BeyonderNPCEntity mob : mobs) {
            if (!mob.isRemoved()) mob.discard();
        }
    }

    private static void removeMob(UUID victimUUID, BeyonderNPCEntity mob) {
        List<BeyonderNPCEntity> mobs = VICTIM_MOBS.get(victimUUID);
        if (mobs == null) return;
        mobs.remove(mob);
        if (mobs.isEmpty()) VICTIM_MOBS.remove(victimUUID);
    }

    private static void removeAllMobs(UUID victimUUID, List<BeyonderNPCEntity> mobs) {
        List<BeyonderNPCEntity> tracked = VICTIM_MOBS.get(victimUUID);
        if (tracked == null) return;
        tracked.removeAll(mobs);
        if (tracked.isEmpty()) VICTIM_MOBS.remove(victimUUID);
    }
}