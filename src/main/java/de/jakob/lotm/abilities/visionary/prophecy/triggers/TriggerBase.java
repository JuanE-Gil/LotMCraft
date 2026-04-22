package de.jakob.lotm.abilities.visionary.prophecy.triggers;

import de.jakob.lotm.abilities.visionary.prophecy.actions.ActionBase;
import de.jakob.lotm.abilities.visionary.prophecy.actions.ActionsEnum;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextEnum;
import de.jakob.lotm.abilities.visionary.prophecy.actions.implementations.DropItemAction;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.TriggerContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.TriggerContextEnum;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.implementations.TriggerPositionContext;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.implementations.HasItemTrigger;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.implementations.InstantTrigger;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.implementations.PositionTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.UUID;


public abstract class  TriggerBase {
    public static final String ACTION = "action";
    public static final String CONTEXT = "context";
    public static final String ACTION_TYPE = "action_type";
    public static final String CONTEXT_TYPE = "context_type";
    public static final String TIMESTAMP = "timestamp";

    protected final ActionBase action;
    protected final TriggerContextBase context;

    public TriggerBase(ActionBase action, TriggerContextBase context){
        this.action = action;
        this.context = context;
    }

    public abstract TriggerEnum getType();

    public CompoundTag toNBT(HolderLookup.Provider provider){
        CompoundTag tag = new CompoundTag();

        tag.put(ACTION, action.toNBT(provider));
        tag.put(CONTEXT, context.toNBT(provider));
        action.getType().toNBT(tag, ACTION_TYPE);
        context.getType().toNBT(tag, CONTEXT_TYPE);

        return tag;
    }

    public UUID getTarget(){
        return context.getTargetId();
    }

    public int getActionRequiredSeq(){
        return action.getRequiredSeq();
    }

    public ActionsEnum getActionType(){
        return action.getType();
    }

    public abstract int getRequiredSeq();

    public abstract boolean checkTrigger(Level level, LivingEntity entity);

    public static TriggerBase load(TriggerEnum type, CompoundTag tag, HolderLookup.Provider provider) {
        ActionsEnum actionType = ActionsEnum.fromNBT(tag, ACTION_TYPE);
        TriggerContextEnum contextType = TriggerContextEnum.fromNBT(tag, CONTEXT_TYPE);

        return switch (type) {
            case POSITION -> PositionTrigger.load(tag, actionType, contextType, provider);
            case PICK_UP -> HasItemTrigger.load(tag, actionType, contextType, provider);
            case INSTANT -> InstantTrigger.load(tag, actionType, contextType, provider);
        };
    }

    public static TriggerBase create(TriggerEnum type, ActionBase action, TriggerContextBase context){
        return switch (type){
            case POSITION -> new PositionTrigger(action, context);
            case PICK_UP -> new HasItemTrigger(action, context);
            case INSTANT -> new InstantTrigger(action, context);
        };
    }
}


