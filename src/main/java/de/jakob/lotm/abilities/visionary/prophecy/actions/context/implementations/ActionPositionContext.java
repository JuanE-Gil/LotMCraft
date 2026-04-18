package de.jakob.lotm.abilities.visionary.prophecy.actions.context.implementations;

import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextEnum;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class ActionPositionContext extends ActionContextBase {
    public Vec3 pos;

    public ActionPositionContext(UUID id, Vec3 pos){
        super(id);
        this.pos = pos;
    }

    @Override
    public ActionContextEnum getType() {
        return ActionContextEnum.POSITION;
    }

    @Override
    public CompoundTag toNBT() {
        var tag = super.toNBT();

        tag.putDouble("x", pos.x);
        tag.putDouble("y", pos.y);
        tag.putDouble("z", pos.z);
        return tag;
    }

    public static ActionPositionContext load(CompoundTag tag, UUID id) {
        return new ActionPositionContext(id, new Vec3(
                tag.getDouble("x"),
                tag.getDouble("y"),
                tag.getDouble("z")
        ));
    }

}
