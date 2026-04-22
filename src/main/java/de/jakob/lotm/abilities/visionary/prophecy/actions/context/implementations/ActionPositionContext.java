package de.jakob.lotm.abilities.visionary.prophecy.actions.context.implementations;

import de.jakob.lotm.abilities.visionary.prophecy.TokenStream;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextBase;
import de.jakob.lotm.abilities.visionary.prophecy.actions.context.ActionContextEnum;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.UUID;

public class ActionPositionContext extends ActionContextBase {
    public Vec3 pos;

    public ActionPositionContext(UUID id, Vec3 pos){
        super(id);
        this.pos = pos;
    }

    public ActionPositionContext(UUID id){
        super(id);
        this.pos = null;
    }

    @Override
    public ActionContextEnum getType() {
        return ActionContextEnum.POSITION;
    }

    @Override
    public ActionContextBase fillFromStream(TokenStream stream) {
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

    public static ActionPositionContext load(CompoundTag tag, UUID id, HolderLookup.Provider provider) {
        return new ActionPositionContext(id, new Vec3(
                tag.getDouble("x"),
                tag.getDouble("y"),
                tag.getDouble("z")
        ));
    }

}
