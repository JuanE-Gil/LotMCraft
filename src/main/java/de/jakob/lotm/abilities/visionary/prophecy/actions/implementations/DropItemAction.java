package de.jakob.lotm.abilities.visionary.prophecy.actions.implementations;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.visionary.prophecy.actions.ActionBase;
import de.jakob.lotm.abilities.visionary.prophecy.actions.ActionsEnum;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextEnum;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class DropItemAction extends ActionBase {
    public DropItemAction(ActionContextBase context) {
        super(context);
    }

    @Override
    public void action(Level level, LivingEntity entity) {
        LOTMCraft.LOGGER.info("Drop action after trigger");
        if (entity instanceof ServerPlayer serverPlayer) {
            LOTMCraft.LOGGER.info("DROP ALL");
            serverPlayer.getInventory().dropAll();
        }
    }

    @Override
    public ActionsEnum getType() {
        return ActionsEnum.DROP_ITEM;
    }

    public static DropItemAction load(CompoundTag tag) {
        return new DropItemAction(ActionContextBase.load(ActionContextEnum.POSITION, tag));
    }
}
