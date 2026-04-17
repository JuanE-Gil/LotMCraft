package de.jakob.lotm.entity.custom.uniqueness;

import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.attachments.UniquenessComponent;
import de.jakob.lotm.damage.ModDamageTypes;
import de.jakob.lotm.entity.ModEntities;
import de.jakob.lotm.util.BeyonderData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A world entity that represents a pathway's uniqueness.
 * Only one entity per pathway can exist in the world at once.
 * It renders like a floating item with the pathway's uniqueness texture.
 * Sequence 1 of the matching pathway can pick it up.
 * Others who touch it are harmed or killed depending on their sequence.
 */
public class UniquenessEntity extends Entity {

    private static final EntityDataAccessor<String> PATHWAY =
            SynchedEntityData.defineId(UniquenessEntity.class, EntityDataSerializers.STRING);

    /** Tracks all active uniqueness entities by pathway (server-side). */
    public static final Map<String, Integer> ACTIVE_ENTITIES = new HashMap<>();

    private int ticksExisted = 0;

    public UniquenessEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public UniquenessEntity(Level level, Vec3 position, String pathway) {
        this(ModEntities.UNIQUENESS_ENTITY.get(), level);
        this.setPos(position.x, position.y, position.z);
        this.setPathway(pathway);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(PATHWAY, "");
    }

    public String getPathway() {
        return entityData.get(PATHWAY);
    }

    public void setPathway(String pathway) {
        entityData.set(PATHWAY, pathway);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            ticksExisted++;
            return;
        }

        ticksExisted++;

        String pathway = getPathway();
        if (pathway.isEmpty()) return;

        // Check nearby entities for hitbox interactions
        AABB hitbox = this.getBoundingBox().inflate(0.5);
        List<LivingEntity> nearby = level().getEntitiesOfClass(LivingEntity.class, hitbox,
                e -> e != this && !(e instanceof UniquenessEntity));

        for (LivingEntity entity : nearby) {
            handleEntityContact(entity, pathway);
        }
    }

    private void handleEntityContact(LivingEntity entity, String pathway) {
        if (!(level() instanceof ServerLevel serverLevel)) return;

        int seq = BeyonderData.getSequence(entity);
        String entityPathway = BeyonderData.getPathway(entity);

        // Sequence 1 of the matching pathway picks up the uniqueness
        if (seq == 1 && entityPathway.equalsIgnoreCase(pathway) && entity instanceof Player player) {
            pickUp(player, pathway, serverLevel);
            return;
        }

        // Others are harmed
        if (seq <= 2 && BeyonderData.isBeyonder(entity)) {
            // Angels (seq <= 2) take massive damage
            entity.hurt(ModDamageTypes.source(level(), ModDamageTypes.BEYONDER_GENERIC), 50.0f);
        } else if (BeyonderData.isBeyonder(entity)) {
            // Seq > 2 die
            entity.hurt(ModDamageTypes.source(level(), ModDamageTypes.BEYONDER_GENERIC), Float.MAX_VALUE);
        }
    }

    private void pickUp(Player player, String pathway, ServerLevel serverLevel) {
        UniquenessComponent component = player.getData(ModAttachments.UNIQUENESS_COMPONENT);
        component.setHasUniqueness(true);
        component.setUniquenessPathway(pathway);

        int color = BeyonderData.pathwayInfos.containsKey(pathway)
                ? BeyonderData.pathwayInfos.get(pathway).color()
                : 0xFFFFFF;

        serverLevel.players().forEach(p ->
                p.displayClientMessage(
                        Component.literal(player.getName().getString())
                                .append(Component.translatable("lotm.uniqueness.picked_up"))
                                .withColor(color),
                        false
                )
        );

        player.playSound(SoundEvents.ITEM_PICKUP);

        // Sync uniqueness component to client
        if (player instanceof ServerPlayer serverPlayer) {
            de.jakob.lotm.network.PacketHandler.syncUniquenessToPlayer(serverPlayer);
        }

        // Remove from active map and discard entity
        ACTIVE_ENTITIES.remove(pathway);
        this.discard();
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (!level().isClientSide) {
            String pathway = getPathway();
            if (!pathway.isEmpty()) {
                ACTIVE_ENTITIES.remove(pathway);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setPathway(tag.getString("Pathway"));
        if (!level().isClientSide && !getPathway().isEmpty()) {
            ACTIVE_ENTITIES.put(getPathway(), this.getId());
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("Pathway", getPathway());
    }

    /**
     * Spawns a UniquenessEntity in the world at the given position for the given pathway.
     * Only spawns if no entity for this pathway already exists.
     */
    public static boolean trySpawn(ServerLevel level, Vec3 position, String pathway) {
        if (ACTIVE_ENTITIES.containsKey(pathway)) {
            // Check that entity is still valid
            Entity existing = level.getEntity(ACTIVE_ENTITIES.get(pathway));
            if (existing != null && !existing.isRemoved()) {
                return false;
            }
            ACTIVE_ENTITIES.remove(pathway);
        }

        UniquenessEntity entity = new UniquenessEntity(level, position, pathway);
        level.addFreshEntity(entity);
        ACTIVE_ENTITIES.put(pathway, entity.getId());

        // Broadcast to online angels (seq <= 2) of this pathway
        int color = BeyonderData.pathwayInfos.containsKey(pathway)
                ? BeyonderData.pathwayInfos.get(pathway).color()
                : 0xFFFFFF;

        for (ServerPlayer player : level.players()) {
            String pPathway = BeyonderData.getPathway(player);
            int pSeq = BeyonderData.getSequence(player);
            if (pPathway.equalsIgnoreCase(pathway) && pSeq <= 2) {
                player.displayClientMessage(
                        Component.translatable("lotm.uniqueness.spawned").withColor(color),
                        false
                );
                player.playSound(SoundEvents.WITHER_SPAWN, 0.5f, 1.5f);
            }
        }

        return true;
    }

    /**
     * Returns true if an active uniqueness entity for this pathway exists in the given level.
     */
    public static boolean existsInWorld(ServerLevel level, String pathway) {
        if (!ACTIVE_ENTITIES.containsKey(pathway)) return false;
        Entity entity = level.getEntity(ACTIVE_ENTITIES.get(pathway));
        return entity != null && !entity.isRemoved();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distSq) {
        return distSq < 4096;
    }

    public int getTicksExisted() {
        return ticksExisted;
    }
}
