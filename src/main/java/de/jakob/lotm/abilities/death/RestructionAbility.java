package de.jakob.lotm.abilities.death;

import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.util.helper.subordinates.SubordinateUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class RestructionAbility extends Ability {

    private static final int SKELETON_COUNT = 10;
    private static final int ZOMBIE_COUNT = 10;

    public RestructionAbility(String id) {
        super(id, 20 * 60 * 3); // 3-minute cooldown
        canBeCopied = false;
        canBeReplicated = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("death", 6));
    }

    @Override
    protected float getSpiritualityCost() {
        return 500;
    }

    @Override
    public void onAbilityUse(Level level, LivingEntity entity) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        for (int i = 0; i < SKELETON_COUNT; i++) {
            Vec3 spawnPos = findSpawnPos(entity, serverLevel);

            Skeleton skeleton = new Skeleton(net.minecraft.world.entity.EntityType.SKELETON, serverLevel);
            skeleton.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
            skeleton.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
            skeleton.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
            skeleton.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
            // Drop chance of 0 so they don't drop the armor on death
            skeleton.setDropChance(EquipmentSlot.HEAD, 0f);
            skeleton.setDropChance(EquipmentSlot.CHEST, 0f);
            skeleton.setDropChance(EquipmentSlot.LEGS, 0f);
            skeleton.setDropChance(EquipmentSlot.FEET, 0f);
            serverLevel.addFreshEntity(skeleton);
            SubordinateUtils.turnEntityIntoSubordinate(skeleton, entity);
        }

        for (int i = 0; i < ZOMBIE_COUNT; i++) {
            Vec3 spawnPos = findSpawnPos(entity, serverLevel);

            Zombie zombie = new Zombie(net.minecraft.world.entity.EntityType.ZOMBIE, serverLevel);
            zombie.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
            zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
            zombie.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
            zombie.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
            zombie.setDropChance(EquipmentSlot.HEAD, 0f);
            zombie.setDropChance(EquipmentSlot.CHEST, 0f);
            zombie.setDropChance(EquipmentSlot.LEGS, 0f);
            zombie.setDropChance(EquipmentSlot.FEET, 0f);
            serverLevel.addFreshEntity(zombie);
            SubordinateUtils.turnEntityIntoSubordinate(zombie, entity);
        }
    }

    /** Finds a valid spawn position near the caster with a bit of spread. */
    private Vec3 findSpawnPos(LivingEntity entity, ServerLevel level) {
        for (int attempt = 0; attempt < 10; attempt++) {
            double offsetX = random.nextDouble(-4, 4);
            double offsetZ = random.nextDouble(-4, 4);
            Vec3 candidate = entity.position().add(offsetX, 0, offsetZ);

            // Try to find solid ground
            net.minecraft.core.BlockPos blockPos = net.minecraft.core.BlockPos.containing(candidate);
            while (blockPos.getY() > level.getMinBuildHeight() &&
                    level.getBlockState(blockPos).isAir()) {
                blockPos = blockPos.below();
            }

            if (!level.getBlockState(blockPos).isAir()) {
                return Vec3.atBottomCenterOf(blockPos.above());
            }
        }
        // Fallback: spawn directly at caster's position
        return entity.position();
    }
}
