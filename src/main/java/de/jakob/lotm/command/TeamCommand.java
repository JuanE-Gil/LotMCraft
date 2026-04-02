package de.jakob.lotm.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.attachments.TeamComponent;
import de.jakob.lotm.network.PacketHandler;
import de.jakob.lotm.network.packets.toClient.PendingTeamInvitePacket;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.helper.TeamInviteManager;
import de.jakob.lotm.util.helper.TeamUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeamCommand {

    // Pending invites: leaderUUID -> (targetUUID -> expiry time ms)
    private static final Map<UUID, Map<UUID, Long>> pendingInvites = new ConcurrentHashMap<>();
    private static final long INVITE_TIMEOUT = 30_000L;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(Commands.literal("rteam")
                // /team add <player> — leader invites a player
                .then(Commands.literal("add")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> {
                                    ServerPlayer leader = context.getSource().getPlayerOrException();
                                    ServerPlayer target = EntityArgument.getPlayer(context, "player");
                                    return executeAdd(context.getSource(), leader, target);
                                })
                        )
                )
                // /team remove <player> — leader removes a member, or member removes themselves
                .then(Commands.literal("remove")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> {
                                    ServerPlayer sender = context.getSource().getPlayerOrException();
                                    ServerPlayer target = EntityArgument.getPlayer(context, "player");
                                    return executeRemove(context.getSource(), sender, target);
                                })
                        )
                )
                // /team leave — member leaves their team
                .then(Commands.literal("leave")
                        .executes(context -> {
                            ServerPlayer sender = context.getSource().getPlayerOrException();
                            return executeLeave(context.getSource(), sender);
                        })
                )
                // /team disband — leader disbands the whole team
                .then(Commands.literal("disband")
                        .executes(context -> {
                            ServerPlayer sender = context.getSource().getPlayerOrException();
                            return executeDisband(context.getSource(), sender);
                        })
                )
        );
    }

    private static int executeAdd(CommandSourceStack source, ServerPlayer leader, ServerPlayer target) {
        if (!TeamUtils.isEligibleLeader(leader)) {
            source.sendFailure(Component.literal("Only Red Priest Beyonders at sequence 4 or higher can lead a team."));
            return 0;
        }
        if (leader.equals(target)) {
            source.sendFailure(Component.literal("You cannot invite yourself."));
            return 0;
        }

        TeamComponent leaderTeam = leader.getData(ModAttachments.TEAM_COMPONENT.get());
        int maxSize = TeamUtils.getMaxTeamSize(BeyonderData.getSequence(leader));

        if (leaderTeam.memberCount() >= maxSize) {
            source.sendFailure(Component.literal("Your team is full (" + maxSize + " members max at your sequence)."));
            return 0;
        }
        if (leaderTeam.hasMember(target.getStringUUID())) {
            source.sendFailure(Component.literal(target.getName().getString() + " is already in your team."));
            return 0;
        }

        TeamComponent targetTeam = target.getData(ModAttachments.TEAM_COMPONENT.get());
        if (targetTeam.isInTeam()) {
            source.sendFailure(Component.literal(target.getName().getString() + " is already in another team."));
            return 0;
        }

        // Check if target already sent an invite to the leader (auto-accept)
        if (hasPendingInvite(target.getUUID(), leader.getUUID())) {
            removePendingInvite(target.getUUID(), leader.getUUID());
            TeamUtils.addMember(leader, target);
            source.sendSuccess(() -> Component.literal("You and " + target.getName().getString() + " are now in the same team.").withStyle(s -> s.withColor(0x4CAF50)), false);
            target.sendSystemMessage(Component.literal("You joined " + leader.getName().getString() + "'s team.").withStyle(s -> s.withColor(0x4CAF50)));
            return 1;
        }

        addPendingInvite(leader.getUUID(), target.getUUID());
        PacketHandler.sendToPlayer(target, new PendingTeamInvitePacket(leader.getUUID(), leader.getName().getString()));
        source.sendSuccess(() -> Component.literal("Team invite sent to " + target.getName().getString() + ".").withStyle(s -> s.withColor(0x2196F3)), false);
        return 1;
    }

    private static int executeRemove(CommandSourceStack source, ServerPlayer sender, ServerPlayer target) {
        TeamComponent senderTeam = sender.getData(ModAttachments.TEAM_COMPONENT.get());

        // Allow the leader to remove a member
        if (senderTeam.hasMember(target.getStringUUID())) {
            TeamUtils.removeMember(sender, target);
            source.sendSuccess(() -> Component.literal("Removed " + target.getName().getString() + " from your team.").withStyle(s -> s.withColor(0xFF9800)), false);
            target.sendSystemMessage(Component.literal("You were removed from " + sender.getName().getString() + "'s team.").withStyle(s -> s.withColor(0xFF9800)));
            return 1;
        }

        source.sendFailure(Component.literal(target.getName().getString() + " is not in your team."));
        return 0;
    }

    private static int executeLeave(CommandSourceStack source, ServerPlayer sender) {
        TeamComponent senderTeam = sender.getData(ModAttachments.TEAM_COMPONENT.get());
        if (!senderTeam.isInTeam()) {
            source.sendFailure(Component.literal("You are not in a team."));
            return 0;
        }

        String leaderUUID = senderTeam.leaderUUID();
        ServerPlayer leader = source.getServer().getPlayerList().getPlayer(UUID.fromString(leaderUUID));

        if (leader != null) {
            TeamUtils.removeMember(leader, sender);
            leader.sendSystemMessage(Component.literal(sender.getName().getString() + " left your team.").withStyle(s -> s.withColor(0xFF9800)));
        } else {
            // Leader is offline — clean up manually and clear client data
            sender.setData(ModAttachments.TEAM_COMPONENT.get(), senderTeam.clearLeader());
            PacketHandler.sendToPlayer(sender, new de.jakob.lotm.network.packets.toClient.SyncSharedAbilitiesDataPacket(
                    "", new java.util.ArrayList<>(), new java.util.ArrayList<>(), new java.util.HashMap<>(), 0, 0));
        }

        source.sendSuccess(() -> Component.literal("You left the team.").withStyle(s -> s.withColor(0xFF9800)), false);
        return 1;
    }

    private static int executeDisband(CommandSourceStack source, ServerPlayer sender) {
        TeamComponent senderTeam = sender.getData(ModAttachments.TEAM_COMPONENT.get());
        if (senderTeam.memberCount() == 0 && !senderTeam.isInTeam()) {
            source.sendFailure(Component.literal("You do not have a team to disband."));
            return 0;
        }
        if (senderTeam.isInTeam()) {
            source.sendFailure(Component.literal("Only the team leader can disband the team. Use /team leave to leave."));
            return 0;
        }

        TeamUtils.disbandTeam(sender, source.getServer());
        source.sendSuccess(() -> Component.literal("Your team has been disbanded.").withStyle(s -> s.withColor(0xF44336)), false);
        return 1;
    }

    // ===== Invite management =====

    public static void acceptInvite(ServerPlayer accepter, UUID leaderUUID) {
        if (!hasPendingInvite(leaderUUID, accepter.getUUID())) {
            accepter.sendSystemMessage(Component.literal("No pending invite from that player.").withStyle(s -> s.withColor(0xF44336)));
            return;
        }
        removePendingInvite(leaderUUID, accepter.getUUID());

        ServerPlayer leader = accepter.getServer().getPlayerList().getPlayer(leaderUUID);
        if (leader == null) {
            accepter.sendSystemMessage(Component.literal("The leader is no longer online.").withStyle(s -> s.withColor(0xF44336)));
            return;
        }

        boolean added = TeamUtils.addMember(leader, accepter);
        if (!added) {
            accepter.sendSystemMessage(Component.literal("Could not join the team (it may be full or you are already in one).").withStyle(s -> s.withColor(0xF44336)));
            return;
        }

        accepter.sendSystemMessage(Component.literal("You joined " + leader.getName().getString() + "'s team.").withStyle(s -> s.withColor(0x4CAF50)));
        leader.sendSystemMessage(Component.literal(accepter.getName().getString() + " joined your team.").withStyle(s -> s.withColor(0x4CAF50)));
    }

    public static void declineInvite(ServerPlayer decliner, UUID leaderUUID) {
        removePendingInvite(leaderUUID, decliner.getUUID());
        decliner.sendSystemMessage(Component.literal("Team invite declined.").withStyle(s -> s.withColor(0xFF9800)));

        ServerPlayer leader = decliner.getServer().getPlayerList().getPlayer(leaderUUID);
        if (leader != null) {
            leader.sendSystemMessage(Component.literal(decliner.getName().getString() + " declined your team invite.").withStyle(s -> s.withColor(0xFF9800)));
        }
    }

    private static void addPendingInvite(UUID leader, UUID target) {
        pendingInvites.computeIfAbsent(leader, k -> new ConcurrentHashMap<>())
                .put(target, System.currentTimeMillis() + INVITE_TIMEOUT);
    }

    private static void removePendingInvite(UUID leader, UUID target) {
        Map<UUID, Long> map = pendingInvites.get(leader);
        if (map != null) {
            map.remove(target);
            if (map.isEmpty()) pendingInvites.remove(leader);
        }
    }

    private static boolean hasPendingInvite(UUID leader, UUID target) {
        cleanupExpired();
        Map<UUID, Long> map = pendingInvites.get(leader);
        if (map == null) return false;
        Long expiry = map.get(target);
        if (expiry == null) return false;
        if (System.currentTimeMillis() > expiry) {
            map.remove(target);
            return false;
        }
        return true;
    }

    private static void cleanupExpired() {
        long now = System.currentTimeMillis();
        pendingInvites.forEach((leader, targets) -> targets.entrySet().removeIf(e -> e.getValue() < now));
        pendingInvites.entrySet().removeIf(e -> e.getValue().isEmpty());
    }
}
