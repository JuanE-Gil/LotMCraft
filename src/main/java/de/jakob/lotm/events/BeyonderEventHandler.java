package de.jakob.lotm.events;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.artifacts.SealedArtifactData;
import de.jakob.lotm.damage.ModDamageTypes;
import de.jakob.lotm.attachments.DisabledFlightComponent;
import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.data.ModDataComponents;
import de.jakob.lotm.effect.ModEffects;
import de.jakob.lotm.gamerule.ModGameRules;
import de.jakob.lotm.item.PotionIngredient;
import de.jakob.lotm.network.PacketHandler;
import de.jakob.lotm.potions.BeyonderCharacteristicItem;
import de.jakob.lotm.potions.BeyonderCharacteristicItemHandler;
import de.jakob.lotm.potions.BeyonderPotion;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.ClientBeyonderCache;
import de.jakob.lotm.util.beyonderMap.BeyonderMap;
import de.jakob.lotm.util.beyonderMap.CharacteristicStack;
import de.jakob.lotm.util.beyonderMap.StoredData;
import de.jakob.lotm.attachments.SharedAbilitiesComponent;
import de.jakob.lotm.attachments.TeamComponent;
import de.jakob.lotm.util.helper.AbilityUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Objects;
import java.util.Random;

import static de.jakob.lotm.util.BeyonderData.beyonderMap;

@EventBusSubscriber(modid = LOTMCraft.MOD_ID)
public class BeyonderEventHandler {

    @SubscribeEvent
    public static void onPlayerJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // Sync beyonder data when player joins
            PacketHandler.syncBeyonderDataToPlayer(serverPlayer);

            beyonderMap.onPlayerUUIDChange(serverPlayer);

            if (!beyonderMap.contains(serverPlayer)) {
                beyonderMap.put(serverPlayer);
            } else {
                StoredData data = beyonderMap.get(serverPlayer).get();

                // Only restore from map if player has NO beyonder data (data loss scenario)
                // Or when marked to do so by server admin
                if (!BeyonderData.isBeyonder(serverPlayer) || data.modified()) {
                    BeyonderData.setBeyonder(serverPlayer, data.pathway(), data.sequence());
                    beyonderMap.markModified(serverPlayer, false);

                } else if (beyonderMap.isDiffPathSeq(serverPlayer)) {
                    // If they have data but it differs, update the map to match NBT (NBT is source of truth)
                    beyonderMap.put(serverPlayer);
                }
            }

