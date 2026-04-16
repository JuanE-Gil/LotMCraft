package de.jakob.lotm.abilities.death;

import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.entity.custom.BeyonderNPCEntity;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.helper.subordinates.SubordinateUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.*;

@EventBusSubscriber
public class InternalUnderworldAbility extends SelectableAbility {

    private static final String STORED_SOULS_TAG = "InternalUnderworldSouls";
    private static final float CAPTURE_CHANCE = 0.5f;

    /** Maximum souls that can be stored, by sequence level. */
    private static int getMaxSouls(int sequence) {
        return switch (sequence) {
            case 5 -> 5;
            case 4 -> 15;
            case 3 -> 20;
            case 2 -> 35;
            case 1 -> 45;
            case 0 -> 53;
            default -> 5; // seq 6+ cannot use this ability, but safe fallback
        };
    }

    public InternalUnderworldAbility(String id) {
        super(id, 1);
        canBeCopied = false;
        canBeReplicated = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("death", 4));
    }

    @Override
    protected float getSpiritualityCost() {
        return 3000;
    }

    @Override
    protected String[] getAbilityNames() {
        return new String[]{
                "ability.lotmcraft.internal_underworld.summon",
                "ability.lotmcraft.internal_underworld.summon_all"
        };
    }

    @Override
    protected void castSelectedAbility(Level level, LivingEntity entity, int selectedAbility) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!(entity instanceof ServerPlayer player)) return;

        switch (selectedAbility) {
            case 0 -> summonSoul(serverLevel, player);
            case 1 -> summonAllSouls(serverLevel, player);
        }
    }

    // -------------------------------------------------------------------------
    // Kill event — capture beyonder souls on kill
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        LivingEntity dying = event.getEntity();
        if (dying instanceof Player) return; // don't capture players

        // Find the killer
        var source = event.getSource();
        var causeEntity = source.getEntity();
        if (causeEntity == null) causeEntity = source.getDirectEntity();
        if (!(causeEntity instanceof ServerPlayer player)) return;

        // Killer must be a Death pathway Beyonder with this ability
        if (!BeyonderData.isBeyonder(player)) return;
        if (!BeyonderData.getPathway(player).equals("death")) return;
        if (BeyonderData.getSequence(player) > 5) return; // Seq 5 or higher rank required

        // Dying entity must be a Beyonder weaker than the player (higher sequence = weaker)
        if (!BeyonderData.isBeyonder(dying)) return;
        int playerSeq = BeyonderData.getSequence(player);
        int targetSeq = BeyonderData.getSequence(dying);
        if (targetSeq <= playerSeq) {
            // Target is same or stronger — cannot absorb
            player.sendSystemMessage(Component.translatable("ability.lotmcraft.internal_underworld.too_strong")
                    .withStyle(ChatFormatting.DARK_RED));
            return;
        }

        // 50% chance to capture
        if (player.getRandom().nextFloat() >= CAPTURE_CHANCE) {
            player.sendSystemMessage(Component.translatable("ability.lotmcraft.internal_underworld.escaped")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }

        // Check capacity against sequence-based limit
        List<CompoundTag> stored = getStoredSouls(player);
        int maxSouls = getMaxSouls(playerSeq);
        if (stored.size() >= maxSouls) {
            player.sendSystemMessage(Component.translatable("ability.lotmcraft.internal_underworld.full")
                    .withStyle(ChatFormatting.RED));
            return;
        }

        // Save soul data
        CompoundTag soulData = new CompoundTag();
        soulData.putString("EntityType", EntityType.getKey(dying.getType()).toString());
        soulData.putString("DisplayName", dying.hasCustomName() ? dying.getCustomName().getString() : dying.getName().getString());
        soulData.putString("BeyonderPathway", BeyonderData.getPathway(dying));
        soulData.putInt("BeyonderSequence", targetSeq);

        // Save full NBT for reconstruction
        CompoundTag entityNBT = new CompoundTag();
        dying.save(entityNBT);
        entityNBT.remove("UUID");
        entityNBT.remove("Health"); // remove death health so it spawns at full health
        if (entityNBT.contains("neoforge:attachments")) {
            entityNBT.getCompound("neoforge:attachments").remove("lotmcraft:copied_inventory");
        }
        soulData.put("EntityNBT", entityNBT);

        if (dying instanceof BeyonderNPCEntity beyonderNPC) {
            soulData.putBoolean("IsBeyonderNPC", true);
            soulData.putString("NPCPathway", beyonderNPC.getPathway());
            soulData.putInt("NPCSequence", beyonderNPC.getSequence());
            soulData.putString("NPCSkin", beyonderNPC.getSkinName());
            soulData.putBoolean("NPCHostile", beyonderNPC.isHostile());
        } else {
            soulData.putBoolean("IsBeyonderNPC", false);
        }

        addStoredSoul(player, soulData);
        player.sendSystemMessage(Component.translatable("ability.lotmcraft.internal_underworld.captured",
                dying.getName().getString()).withStyle(ChatFormatting.DARK_AQUA));
    }

    // -------------------------------------------------------------------------
    // Summon — open GUI to pick a stored soul and summon it as a subordinate
    // -------------------------------------------------------------------------

    private void summonSoul(ServerLevel level, ServerPlayer player) {
        List<CompoundTag> storedSouls = getStoredSouls(player);

        if (storedSouls.isEmpty()) {
            player.sendSystemMessage(Component.translatable("ability.lotmcraft.internal_underworld.no_souls")
                    .withStyle(ChatFormatting.RED));
            return;
        }

        // Build container for soul selection (max 27 souls + 1 clear-mode button)
        SimpleContainer container = new SimpleContainer(54) {
            @Override
            public boolean canTakeItem(Container target, int index, ItemStack stack) {
                return false;
            }
        };

        // Slot 0: clear-mode toggle (barrier)
        ItemStack clearItem = new ItemStack(Items.BARRIER);
        clearItem.set(DataComponents.CUSTOM_NAME, Component.literal("Remove Soul").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        clearItem.set(DataComponents.LORE, new ItemLore(List.of(
                Component.literal("Click a soul after this to release it").withStyle(ChatFormatting.GRAY)
        )));
        CompoundTag clearTag = new CompoundTag();
        clearTag.putBoolean("IsDeleteMode", true);
        clearItem.set(DataComponents.CUSTOM_DATA, CustomData.of(clearTag));
        container.setItem(0, clearItem);

        for (int i = 0; i < Math.min(storedSouls.size(), 53); i++) {
            CompoundTag soul = storedSouls.get(i);
            container.setItem(i + 1, createSoulDisplayItem(soul));
        }

        final int containerSize = container.getContainerSize();

        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new ChestMenu(MenuType.GENERIC_9x6, id, inv, container, 6) {
                    private boolean isDeleting = false;

                    @Override
                    public void clicked(int slotId, int button, net.minecraft.world.inventory.ClickType clickType,
                                       net.minecraft.world.entity.player.Player clickPlayer) {
                        if (slotId < 0 || slotId >= containerSize) return;
                        ItemStack clicked = container.getItem(slotId);
                        if (clicked.isEmpty()) return;

                        CustomData customData = clicked.get(DataComponents.CUSTOM_DATA);
                        if (customData == null) return;
                        CompoundTag tag = customData.copyTag();

                        if (tag.contains("IsDeleteMode")) {
                            this.isDeleting = !this.isDeleting;
                            return;
                        }

                        if (!tag.contains("SoulData")) return;
                        CompoundTag soulData = tag.getCompound("SoulData");

                        if (isDeleting) {
                            removeStoredSoul(player, soulData);
                            player.closeContainer();
                            player.sendSystemMessage(Component.translatable("ability.lotmcraft.internal_underworld.released")
                                    .withStyle(ChatFormatting.GRAY));
                        } else {
                            player.closeContainer();
                            level.getServer().execute(() -> spawnSoulAsSubordinate(level, player, soulData));
                        }
                    }
                },
                Component.translatable("ability.lotmcraft.internal_underworld.select_soul")
        ));
    }

    // -------------------------------------------------------------------------
    // Summon All — spawns every stored soul at once up to the sequence cap
    // -------------------------------------------------------------------------

    private void summonAllSouls(ServerLevel level, ServerPlayer player) {
        List<CompoundTag> storedSouls = getStoredSouls(player);

        if (storedSouls.isEmpty()) {
            player.sendSystemMessage(Component.translatable("ability.lotmcraft.internal_underworld.no_souls")
                    .withStyle(ChatFormatting.RED));
            return;
        }

        int maxSouls = getMaxSouls(BeyonderData.getSequence(player));
        List<CompoundTag> toSummon = new ArrayList<>(storedSouls.subList(0, Math.min(storedSouls.size(), maxSouls)));

        int summoned = 0;
        for (CompoundTag soulData : toSummon) {
            try {
                spawnSoulAsSubordinate(level, player, soulData);
                summoned++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        player.sendSystemMessage(Component.translatable("ability.lotmcraft.internal_underworld.summoned_all", summoned)
                .withStyle(ChatFormatting.DARK_AQUA));
    }

    private ItemStack createSoulDisplayItem(CompoundTag soulData) {
        String displayName = soulData.getString("DisplayName");
        String pathway = soulData.getString("BeyonderPathway");
        int sequence = soulData.getInt("BeyonderSequence");

        ItemStack item = new ItemStack(Items.PLAYER_HEAD);
        item.set(DataComponents.CUSTOM_NAME, Component.literal(displayName).withStyle(ChatFormatting.LIGHT_PURPLE));
        item.set(DataComponents.LORE, new ItemLore(List.of(
                Component.literal("-------------------").withStyle(style -> style.withColor(0xFF7ECFCF).withItalic(false)),
                Component.translatable("lotm.pathway").append(Component.literal(": " + pathway)).withColor(0x7ECFCF).withStyle(style -> style.withItalic(false)),
                Component.translatable("lotm.sequence").append(Component.literal(": " + sequence)).withColor(0x7ECFCF).withStyle(style -> style.withItalic(false))
        )));

        CompoundTag tag = new CompoundTag();
        tag.put("SoulData", soulData);
        item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        return item;
    }

    private void spawnSoulAsSubordinate(ServerLevel level, ServerPlayer player, CompoundTag soulData) {
        try {
            String entityTypeId = soulData.getString("EntityType");
            Optional<EntityType<?>> optionalType = EntityType.byString(entityTypeId);

            if (optionalType.isEmpty()) {
                player.sendSystemMessage(Component.translatable("ability.lotmcraft.internal_underworld.failed")
                        .withStyle(ChatFormatting.RED));
                return;
            }

            EntityType<?> entityType = optionalType.get();
            net.minecraft.world.entity.Entity entity;

            if (soulData.getBoolean("IsBeyonderNPC")) {
                String pathway = soulData.getString("NPCPathway");
                int sequence = soulData.getInt("NPCSequence");
                String skin = soulData.getString("NPCSkin");
                boolean hostile = soulData.getBoolean("NPCHostile");

                entity = new BeyonderNPCEntity(
                        (EntityType<? extends BeyonderNPCEntity>) entityType,
                        level, hostile, skin, pathway, sequence
                );
                ((BeyonderNPCEntity) entity).setQuestId("");
                entity.getPersistentData().putBoolean("Initialized", true);

                if (soulData.contains("EntityNBT")) {
                    CompoundTag nbt = soulData.getCompound("EntityNBT").copy();
                    nbt.remove("UUID");
                    nbt.remove("pathway");
                    nbt.remove("sequence");
                    nbt.remove("skin");
                    nbt.remove("hostile");
                    entity.load(nbt);
                }
            } else {
                entity = entityType.create(level);
                if (entity == null) {
                    player.sendSystemMessage(Component.translatable("ability.lotmcraft.internal_underworld.failed")
                            .withStyle(ChatFormatting.RED));
                    return;
                }
                if (soulData.contains("EntityNBT")) {
                    CompoundTag nbt = soulData.getCompound("EntityNBT").copy();
                    nbt.remove("UUID");
                    entity.load(nbt);
                }
            }

            // Spawn in front of player
            Vec3 look = player.getLookAngle();
            Vec3 pos = player.position().add(look.x * 2, 0, look.z * 2);
            entity.moveTo(pos.x, pos.y, pos.z, player.getYRot(), 0);
            entity.setUUID(UUID.randomUUID());

            boolean spawned = level.addFreshEntity(entity);

            if (spawned && entity instanceof LivingEntity livingEntity) {
                SubordinateUtils.turnEntityIntoSubordinate(livingEntity, player, false);
                removeStoredSoul(player, soulData);
                player.sendSystemMessage(Component.translatable("ability.lotmcraft.internal_underworld.summoned",
                        entity.getName().getString()).withStyle(ChatFormatting.DARK_AQUA));
            } else {
                player.sendSystemMessage(Component.translatable("ability.lotmcraft.internal_underworld.failed")
                        .withStyle(ChatFormatting.RED));
            }

        } catch (Exception e) {
            player.sendSystemMessage(Component.translatable("ability.lotmcraft.internal_underworld.failed")
                    .withStyle(ChatFormatting.RED));
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // NBT persistence helpers — stored in player's persistent data
    // -------------------------------------------------------------------------

    private static List<CompoundTag> getStoredSouls(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        List<CompoundTag> souls = new ArrayList<>();
        if (data.contains(STORED_SOULS_TAG)) {
            ListTag list = data.getList(STORED_SOULS_TAG, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                souls.add(list.getCompound(i));
            }
        }
        return souls;
    }

    private static void addStoredSoul(ServerPlayer player, CompoundTag soulData) {
        CompoundTag data = player.getPersistentData();
        ListTag list = data.contains(STORED_SOULS_TAG)
                ? data.getList(STORED_SOULS_TAG, Tag.TAG_COMPOUND)
                : new ListTag();

        list.add(soulData);

        int maxSouls = getMaxSouls(BeyonderData.getSequence(player));
        while (list.size() > maxSouls) {
            list.remove(0);
        }

        data.put(STORED_SOULS_TAG, list);
    }

    private static void removeStoredSoul(ServerPlayer player, CompoundTag soulData) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(STORED_SOULS_TAG)) return;

        ListTag list = data.getList(STORED_SOULS_TAG, Tag.TAG_COMPOUND);
        list.remove(soulData);
        data.put(STORED_SOULS_TAG, list);
    }
}
