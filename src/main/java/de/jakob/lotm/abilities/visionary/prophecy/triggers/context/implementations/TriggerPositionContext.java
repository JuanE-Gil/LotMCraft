package de.jakob.lotm.abilities.visionary.prophecy.triggers.context.implementations;

import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextEnum;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.implementations.ActionPositionContext;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.TriggerContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.TriggerContextEnum;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class TriggerPositionContext extends TriggerContextBase {
    public Vec3 pos;

    public TriggerPositionContext(UUID entityId, Vec3 pos) {
        super(entityId);

        this.pos = pos;
    }

    @Override
    public TriggerContextEnum getType() {
        return TriggerContextEnum.POSITION;
    }

    @Override
    public CompoundTag toNBT() {
        var tag = super.toNBT();

        tag.putDouble("x", pos.x);
        tag.putDouble("y", pos.y);
        tag.putDouble("z", pos.z);
        return tag;
    }

    public static TriggerPositionContext load(CompoundTag tag, UUID id) {
        return new TriggerPositionContext(id, new Vec3(
                tag.getDouble("x"),
                tag.getDouble("y"),
                tag.getDouble("z")
        ));
    }
}
