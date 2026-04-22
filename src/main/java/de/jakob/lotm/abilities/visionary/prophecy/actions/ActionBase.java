package de.jakob.lotm.abilities.visionary.prophecy.actions;

import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.actions.implementations.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Saddleable;
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

    public abstract int getRequiredSeq();

    abstract public void action(Level level, LivingEntity entity);

    public static ActionBase load(ActionsEnum type, CompoundTag tag, HolderLookup.Provider provider) {
        return switch (type) {
            case DROP_ITEM -> DropItemAction.load(tag, provider);
            case TELEPORT -> TeleportAction.load(tag, provider);
            case DIGESTION -> DigestionAction.load(tag, provider);
            case HEALTH -> HealthAction.load(tag, provider);
            case SANITY -> SanityAction.load(tag, provider);
        };
    }

    public static ActionBase create(ActionsEnum type, ActionContextBase context){
        return switch (type){
            case DROP_ITEM -> new DropItemAction(context);
            case TELEPORT -> new TeleportAction(context);
            case DIGESTION -> new DigestionAction(context);
            case HEALTH -> new HealthAction(context);
            case SANITY -> new SanityAction(context);
        };
    }
}
