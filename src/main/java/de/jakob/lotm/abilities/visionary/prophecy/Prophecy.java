package de.jakob.lotm.abilities.visionary.prophecy;

import de.jakob.lotm.abilities.visionary.prophecy.triggers.TriggerBase;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.TriggerEnum;
import net.minecraft.nbt.CompoundTag;
import org.checkerframework.checker.units.qual.C;

import java.util.UUID;

public record Prophecy(UUID targetID, TriggerBase trigger, TriggerEnum triggerType) {
    public static final String TARGET_ID = "target_id";
    public static final String TRIGGER = "trigger";
    public static final String TRIGGER_TYPE = "trigger_type";

    public CompoundTag toNBT(){
        CompoundTag tag = new CompoundTag();

        tag.putUUID(TARGET_ID, targetID);
        tag.put(TRIGGER, trigger.toNBT());
        trigger.getType().toNBT(tag, TRIGGER_TYPE);

        return tag;
    }

    public static Prophecy fromNBT(CompoundTag tag){
        UUID id = tag.getUUID(TARGET_ID);
        var trigger = TriggerBase.load(TriggerEnum.fromNBT(tag, TRIGGER_TYPE), tag);

        return new Prophecy(id, trigger, trigger.getType());
    }

}
