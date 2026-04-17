package de.jakob.lotm.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.authlib.GameProfile;
import de.jakob.lotm.util.helper.AllyUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

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
        );
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
