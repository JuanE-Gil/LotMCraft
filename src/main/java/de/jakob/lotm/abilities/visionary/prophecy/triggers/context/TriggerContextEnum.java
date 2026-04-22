package de.jakob.lotm.abilities.visionary.prophecy.triggers.context;

import de.jakob.lotm.abilities.visionary.prophecy.actions.ActionsEnum;
import net.minecraft.nbt.CompoundTag;

public enum TriggerContextEnum {
    POSITION,
    ITEM,
    EMPTY
    ;

    public static TriggerContextEnum fromNBT(CompoundTag tag, String key) {
        String name = tag.getString(key);
        try {
            return TriggerContextEnum.valueOf(name);
        } catch (Exception e) {
            return POSITION; // fallback
        }
    }

    public void toNBT(CompoundTag tag, String key) {
        tag.putString(key, this.name());
    }
}
