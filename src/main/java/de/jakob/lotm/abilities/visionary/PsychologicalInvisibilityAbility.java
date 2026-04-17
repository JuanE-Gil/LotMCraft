package de.jakob.lotm.abilities.visionary;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.abilities.core.ToggleAbility;
import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.damage.ModDamageTypes;
import de.jakob.lotm.network.PacketHandler;
import de.jakob.lotm.network.packets.toClient.SyncApotheosisPacket;
import de.jakob.lotm.network.packets.toClient.SyncPsychologicalInvisibilityPacket;
import de.jakob.lotm.rendering.DecryptionRenderLayer;
import de.jakob.lotm.rendering.SpiritVisionOverlayRenderer;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.ClientBeyonderCache;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = LOTMCraft.MOD_ID)
public class PsychologicalInvisibilityAbility extends ToggleAbility {
    public static final HashMap<UUID, Integer> invisiblePlayers = new HashMap<>();
    public static HashMap<UUID, Integer> invisiblePlayersClient = new HashMap<>();
    public static final HashMap<UUID, Integer> hits = new HashMap<>();

    public PsychologicalInvisibilityAbility(String id) {
        super(id);
        canBeCopied = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("visionary", 6));
    }

    @Override
    public float getSpiritualityCost() {
        return 13;
    }

    @Override
    public void tick(Level level, LivingEntity entity) {
        if (level instanceof ServerLevel serverLevel) {
            if (!invisiblePlayers.containsKey(entity.getUUID()))
                cancel(serverLevel, entity);
        }
    }

    @Override
    public void start(Level level, LivingEntity entity) {
        if (!invisiblePlayers.containsKey(entity.getUUID())) {
            add(entity, this);
        }
    }

    @Override
    public void stop(Level level, LivingEntity entity) {
        remove(entity);
    }

    public static void add(LivingEntity entity, PsychologicalInvisibilityAbility skill) {
        invisiblePlayers.put(entity.getUUID(), AbilityUtil.getSeqWithArt(entity, skill));
        hits.put(entity.getUUID(), 0);

        PacketHandler.sendToAllPlayers(new SyncPsychologicalInvisibilityPacket(invisiblePlayers));
    }

    public static void remove(LivingEntity entity) {
        invisiblePlayers.remove(entity.getUUID());
        hits.remove(entity.getUUID());

        PacketHandler.sendToAllPlayers(new SyncPsychologicalInvisibilityPacket(invisiblePlayers));
        entity.setInvisible(false);
    }

    public static int getHitsBySeq(int seq) {
        return switch (seq) {
            case 6 -> 1;
            case 5 -> 2;
            case 4 -> 4;
            case 3 -> 5;
            case 2 -> 10;
            case 1 -> 11;
            case 0 -> 22;
            default -> 1;
        };
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();

        if (invisiblePlayers.containsKey(player.getUUID())) {
            int seq = invisiblePlayers.get(player.getUUID());

            if (seq > 2) {
                remove(player);
                return;
            }

            BeyonderData.incrementSpirituality(player, -1000f);
        }
    }

    @SubscribeEvent
    public static void onEntityTickPre(EntityTickEvent.Post event) {
        if(event.getEntity() instanceof LivingEntity entity) {
            int tickCount = entity.tickCount;

            if (invisiblePlayers.containsKey(entity.getUUID())){
                if(tickCount % 200 == 0){
                    if(hits.get(entity.getUUID()) != 0)
                        hits.put(entity.getUUID(), hits.get(entity.getUUID()) - 1);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingIncomingDamageEvent event) {
        //was hit
        var entity = event.getEntity();

        if (invisiblePlayers.containsKey(entity.getUUID())) {
            hits.put(entity.getUUID(), hits.get(entity.getUUID()) + 1);

            if (hits.get(entity.getUUID()) >= getHitsBySeq(invisiblePlayers.get(entity.getUUID()))) {
                remove(entity);
            }
        }


        //hit someone
        if (event.getSource().getEntity() instanceof LivingEntity player) {
            var source = event.getSource();

            if (!(source.is(ModDamageTypes.BEYONDER_GENERIC)) || !(source.is(ModDamageTypes.LOOSING_CONTROL))) {
                if (invisiblePlayers.containsKey(player.getUUID())) {
                    remove(player);
                }
            }
        }

    }

    @SubscribeEvent
    public static void onLivingTarget(LivingChangeTargetEvent event) {
        if (event.getNewAboutToBeSetTarget() != null && invisiblePlayers.containsKey(event.getNewAboutToBeSetTarget().getUUID())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();

        if (invisiblePlayersClient.containsKey(player.getUUID())) {
            var clientPlayer = Minecraft.getInstance().player;

            if (clientPlayer == player) return;

            if (clientPlayer != null) {
                if (ClientBeyonderCache.getPathway(clientPlayer.getUUID()).equals("visionary") &&
                        ClientBeyonderCache.getSequence(clientPlayer.getUUID()) < invisiblePlayersClient.get(player.getUUID()))
                    return;

                if (DecryptionRenderLayer.activeDecryption.contains(clientPlayer.getUUID()) ||
                        SpiritVisionOverlayRenderer.entitiesLookedAt.containsKey(clientPlayer.getUUID())) {
                    if (AbilityUtil.isTargetSignificantlyWeaker(ClientBeyonderCache.getSequence(clientPlayer.getUUID()),
                            invisiblePlayersClient.get(player.getUUID())))
                        return;
                }
            }

            player.setInvisible(true);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlaySoundAtPos(PlayLevelSoundEvent.AtPosition event) {
        Level level = event.getLevel();

        for (Player player : level.players()) {
            if (invisiblePlayersClient.containsKey(player.getUUID())) {

                double dist = player.distanceToSqr(
                        event.getPosition().x,
                        event.getPosition().y,
                        event.getPosition().z
                );

                if (dist < 5.0) {
                    var clientPlayer = Minecraft.getInstance().player;

                    if (clientPlayer == player) return;

                    if (clientPlayer != null) {
                        if (ClientBeyonderCache.getPathway(clientPlayer.getUUID()).equals("visionary") &&
                                ClientBeyonderCache.getSequence(clientPlayer.getUUID()) < invisiblePlayersClient.get(player.getUUID()))
                            return;

                        if (DecryptionRenderLayer.activeDecryption.contains(clientPlayer.getUUID()) ||
                                SpiritVisionOverlayRenderer.entitiesLookedAt.containsKey(clientPlayer.getUUID())) {
                            if (AbilityUtil.isTargetSignificantlyWeaker(ClientBeyonderCache.getSequence(clientPlayer.getUUID()),
                                    invisiblePlayersClient.get(player.getUUID())))
                                return;
                        }
                    }

                    player.setSilent(true);
                    event.setCanceled(true);
                }
            }
        }
    }

}
