package de.jakob.lotm.abilities.visionary.prophecy.actions;

import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.actions.implementations.DropItemAction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public abstract class ActionBase {
    public static final String CONTEXT = "action_contex";

    protected final ActionContextBase context;

    public ActionBase(ActionContextBase context){
        this.context = context;
    }

    abstract public ActionsEnum getType();

    public CompoundTag toNBT(HolderLookup.Provider provider){
        CompoundTag tag = new CompoundTag();
        tag.put(CONTEXT, context.toNBT(provider));
        return tag;
    }

    abstract public void action(Level level, LivingEntity entity);

    public static ActionBase load(ActionsEnum type, CompoundTag tag, HolderLookup.Provider providerg) {
        return switch (type) {
            case DROP_ITEM -> DropItemAction.load(tag, providerg);
        };
    }

    public static ActionBase create(ActionsEnum type, ActionContextBase context){
        return switch (type){
            case DROP_ITEM -> new DropItemAction(context);
        };
    }
}
