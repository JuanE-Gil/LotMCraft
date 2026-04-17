package de.jakob.lotm.item.custom;

import de.jakob.lotm.attachments.ApotheosisComponent;
import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.damage.ModDamageTypes;
import de.jakob.lotm.gamerule.ModGameRules;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.beyonderMap.StoredData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

import static de.jakob.lotm.util.BeyonderData.beyonderMap;

public class UniquenessItem extends Item {

    private final String pathway;

    public UniquenessItem(Properties properties, String pathway) {
        super(properties);
        this.pathway = pathway;
    }

    public String getPathway() {
        return pathway;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.success(itemStack);
        }

        int color = BeyonderData.pathwayInfos.get(pathway).color();

        int charStackCount = BeyonderData.getCurrentCharStack(player);

        if(
                !pathway.equalsIgnoreCase(BeyonderData.getPathway(player)) ||
                BeyonderData.getSequence(player) != 1 ||
                charStackCount < level.getGameRules().getInt(ModGameRules.CHARSTACK_REQUIRED_FOR_APOTHEOSIS)
        ) {
            player.displayClientMessage(Component.translatable("lotm.uniqueness.fail").withColor(color), false);
            player.hurt(ModDamageTypes.source(level, ModDamageTypes.LOOSING_CONTROL), 1);
            player.setHealth(1);
            return InteractionResultHolder.fail(itemStack);
        }

        if(!level.dimension().equals(Level.OVERWORLD)) {
            player.displayClientMessage(Component.translatable("lotm.uniqueness.overworld").withColor(color), false);
            player.hurt(ModDamageTypes.source(level, ModDamageTypes.LOOSING_CONTROL), 1);
            player.setHealth(1);
            return InteractionResultHolder.fail(itemStack);
        }

        itemStack.consume(1, player);
        startApotheosis(player);

        return InteractionResultHolder.success(itemStack);
    }

    private void startApotheosis(Player player) {
        ApotheosisComponent apotheosisComponent = player.getData(ModAttachments.APOTHEOSIS_COMPONENT);
        if(apotheosisComponent.getApotheosisTicksLeft() > 0) return;

        player.level().players().forEach(p -> p.displayClientMessage(Component.literal(player.getName().getString()).append(Component.translatable("lotm.uniqueness.start")).withColor(BeyonderData.pathwayInfos.get(pathway).color()), false));

        apotheosisComponent.setApotheosisTicksLeftAndSync(20 * 60 * 5, (ServerLevel) player.level(), player);
        apotheosisComponent.setPathway(pathway);

        player.level().players().forEach(p -> p.playSound(SoundEvents.WITHER_SPAWN));
    }
}
