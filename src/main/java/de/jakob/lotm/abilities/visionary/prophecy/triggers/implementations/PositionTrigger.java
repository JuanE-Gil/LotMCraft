package de.jakob.lotm.abilities.visionary.prophecy.triggers.implementations;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.abyss.LanguageOfFoulnessAbility;
import de.jakob.lotm.abilities.visionary.prophecy.actions.ActionBase;
import de.jakob.lotm.abilities.visionary.prophecy.actions.ActionsEnum;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.TriggerBase;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.TriggerEnum;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.TriggerContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.TriggerContextEnum;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.implementations.TriggerPositionContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PositionTrigger extends TriggerBase {
    public PositionTrigger(ActionBase action, TriggerContextBase context) {
        super(action, context);
    }

    @Override
    public TriggerEnum getType() {
        return TriggerEnum.POSITION;
    }

    @Override
    public int getRequiredSeq() {
        return 7;
    }

    @Override
    public boolean checkTrigger(Level level, LivingEntity entity) {
        if (!(context instanceof TriggerPositionContext position)) return true;

        Vec3 pos = entity.position();
        Vec3 target = position.pos;

        double dist = pos.distanceTo(target);

        if (dist <= position.range) {
            action.action(level, entity);

            return true;
        }

        return false;
    }

    public static PositionTrigger load(CompoundTag tag,
                                       ActionsEnum actionType,
                                       TriggerContextEnum contextType,
                                       HolderLookup.Provider provider){
        return new PositionTrigger(ActionBase.load(actionType, tag, provider),
                TriggerContextBase.load(contextType, tag, provider));
    }
}
