package de.jakob.lotm.events;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.attachments.UniquenessComponent;
import de.jakob.lotm.entity.custom.uniqueness.UniquenessEntity;
import de.jakob.lotm.gamerule.ModGameRules;
import de.jakob.lotm.network.PacketHandler;
import de.jakob.lotm.util.BeyonderData;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Random;

@EventBusSubscriber(modid = LOTMCraft.MOD_ID)
public class UniquenessEventHandler {

    private static final Random RANDOM = new Random();
    private static final int SPAWN_CHECK_INTERVAL = 20 * 5;

    private static final int SPAWN_RADIUS_BLOCKS = 180;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        var server = event.getServer();
        ServerLevel overworld = server.overworld();

        if (overworld.getGameTime() % SPAWN_CHECK_INTERVAL != 0) return;

        for (String pathway : BeyonderData.implementedPathways) {
            trySpawnUniqueness(overworld, pathway);
        }
    }

    private static void trySpawnUniqueness(ServerLevel level, String pathway) {
        if (UniquenessEntity.existsInWorld(level, pathway)) return;

        if (anyPlayerHoldsUniqueness(level, pathway)) return;

        if (BeyonderData.beyonderMap != null && BeyonderData.beyonderMap.count(pathway, 0) > 0) return;

        if (BeyonderData.beyonderMap == null || BeyonderData.beyonderMap.count(pathway, 1) == 0) return;

        float spawnChance = level.getGameRules().getInt(ModGameRules.UNIQUENESS_SPAWN_LIKELIHOOD) / 100f;

        if (RANDOM.nextDouble() > spawnChance) return;

        ServerPlayer targetPlayer = findSeq1Player(level, pathway);
        if (targetPlayer == null) return;
        if(targetPlayer.level().dimension() != Level.OVERWORLD) return;

        Vec3 spawnPos = targetPlayer.position().add(
                (RANDOM.nextDouble() - 0.5) * SPAWN_RADIUS_BLOCKS * 2,
                0,
                (RANDOM.nextDouble() - 0.5) * SPAWN_RADIUS_BLOCKS * 2
        );
        int groundY = level.getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                new net.minecraft.core.BlockPos((int) spawnPos.x, 0, (int) spawnPos.z)
        ).getY();

        Vec3 finalPos = new Vec3(spawnPos.x, groundY, spawnPos.z);
        UniquenessEntity.trySpawn(level, finalPos, pathway);
    }

    private static boolean anyPlayerHoldsUniqueness(ServerLevel level, String pathway) {
        for (ServerPlayer player : level.players()) {
            UniquenessComponent comp = player.getData(ModAttachments.UNIQUENESS_COMPONENT);
            if (comp.hasUniqueness() && pathway.equalsIgnoreCase(comp.getUniquenessPathway())) {
                return true;
            }
        }
        // Also check all players in the beyonder map (even offline) via the stored attachment
        // For offline players we rely on the attachment serialization (copyOnDeath preserves it)
        return false;
    }

    private static ServerPlayer findSeq1Player(ServerLevel level, String pathway) {
        for (ServerPlayer player : level.players()) {
            if (BeyonderData.getSequence(player) == 1
                    && BeyonderData.getPathway(player).equalsIgnoreCase(pathway)) {
                return player;
            }
        }
        return null;
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;

        if (entity instanceof Player player) {
            UniquenessComponent comp = player.getData(ModAttachments.UNIQUENESS_COMPONENT);
            if (comp.hasUniqueness()) {
                String pathway = comp.getUniquenessPathway();
                Vec3 deathPos = player.position();

                serverLevel.getServer().execute(() -> {
                    comp.setHasUniqueness(false);
                    comp.setUniquenessPathway("");
                    UniquenessEntity.trySpawn(serverLevel, deathPos, pathway);
                    if (player instanceof ServerPlayer sp) {
                        PacketHandler.syncUniquenessToPlayer(sp);
                    }
                });
            }
        }

        Entity killer = event.getSource().getEntity();
        if (killer instanceof ServerPlayer killerPlayer) {
            UniquenessComponent killerComp = killerPlayer.getData(ModAttachments.UNIQUENESS_COMPONENT);
            if (killerComp.hasUniqueness()) {
                killerComp.incrementKillCount();
                PacketHandler.syncUniquenessToPlayer(killerPlayer);
            }
        }
    }
}
