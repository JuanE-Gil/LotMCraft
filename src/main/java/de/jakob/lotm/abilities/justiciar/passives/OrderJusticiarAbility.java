package de.jakob.lotm.abilities.justiciar.passives;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.PassiveAbilityHandler;
import de.jakob.lotm.abilities.PassiveAbilityItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = LOTMCraft.MOD_ID)
public class OrderJusticiarAbility extends PassiveAbilityItem {

    public OrderJusticiarAbility(Properties properties) {
        super(properties);
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("justiciar", 9));
    }

    @Override
    public void tick(Level level, LivingEntity entity) {
        // Detection is handled via the event subscriber below
    }

    @SubscribeEvent
    public static void onEntityKilled(LivingDeathEvent event) {
        // Only trigger when a player commits murder (kills another player)
        if (!(event.getSource().getEntity() instanceof Player killer)) return;
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(killer.level() instanceof ServerLevel serverLevel)) return;

        Component message = Component.literal("[Order] ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal(killer.getDisplayName().getString())
                        .withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" has slain ")
                        .withStyle(ChatFormatting.GRAY))
                .append(Component.literal(victim.getDisplayName().getString())
                        .withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" nearby.")
                        .withStyle(ChatFormatting.GRAY));

        OrderJusticiarAbility orderAbility = (OrderJusticiarAbility) PassiveAbilityHandler.ORDER_JUSTICIAR.get();

        List<ServerPlayer> allPlayers = serverLevel.getServer().getPlayerList().getPlayers();
        for (ServerPlayer observer : allPlayers) {
            if (!observer.level().equals(serverLevel)) continue;
            if (!orderAbility.shouldApplyTo(observer)) continue;
            if (observer.distanceTo(killer) > 40.0) continue;

            observer.sendSystemMessage(message);
        }
    }
}