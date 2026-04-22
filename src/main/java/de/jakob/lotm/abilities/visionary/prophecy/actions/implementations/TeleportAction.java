package de.jakob.lotm.abilities.visionary.prophecy.actions.implementations;

import de.jakob.lotm.abilities.visionary.prophecy.actions.ActionBase;
import de.jakob.lotm.abilities.visionary.prophecy.actions.ActionsEnum;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextEnum;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.implementations.ActionItemsContext;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.implementations.ActionPositionContext;
import de.jakob.lotm.util.TeleportationUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class TeleportAction extends ActionBase {
    public TeleportAction(ActionContextBase context) {
        super(context);
    }

    @Override
    public ActionsEnum getType() {
        return ActionsEnum.TELEPORT;
    }

    @Override
    public int getRequiredSeq() {
        return 0;
    }

    @Override
    public void action(Level level, LivingEntity entity) {
        if (!(entity instanceof ServerPlayer serverPlayer)) return;

        if(!(context instanceof ActionPositionContext position)) return;

        var validated = TeleportationUtil.clampToBorder((ServerLevel) serverPlayer.level(), position.pos);
        entity.teleportTo(validated.x, validated.y, validated.z);
    }

    public static TeleportAction load(CompoundTag tag, HolderLookup.Provider provider) {
        return new TeleportAction(ActionContextBase.load(ActionContextEnum.POSITION, tag, provider));
    }
}
