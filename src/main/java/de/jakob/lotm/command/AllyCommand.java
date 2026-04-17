package de.jakob.lotm.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.authlib.GameProfile;
import de.jakob.lotm.attachments.AllyComponent;
import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.util.helper.AllyUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.UUID;

public class AllyCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ally")
                .requires(source -> source.getPlayer() != null)
                .then(Commands.literal("remove")
                        .then(Commands.argument("player", GameProfileArgument.gameProfile())
                                .executes(context -> {
                                    ServerPlayer sender = context.getSource().getPlayerOrException();
                                    Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(context, "player");
                                    GameProfile profile = profiles.iterator().next();
                                    return executeRemove(context.getSource(), sender, profile);
                                })
                        )
                )
                .then(Commands.literal("list")
                        .executes(context -> {
                            ServerPlayer sender = context.getSource().getPlayerOrException();
                            return executeList(context.getSource(), sender);
                        })
                )
        );
    }

    private static int executeList(CommandSourceStack source, ServerPlayer sender) {
        AllyComponent comp = sender.getData(ModAttachments.ALLY_COMPONENT.get());

        if (!comp.hasAllies()) {
            source.sendSuccess(() -> Component.translatable("lotm.ally.list_empty").withColor(0x9E9E9E), false);
            return 1;
        }

        source.sendSuccess(() -> Component.translatable("lotm.ally.list_header", comp.allyCount()).withColor(0x2196F3), false);
        for (String uuidStr : comp.allies()) {
            try {
                UUID allyUUID = UUID.fromString(uuidStr);
                ServerPlayer online = source.getServer().getPlayerList().getPlayer(allyUUID);
                Component name = online != null
                        ? online.getName().copy().withColor(0x4CAF50)
                        : Component.literal(source.getServer().getProfileCache()
                                .get(allyUUID)
                                .map(GameProfile::getName)
                                .orElse(uuidStr)).withColor(0x9E9E9E);
                source.sendSuccess(() -> Component.literal("  - ").append(name), false);
            } catch (IllegalArgumentException ignored) {}
        }

        return 1;
    }

    private static int executeRemove(CommandSourceStack source, ServerPlayer sender, GameProfile targetProfile) {
        if (sender.getUUID().equals(targetProfile.getId())) {
            source.sendFailure(Component.translatable("lotm.ally.remove_self"));
            return 0;
        }

        if (!AllyUtil.isAlly(sender, targetProfile.getId())) {
            source.sendFailure(Component.translatable("lotm.ally.not_allies",
                    Component.literal(targetProfile.getName())));
            return 0;
        }

        // Check if target is online — remove both sides immediately if so
        ServerPlayer onlineTarget = source.getServer().getPlayerList().getPlayer(targetProfile.getId());
        if (onlineTarget != null) {
            AllyUtil.removeAllies(sender, onlineTarget);
        } else {
            // Target is offline — remove from sender's side only.
            // The stale entry on the offline player's list is pruned when they next log in.
            AllyUtil.removeAllyOneWay(sender, targetProfile.getId());
            sender.sendSystemMessage(Component.translatable("lotm.ally.removed",
                    Component.literal(targetProfile.getName())).withColor(0xFF9800));
        }

        return 1;
    }
}
