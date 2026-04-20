package de.jakob.lotm.abilities.visionary.prophecy.triggers.context.implementations;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.visionary.prophecy.TokenStream;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextEnum;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.implementations.ActionPositionContext;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.TriggerContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.triggers.context.TriggerContextEnum;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.UUID;

public class TriggerPositionContext extends TriggerContextBase {
    public Vec3 pos;

    public TriggerPositionContext(UUID entityId, Vec3 pos) {
        super(entityId);

        this.pos = pos;
    }

    public TriggerPositionContext(UUID id){
        super(id);

        this.pos = null;
    }


    @Override
    public TriggerContextEnum getType() {
        return TriggerContextEnum.POSITION;
    }

    @Override
    public TriggerContextBase fillFromStream(TokenStream stream) {
        if(stream.match("on"))
            stream.next();

        try{

            int x = Integer.parseInt(Objects.requireNonNull(stream.peek()));
            stream.next();
            int y = Integer.parseInt(Objects.requireNonNull(stream.peek()));
            stream.next();
            int z = Integer.parseInt(Objects.requireNonNull(stream.peek()));

            pos = new Vec3(x, y, z);
        }catch (NumberFormatException e){
            pos = Vec3.ZERO;
        }

        return this;
    }

    @Override
    public CompoundTag toNBT(HolderLookup.Provider provider) {
        var tag = super.toNBT(provider);

        tag.putDouble("x", pos.x);
        tag.putDouble("y", pos.y);
        tag.putDouble("z", pos.z);
        return tag;
    }

    public static TriggerPositionContext load(CompoundTag tag, UUID id, HolderLookup.Provider provider) {
        return new TriggerPositionContext(id, new Vec3(
                tag.getDouble("x"),
                tag.getDouble("y"),
                tag.getDouble("z")
        ));
    }
}
