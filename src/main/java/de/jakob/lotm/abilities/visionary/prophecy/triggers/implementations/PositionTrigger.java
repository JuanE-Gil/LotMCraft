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
    public void checkTrigger(Level level, LivingEntity entity) {
        LOTMCraft.LOGGER.info("context type: {}", context.getType());

        if (!(context instanceof TriggerPositionContext)) return;

        LOTMCraft.LOGGER.info("After if in trigger");

        Vec3 pos = entity.position();
        Vec3 target = ((TriggerPositionContext) context).pos;

        if (pos.distanceTo(target) < 0.5) {
            LOTMCraft.LOGGER.info("action in trigger");
            action.action(level, entity);
        }
    }

    public static PositionTrigger load(CompoundTag tag, ActionsEnum actionType, TriggerContextEnum contextType){
        return new PositionTrigger(ActionBase.load(actionType, tag), TriggerContextBase.load(contextType, tag));
    }
}
