package de.jakob.lotm.abilities.death;

import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.abilities.core.interaction.InteractionHandler;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.data.Location;
import de.jakob.lotm.util.helper.AbilityUtil;
import de.jakob.lotm.util.helper.subordinates.SubordinateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RestructionAbility extends SelectableAbility {

    private static final int SKELETON_COUNT = 10;
    private static final int ZOMBIE_COUNT = 10;

    private static final String[] MODES = {
            "ability.lotmcraft.restruction.summon",
            "ability.lotmcraft.restruction.release"
    };

    /** Tracks all mobs summoned per player so they can be despawned. */
    private static final HashMap<UUID, List<Mob>> summonedMobs = new HashMap<>();

    public RestructionAbility(String id) {
        super(id, 10f);
        canBeCopied = false;
        cannotBeStolen = true;
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
    protected String[] getAbilityNames() {
        return MODES;
    }

    @Override
    public void useAbility(ServerLevel serverLevel, LivingEntity entity, boolean consumeSpirituality, boolean hasToHaveAbility, boolean hasToMeetRequirements) {
        // Release sub-ability bypasses cooldown and spirituality cost
        if (getSelectedAbilityIndex(entity.getUUID()) == 1) {
            onAbilityUse(serverLevel, entity);
            return;
        }
        super.useAbility(serverLevel, entity, consumeSpirituality, hasToHaveAbility, hasToMeetRequirements);
    }

    @Override
    protected void castSelectedAbility(Level level, LivingEntity entity, int selectedAbility) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (selectedAbility == 0 && InteractionHandler.isInteractionPossibleStrictlyHigher(new Location(entity.position(), serverLevel), "purification", BeyonderData.getSequence(entity), -1)) return;

        switch (selectedAbility) {
            case 0 -> summon(serverLevel, entity);
            case 1 -> release(entity);
        }
    }

    private void summon(ServerLevel serverLevel, LivingEntity entity) {
        List<Mob> mobs = summonedMobs.computeIfAbsent(entity.getUUID(), k -> new ArrayList<>());

        for (int i = 0; i < SKELETON_COUNT; i++) {
            Vec3 spawnPos = findSpawnPos(entity, serverLevel);

            Skeleton skeleton = new Skeleton(net.minecraft.world.entity.EntityType.SKELETON, serverLevel);
            skeleton.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
            skeleton.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
            skeleton.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
            skeleton.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
            skeleton.setDropChance(EquipmentSlot.HEAD, 0f);
            skeleton.setDropChance(EquipmentSlot.CHEST, 0f);
            skeleton.setDropChance(EquipmentSlot.LEGS, 0f);
            skeleton.setDropChance(EquipmentSlot.FEET, 0f);
            serverLevel.addFreshEntity(skeleton);
            SubordinateUtils.turnEntityIntoSubordinate(skeleton, entity, false);
            mobs.add(skeleton);
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
            SubordinateUtils.turnEntityIntoSubordinate(zombie, entity, false);
            mobs.add(zombie);
        }
    }

    private void release(LivingEntity entity) {
        List<Mob> mobs = summonedMobs.remove(entity.getUUID());
        if (mobs == null || mobs.isEmpty()) {
            AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.restruction.none_summoned").withColor(0xFF4444));
            return;
        }

        for (Mob mob : mobs) {
            if (mob.isAlive()) mob.discard();
        }

        AbilityUtil.sendActionBar(entity, Component.translatable("ability.lotmcraft.restruction.released").withColor(0x44FF44));
    }

    private Vec3 findSpawnPos(LivingEntity entity, ServerLevel level) {
        for (int attempt = 0; attempt < 10; attempt++) {
            double offsetX = random.nextDouble(-4, 4);
            double offsetZ = random.nextDouble(-4, 4);
            Vec3 candidate = entity.position().add(offsetX, 0, offsetZ);

            net.minecraft.core.BlockPos blockPos = net.minecraft.core.BlockPos.containing(candidate);
            while (blockPos.getY() > level.getMinBuildHeight() &&
                    level.getBlockState(blockPos).isAir()) {
                blockPos = blockPos.below();
            }

            if (!level.getBlockState(blockPos).isAir()) {
                return Vec3.atBottomCenterOf(blockPos.above());
            }
        }
        return entity.position();
    }
}
