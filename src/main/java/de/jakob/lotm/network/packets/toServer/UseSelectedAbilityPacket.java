package de.jakob.lotm.network.packets.toServer;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.attachments.AbilityWheelComponent;
import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.attachments.TeamComponent;
import de.jakob.lotm.attachments.SharedAbilitiesComponent;
import de.jakob.lotm.effect.ModEffects;
import de.jakob.lotm.util.BeyonderData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UseSelectedAbilityPacket() implements CustomPacketPayload {

    public static final Type<UseSelectedAbilityPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LOTMCraft.MOD_ID, "use_selected_ability"));

    public static final StreamCodec<ByteBuf, UseSelectedAbilityPacket> STREAM_CODEC = StreamCodec.unit(new UseSelectedAbilityPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UseSelectedAbilityPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {

                // Fooling effect: 25% chance to fail entirely, otherwise scramble to a random ability.
                if (serverPlayer.hasEffect(ModEffects.FOOLING)) {
                    if (new java.util.Random().nextFloat() < 0.25f) return;
                    if (serverPlayer.level() instanceof ServerLevel serverLevel) {
                        String pathway = BeyonderData.getPathway(serverPlayer);
                        int sequence   = BeyonderData.getSequence(serverPlayer);
                        Ability randomAbility = LOTMCraft.abilityHandler.getRandomAbility(
                                pathway, sequence, new java.util.Random(), false, java.util.Collections.emptyList());
                        if (randomAbility != null) {
                            randomAbility.useAbility(serverLevel, serverPlayer);
                        }
                    }
                    return;
                }

                AbilityWheelComponent component = serverPlayer.getData(ModAttachments.ABILITY_WHEEL_COMPONENT);

                if (component.getAbilities().isEmpty()) {
                    return;
                }

                int selectedIndex = component.getSelectedAbility();
                if (selectedIndex >= 0 && selectedIndex < component.getAbilities().size()) {
                    String abilityId = component.getAbilities().get(selectedIndex);
                    Ability ability = LOTMCraft.abilityHandler.getById(abilityId);

                    if (ability != null && serverPlayer.level() instanceof ServerLevel serverLevel) {
                        boolean isSharedAbility = isSharedAbility(serverPlayer, abilityId);
                        ability.useAbility(serverLevel, serverPlayer, true, !isSharedAbility, true);
                    }
                }
            }
        });
    }

    private static boolean isSharedAbility(ServerPlayer player, String abilityId) {
        TeamComponent team = player.getData(ModAttachments.TEAM_COMPONENT.get());
        if (player.getServer() == null) return false;

        if (!team.isInTeam()) {
            // Player is the leader — check all members' contributions keyed by own UUID
            for (String memberUUID : team.memberUUIDs()) {
                ServerPlayer member = player.getServer().getPlayerList().getPlayer(
                        java.util.UUID.fromString(memberUUID));
                if (member == null) continue;
                SharedAbilitiesComponent shared = member.getData(ModAttachments.SHARED_ABILITIES_COMPONENT.get());
                if (shared.getContributions(player.getStringUUID()).contains(abilityId)) return true;
            }
            return false;
        }

        // Player is a member — check all other members' contributions keyed by the leader UUID
        ServerPlayer leader = player.getServer().getPlayerList().getPlayer(
                java.util.UUID.fromString(team.leaderUUID()));
        if (leader == null) return false;

        TeamComponent leaderTeam = leader.getData(ModAttachments.TEAM_COMPONENT.get());
        for (String memberUUID : leaderTeam.memberUUIDs()) {
            if (memberUUID.equals(player.getStringUUID())) continue; // skip own contributions
            ServerPlayer member = player.getServer().getPlayerList().getPlayer(
                    java.util.UUID.fromString(memberUUID));
            if (member == null) continue;
            SharedAbilitiesComponent shared = member.getData(ModAttachments.SHARED_ABILITIES_COMPONENT.get());
            if (shared.getContributions(team.leaderUUID()).contains(abilityId)) return true;
        }
        return false;
    }
}