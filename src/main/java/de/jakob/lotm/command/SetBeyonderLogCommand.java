package de.jakob.lotm.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import de.jakob.lotm.util.SetBeyonderAuditLog;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public class SetBeyonderLogCommand {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 100;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setbeyonderlog")
            .requires(source -> source.hasPermission(2))
            .executes(context -> executeLog(context.getSource(), DEFAULT_LIMIT))
            .then(Commands.argument("count", IntegerArgumentType.integer(1, MAX_LIMIT))
                .executes(context -> executeLog(
                        context.getSource(),
                        IntegerArgumentType.getInteger(context, "count")
                ))
            )
        );
    }

    private static int executeLog(CommandSourceStack source, int limit) {
        ServerLevel level = source.getLevel();
        SetBeyonderAuditLog log = SetBeyonderAuditLog.get(level);

        List<SetBeyonderAuditLog.AuditEntry> entries = log.getRecent(limit);

        if (entries.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No /setbeyonder usage recorded yet."), false);
            return 0;
        }

        source.sendSuccess(() -> Component.literal(
                "--- /setbeyonder log (last " + entries.size() + " of " + log.totalEntries() + " total) ---"
        ), false);

        for (SetBeyonderAuditLog.AuditEntry entry : entries) {
            source.sendSuccess(() -> Component.literal(entry.format()), false);
        }

        return entries.size();
    }
}
