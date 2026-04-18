package de.jakob.lotm.abilities.visionary.prophecy.actions.context;

import de.jakob.lotm.abilities.visionary.prophecy.actions.context.implementations.ActionPositionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public abstract class ActionContextBase {
    public static final String TARGET_ID = "target_id";

    protected UUID targetId;

    public ActionContextBase(LivingEntity entity){
        targetId = entity.getUUID();
    }

    public ActionContextBase(UUID entityId){
        targetId = entityId;
    }

    public abstract ActionContextEnum getType();

    public CompoundTag toNBT(){
        CompoundTag tag = new CompoundTag();
        tag.putUUID(TARGET_ID, targetId);
        return tag;
    }

    public static ActionContextBase load(ActionContextEnum type, CompoundTag tag) {
        UUID id = tag.getUUID(TARGET_ID);

        return switch (type) {
            case POSITION -> ActionPositionContext.load(tag, id);
        };
    }
}
