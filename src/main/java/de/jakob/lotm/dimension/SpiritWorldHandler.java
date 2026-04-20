package de.jakob.lotm.dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


public class SpiritWorldHandler {

    public static Vec3 getCoordinatesInSpiritWorld(Vec3 origin, Level level) {
        double x = origin.x * Math.sin(1 / origin.x);
        double y = Math.max(10, origin.y);
        double z = origin.z * Math.cos(1 / origin.z);

        return level.getWorldBorder().clampToBounds(BlockPos.containing(x, y, z)).getCenter();
    }

    public static Vec3 getCoordinatesInOverworld(Vec3 origin, Level level) {
        double x = origin.x / Math.sin(1 / origin.x);
        double y = origin.y;
        double z = origin.z / Math.cos(1 / origin.z);

        return level.getWorldBorder().clampToBounds(BlockPos.containing(x, y, z)).getCenter();
    }

}
