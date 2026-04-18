package de.jakob.lotm.abilities.visionary.prophecy.triggers.context;

import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextEnum;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.implementations.ActionPositionContext;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.implementations.TriggerPositionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public abstract class TriggerContextBase {
    public static final String TARGET_ID = "target_id";

    protected UUID targetId;

    public TriggerContextBase(LivingEntity entity){
        targetId = entity.getUUID();
    }

    public TriggerContextBase(UUID entityId){
        targetId = entityId;
    }

    public abstract TriggerContextEnum getType();

    public CompoundTag toNBT(){
        CompoundTag tag = new CompoundTag();
        tag.putUUID(TARGET_ID, targetId);
        return tag;
    }

    public static TriggerContextBase load(TriggerContextEnum type, CompoundTag tag) {
        UUID id = tag.getUUID(TARGET_ID);

        return switch (type) {
            case POSITION -> TriggerPositionContext.load(tag, id);
        };
    }
}
