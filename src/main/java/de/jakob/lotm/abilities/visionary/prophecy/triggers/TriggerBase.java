package de.jakob.lotm.abilities.visionary.prophecy.triggers;

import de.jakob.lotm.abilities.visionary.prophecy.actions.ActionBase;
import de.jakob.lotm.abilities.visionary.prophecy.actions.ActionsEnum;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextEnum;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.TriggerContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.TriggerContextEnum;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.implementations.TriggerPositionContext;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.implementations.PositionTrigger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.UUID;


public abstract class  TriggerBase {
    public static final String ACTION = "action";
    public static final String CONTEXT = "context";
    public static final String ACTION_TYPE = "action_type";
    public static final String CONTEXT_TYPE = "context_type";

    protected final ActionBase action;
    protected final TriggerContextBase context;

    public TriggerBase(ActionBase action, TriggerContextBase context){
        this.action = action;
        this.context = context;
    }

    public abstract TriggerEnum getType();

    public CompoundTag toNBT(){
        CompoundTag tag = new CompoundTag();

        tag.put(ACTION, action.toNBT());
        tag.put(CONTEXT, context.toNBT());
        action.getType().toNBT(tag, ACTION_TYPE);
        context.getType().toNBT(tag, CONTEXT_TYPE);

        return tag;
    }

    public abstract void checkTrigger(Level level, LivingEntity entity);

    public static TriggerBase load(TriggerEnum type, CompoundTag tag) {
        ActionsEnum actionType = ActionsEnum.fromNBT(tag, ACTION_TYPE);
        TriggerContextEnum contextType = TriggerContextEnum.fromNBT(tag, CONTEXT_TYPE);

        return switch (type) {
            case POSITION -> PositionTrigger.load(tag, actionType, contextType);
        };
    }
}


