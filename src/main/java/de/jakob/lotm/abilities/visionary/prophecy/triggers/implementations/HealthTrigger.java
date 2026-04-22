package de.jakob.lotm.abilities.visionary.prophecy.triggers.implementations;

import de.jakob.lotm.abilities.visionary.prophecy.actions.ActionBase;
import de.jakob.lotm.abilities.visionary.prophecy.actions.ActionsEnum;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.TriggerBase;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.TriggerEnum;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.TriggerContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.TriggerContextEnum;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.implementations.TriggerNumbersContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class HealthTrigger extends TriggerBase {
    public HealthTrigger(ActionBase action, TriggerContextBase context) {
        super(action, context);
    }

    @Override
    public TriggerEnum getType() {
        return TriggerEnum.HEALTH;
    }

    @Override
    public int getRequiredSeq() {
        return 4;
    }

    @Override
    public boolean checkTrigger(Level level, LivingEntity entity) {
        if(!(context instanceof TriggerNumbersContext numbers)) return true;

        float value = numbers.isInt ? numbers.intValue : (float) numbers.doubleValue;
        float health = entity.getHealth();

        if(switch (numbers.operation) {
            case -2 -> value < health;
            case -1 -> value <= health;
            case 0 -> value == health;
            case 1 -> value >= health;
            case 2 -> value > health;
            default -> true;
        }){
            action.action(level, entity);
            return true;
        }

        return false;
    }

    public static HealthTrigger load(CompoundTag tag,
                                      ActionsEnum actionType,
                                      TriggerContextEnum contextType,
                                      HolderLookup.Provider provider){
        return new HealthTrigger(ActionBase.load(actionType, tag, provider),
                TriggerContextBase.load(contextType, tag, provider));
    }
}