            serverPlayer.addEffect(new MobEffectInstance(ModEffects.CONCEALMENT, 20 * 30, 99, false, false, false));
        }
    }

    @SubscribeEvent
    public static void onTotemUsed(LivingUseTotemEvent event) {
        LivingEntity entity = event.getEntity();

        if(BeyonderData.isBeyonder(entity)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // Re-sync data when changing dimensions
            PacketHandler.syncBeyonderDataToPlayer(serverPlayer);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity source) {
            if (!AbilityUtil.mayDamage(source, event.getEntity())) {
                event.setCanceled(true);
            }
        }
    }

    // Disable Flight while in combat
    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Post event) {
        if (event.getSource().getEntity() instanceof LivingEntity source) {
            if(BeyonderData.isBeyonder(source) && event.getEntity().level().getGameRules().getBoolean(ModGameRules.DISABLE_FLIGHT_IN_COMBAT)) {
                DisabledFlightComponent flightData = event.getEntity().getData(ModAttachments.FLIGHT_DISABLE_COMPONENT);
                flightData.setCooldownTicks(20 * 20);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // Re-sync data on respawn
            PacketHandler.syncBeyonderDataToPlayer(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity().level().isClientSide()) {
            // Clear client cache when player logs out
            ClientBeyonderCache.removePlayer(event.getEntity().getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerDrops(LivingDropsEvent event) {
        // sorry nihil i have to mess with your method :)
        // cancel the drop of items completely for summoned entities
        if (event.getEntity().getPersistentData().contains("VoidSummoned")) {
            event.setCanceled(true);
            return;
        }

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!BeyonderData.isBeyonder(player)) return;
        if (!player.serverLevel().getGameRules().getBoolean(ModGameRules.REGRESS_SEQUENCE_ON_DEATH)) return;

        var stack = new ItemStack(Objects.requireNonNull(BeyonderCharacteristicItemHandler
                .selectCharacteristicOfPathwayAndSequence(
                        BeyonderData.getPathway(player), BeyonderData.getSequence(player))).asItem());

        ItemEntity itemEntity = new ItemEntity(
                player.level(),
                player.getX(),
                player.getY(),
                player.getZ(),
                stack
        );

        if(beyonderMap.get(player).isEmpty()) return;

        var data = beyonderMap.get(player).get();
        BeyonderData.setBeyonder(player, data.pathway(), data.sequence());

        event.getDrops().add(itemEntity);
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {

            var source = event.getSource().getEntity();
            if(source != null){
                LOTMCraft.LOGGER.info("{} was killed by {} with {}", player.getGameProfile().getName(), event.getSource().getEntity().getName(), event.getSource());
            }
            else{
                LOTMCraft.LOGGER.info("{} was killed with {}", player.getGameProfile().getName(),event.getSource());
            }


            if (!BeyonderData.isBeyonder(player)) return;
            if(beyonderMap.get(player).isEmpty()) return;
            if (!player.level().getGameRules().getBoolean(ModGameRules.REGRESS_SEQUENCE_ON_DEATH)) return;

            StoredData data = beyonderMap.get(player).get();
            StoredData regressed = data.regressSeq(false);

            beyonderMap.put(player, regressed);
            player.getPersistentData().putFloat(BeyonderData.NBT_DIGESTION_PROGRESS, 1.0f);

            BeyonderData.recalculateCharStackModifiers(player);

            if (Objects.equals(regressed.sequence(), LOTMCraft.NON_BEYONDER_SEQ)) {
                ClientBeyonderCache.removePlayer(player.getUUID());
            } else
                ClientBeyonderCache.updateData(player.getUUID(), regressed.pathway(), regressed.sequence(),
                        0.0f, false, true, 0.0f);
        }
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getEntity().level().isClientSide()) return;

        var player = event.getEntity();

        if(player.isCreative())
            return;

        Objects.requireNonNull(player.getServer()).execute(() -> {
            var container = event.getContainer();

            for (Slot slot : container.slots) {
                ItemStack stack = slot.getItem();
                if (stack.isEmpty()) continue;

                Item item = stack.getItem();

                if (item instanceof PotionIngredient obj) {
                    for (var path : obj.getPathways()) {
                        if (!BeyonderData.beyonderMap.check(path, obj.getSequence())) {
                            slot.set(ItemStack.EMPTY);
                            break;
                        }
                    }
                }

                else if (item instanceof BeyonderPotion potion) {
                    if (!BeyonderData.beyonderMap.check(
                            potion.getPathway(), potion.getSequence())) {
                        slot.set(ItemStack.EMPTY);
                    }
                }

                else if (item instanceof BeyonderCharacteristicItem cha) {
                    if (!BeyonderData.beyonderMap.check(
                            cha.getPathway(), cha.getSequence())) {
                        slot.set(ItemStack.EMPTY);
                    }
                }

                else{
                    SealedArtifactData data = stack.get(ModDataComponents.SEALED_ARTIFACT_DATA.get());

                    boolean valid = true, allowed = true, noNegativesAllowed = true;
                    if(data != null) {
                        valid = beyonderMap.check(data.pathway(), data.sequence());
                        allowed = player.level().getGameRules().getBoolean(ModGameRules.ALLOW_ARTIFACTS);

                        noNegativesAllowed = !player.level().getGameRules().getBoolean(ModGameRules.
                                ALLOW_ARTIFACTS_WITH_NO_NEGATIVES) && data.negativeEffect().isEmpty();
                    }

                    if (data != null && (!valid || !allowed || noNegativesAllowed)) {
                        slot.set(ItemStack.EMPTY);
                    }
                }
            }

            container.broadcastChanges();
        });
    }

    /**
     * Sun Pathway seq ≤ 3: hits reduce the victim's digestion.
     * Direct hit (attacker == direct entity): 3% base, ±1% per sequence difference.
     * Indirect hit (projectile / AoE): 0.5% flat.
     * If digestion hits 0, each hit has a 10% chance to regress the victim by 1 sequence
     * and give the attacker the corresponding characteristic item.
     */
    @SubscribeEvent
    public static void onSunHitDigestion(LivingDamageEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;

        // Attacker must be a Sun Pathway Beyonder at seq 3 or stronger
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;
        if (!BeyonderData.isBeyonder(attacker)) return;

        int attackerSeq;
        if (BeyonderData.getPathway(attacker).equals("sun")) {
            attackerSeq = BeyonderData.getSequence(attacker);
            if (attackerSeq > 3) return;
        } else {
            // Allow shared Sun abilities: attacker must have a Sun team member (seq <= 3) who contributed an ability.
            if (!(attacker instanceof ServerPlayer attackerPlayer)) return;
            attackerSeq = getSunContributorSeq(attackerPlayer);
            if (attackerSeq < 0) return;
        }

        // Victim must be a Beyonder Player with digestion
        LivingEntity victim = event.getEntity();
        if (!(victim instanceof Player victimPlayer)) return;
        if (!BeyonderData.isBeyonder(victim)) return;
        if (victim.level().isClientSide()) return;

        int victimSeq = BeyonderData.getSequence(victim);

        // Indirect = ticking AoEs (PURIFICATION_INDIRECT). Everything else — melee, projectiles,
        // spawned entities — counts as direct (PURIFICATION or any other damage type).
        boolean isDirect = !event.getSource().is(ModDamageTypes.PURIFICATION_INDIRECT);

        // seqDiff > 0 means attacker is stronger (lower seq number), < 0 means weaker
        int seqDiff = victimSeq - attackerSeq;

        float digestionDrain;
        if (isDirect) {
            // Base 3%, +1% per level attacker is stronger, -1% per level attacker is weaker, floor 1%
            digestionDrain = Math.max(0.01f, 0.03f + seqDiff * 0.01f);
        } else {
            // Base 0.5%, +0.1% per level attacker is stronger, -0.1% per level attacker is weaker, floor 0.1%
            digestionDrain = Math.max(0.001f, 0.005f + seqDiff * 0.001f);
        }

        float currentDigestion = BeyonderData.getDigestionProgress(victimPlayer);
        float newDigestion = Math.max(0f, currentDigestion - digestionDrain);
        victimPlayer.getPersistentData().putFloat(BeyonderData.NBT_DIGESTION_PROGRESS, newDigestion);
        if (victim instanceof ServerPlayer sp) {
            PacketHandler.syncBeyonderDataToPlayer(sp);
        }

        // If digestion is fully drained, 10% chance to regress victim and reward attacker
        if (newDigestion <= 0f && new Random().nextFloat() < 0.1f) {
            // Capture pathway before regression changes it — the dropped characteristic belongs to the old pathway/seq
            String pathwayBeforeRegress = BeyonderData.getPathway(victim);
            // Check if victim has a characteristic stack at their current sequence
            boolean hasStack = BeyonderData.beyonderMap != null
                    && BeyonderData.beyonderMap.get(victim.getUUID()).isPresent()
                    && BeyonderData.beyonderMap.get(victim.getUUID()).get().charStack().get(victimSeq) > 0;

            if (hasStack) {
                // Consume one stack instead of desequencing
                BeyonderData.setCharStack(victim, victimSeq,
                        BeyonderData.beyonderMap.get(victim.getUUID()).get().charStack().get(victimSeq) - 1, true);
            } else {
                // No stack — desequence the victim, using regressSeq so domain-switched players restore to their previous pathway
                if (victim instanceof ServerPlayer sp && BeyonderData.beyonderMap.get(sp).isPresent()) {
                    StoredData regressed = BeyonderData.beyonderMap.get(sp).get().regressSeq();
                    BeyonderData.beyonderMap.put(sp, regressed);
                    BeyonderData.setBeyonder(victim, regressed.pathway(), regressed.sequence());
                } else {
                    BeyonderData.setBeyonder(victim, BeyonderData.getPathway(victim), victimSeq + 1);
                }
            }

            // Always give the attacker the corresponding characteristic item (not for void-summoned puppets or players possessing one)
            if (!victim.getPersistentData().getBoolean("VoidSummoned")) {
                BeyonderCharacteristicItem charItem = BeyonderCharacteristicItemHandler
                        .selectCharacteristicOfPathwayAndSequence(pathwayBeforeRegress, victimSeq);
                if (charItem != null && attacker instanceof Player attackerPlayer) {
                    attackerPlayer.getInventory().add(new ItemStack(charItem.asItem()));
                }
            }

            // Either way, reset digestion to full so the victim isn't immediately vulnerable again
            victimPlayer.getPersistentData().putFloat(BeyonderData.NBT_DIGESTION_PROGRESS, 1.0f);
            if (victim instanceof ServerPlayer sp) PacketHandler.syncBeyonderDataToPlayer(sp);
        }
    }

    /**
     * Returns the sequence of the strongest Sun contributor (seq <= 3) sharing abilities with the given player,
     * or -1 if no such contributor exists.
     */
    private static int getSunContributorSeq(ServerPlayer player) {
        if (player.getServer() == null) return -1;
        TeamComponent team = player.getData(ModAttachments.TEAM_COMPONENT.get());

        // Collect all member UUIDs to check — if player is a team member, check other members + leader;
        // if player is the leader, check their members.
        java.util.List<String> toCheck = new java.util.ArrayList<>();
        if (team.isInTeam()) {
            // player is a member — the leader UUID and other members contributed abilities accessible to the leader
            toCheck.add(team.leaderUUID());
            ServerPlayer leader = player.getServer().getPlayerList().getPlayer(
                    java.util.UUID.fromString(team.leaderUUID()));
            if (leader != null) {
                toCheck.addAll(leader.getData(ModAttachments.TEAM_COMPONENT.get()).memberUUIDs());
            }
        } else {
            toCheck.addAll(team.memberUUIDs());
        }

        int best = -1;
        for (String uuid : toCheck) {
            ServerPlayer member = player.getServer().getPlayerList().getPlayer(java.util.UUID.fromString(uuid));
            if (member == null) continue;
            if (!BeyonderData.isBeyonder(member)) continue;
            if (!BeyonderData.getPathway(member).equals("sun")) continue;
            int seq = BeyonderData.getSequence(member);
            if (seq > 3) continue;
            // Check that this member has actually contributed at least one ability to the team
            SharedAbilitiesComponent shared = member.getData(ModAttachments.SHARED_ABILITIES_COMPONENT.get());
            String leaderUUID = team.isInTeam() ? team.leaderUUID() : player.getStringUUID();
            if (shared.getContributions(leaderUUID).isEmpty()) continue;
            if (best < 0 || seq < best) best = seq;
        }
        return best;
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        DamageSource source = event.getSource();

        if(!(victim instanceof Player)) return;

        if (source.getEntity() instanceof ServerPlayer player) {
            if (player.level().isClientSide) return;

            if(!BeyonderData.isBeyonder(player) || !BeyonderData.isBeyonder(victim)) return;

            BeyonderData.recalculateCharStackModifiers(player);

            float diff = BeyonderData.getSequence(player) - BeyonderData.getSequence(victim);

            if(diff >= 0){
                BeyonderData.digest(player, (0.01f + (diff * 0.1f)), true);
            }

           victim.addEffect(new MobEffectInstance(ModEffects.CONCEALMENT, 20 * 30, 99, false, false, false));
        }
    }



}