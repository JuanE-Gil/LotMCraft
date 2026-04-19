package de.jakob.lotm.abilities.justiciar;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.core.AbilityUsedEvent;
import de.jakob.lotm.abilities.core.SelectableAbility;
import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.rendering.effectRendering.EffectManager;
import de.jakob.lotm.effect.ModEffects;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@EventBusSubscriber(modid = LOTMCraft.MOD_ID)
public class OrderProxyAbility extends SelectableAbility {

    // ── Zone Storage ──────────────────────────────────────────────────────────
    public static final List<PermanentProhibitionZone> PERMANENT_PROHIBITION_ZONES = new CopyOnWriteArrayList<>();
    public static final List<PermanentLawZone> PERMANENT_LAW_ZONES = new CopyOnWriteArrayList<>();

    // ── Order Sacrifice Immunity ──────────────────────────────────────────────
    public static final Set<UUID> IMMUNE_ENTITIES = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /** Entities marked here (died in a Resurrecting prohibition zone) cannot be revived. */
    public static final Set<UUID> NO_REVIVAL_ENTITIES = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static final double PROHIBITION_RADIUS = 50.0;
    public static final double LAW_RADIUS = 15.0;
    private static final int MAX_PROHIBITION_ZONES = 3;
    private static final int MAX_LAW_ZONES = 2;

    public OrderProxyAbility(String id) {
        super(id, 20f, "order_proxy");
        interactionRadius = 50;
        hasOptimalDistance = false;
        postsUsedAbilityEventManually = true;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("justiciar", 1));
    }

    @Override
    protected float getSpiritualityCost() {
        return 800;
    }

    @Override
    protected String[] getAbilityNames() {
        return new String[]{
                "ability.lotmcraft.order_proxy.order_sacrifice",
                "ability.lotmcraft.order_proxy.prohibition_resurrecting",
                "ability.lotmcraft.order_proxy.prohibition_dying",
                "ability.lotmcraft.order_proxy.prohibition_demigods",
                "ability.lotmcraft.order_proxy.law_combat",
                "ability.lotmcraft.order_proxy.law_losing_control"
        };
    }

    @Override
    protected void castSelectedAbility(Level level, LivingEntity entity, int abilityIndex) {
        if (level.isClientSide) return;
        ServerLevel serverLevel = (ServerLevel) level;

        switch (abilityIndex) {
            case 0 -> orderSacrifice(serverLevel, entity);
            case 1 -> orderProhibition(serverLevel, entity, OrderProhibitionType.RESURRECTING);
            case 2 -> orderProhibition(serverLevel, entity, OrderProhibitionType.DYING);
            case 3 -> orderProhibition(serverLevel, entity, OrderProhibitionType.DEMIGODS);
            case 4 -> orderLaw(serverLevel, entity, LawType.COMBAT);
            case 5 -> orderLaw(serverLevel, entity, LawType.LOSING_CONTROL);
        }

        NeoForge.EVENT_BUS.post(new AbilityUsedEvent(serverLevel, entity.position(), entity, this, interactionFlags, interactionRadius, 20 * 2));
    }

    // ── Order Sacrifice ───────────────────────────────────────────────────────

    private void orderSacrifice(ServerLevel serverLevel, LivingEntity entity) {
        UUID uuid = entity.getUUID();

        // Grant full power for 15 seconds
        IMMUNE_ENTITIES.add(uuid);
        BeyonderData.addModifierWithTimeLimit(entity, "order_sacrifice_boost", 3.0, 15000L);

        broadcastToNearby(serverLevel, entity,
                "[Order Sacrifice] " + entity.getDisplayName().getString() + " has reached the level of a Half Great Old One!",
                ChatFormatting.DARK_RED);

        if (entity instanceof ServerPlayer sp) {
            sp.sendSystemMessage(Component.literal(
                    "[Order Sacrifice] 15 seconds of absolute power. The price will be paid.")
                    .withStyle(ChatFormatting.GOLD));
        }

        // After 15 seconds, the price is paid
        ServerScheduler.scheduleDelayed(300, () -> {
            IMMUNE_ENTITIES.remove(uuid);

            LivingEntity target = (LivingEntity) serverLevel.getEntity(uuid);
            if (target == null || !target.isAlive()) return;

            target.kill();
        });
    }

    // ── Order Prohibition ─────────────────────────────────────────────────────

    private void orderProhibition(ServerLevel serverLevel, LivingEntity entity, OrderProhibitionType type) {
        Vec3 pos = entity.position();

        // Re-cast inside existing zone of same type → remove it
        PermanentProhibitionZone existing = PERMANENT_PROHIBITION_ZONES.stream()
                .filter(z -> z.type() == type && z.isInZone(pos, serverLevel))
                .findFirst().orElse(null);
        if (existing != null) {
            PERMANENT_PROHIBITION_ZONES.remove(existing);
            if (entity instanceof ServerPlayer sp) {
                sp.sendSystemMessage(Component.literal(
                        "[Order Prohibition] [" + type.displayName + "] prohibition lifted.")
                        .withStyle(ChatFormatting.YELLOW));
            }
            return;
        }

        if (PERMANENT_PROHIBITION_ZONES.size() >= MAX_PROHIBITION_ZONES) {
            if (entity instanceof ServerPlayer sp) {
                sp.sendSystemMessage(Component.literal(
                        "[Order Prohibition] Cannot go above limit (3 permanent prohibitions).")
                        .withStyle(ChatFormatting.RED));
            }
            return;
        }

        PermanentProhibitionZone zone = new PermanentProhibitionZone(entity.getUUID(), type, entity.position(), serverLevel);
        PERMANENT_PROHIBITION_ZONES.add(zone);

        final double px = entity.getX(), py = entity.getY(), pz = entity.getZ();
        EffectManager.playEffect(EffectManager.Effect.PROHIBITION, px, py, pz, serverLevel);

        broadcastToNearby(serverLevel, entity,
                "[Order Prohibition] [" + type.displayName + "] is Permanently Prohibited here.",
                ChatFormatting.GOLD);
    }

    // ── Order Law ─────────────────────────────────────────────────────────────

    private void orderLaw(ServerLevel serverLevel, LivingEntity entity, LawType type) {
        Vec3 pos = entity.position();

        // Re-cast inside existing zone of same type → remove it
        PermanentLawZone existing = PERMANENT_LAW_ZONES.stream()
                .filter(z -> z.type() == type && z.isInZone(pos, serverLevel))
                .findFirst().orElse(null);
        if (existing != null) {
            PERMANENT_LAW_ZONES.remove(existing);
            if (entity instanceof ServerPlayer sp) {
                sp.sendSystemMessage(Component.literal(
                        "[Order Law] [" + type.displayName + "] law lifted.")
                        .withStyle(ChatFormatting.YELLOW));
            }
            return;
        }

        if (PERMANENT_LAW_ZONES.size() >= MAX_LAW_ZONES) {
            if (entity instanceof ServerPlayer sp) {
                sp.sendSystemMessage(Component.literal(
                        "[Order Law] Cannot go above limit (2 permanent laws).")
                        .withStyle(ChatFormatting.RED));
            }
            return;
        }

        PermanentLawZone zone = new PermanentLawZone(entity.getUUID(), type, entity.position(), serverLevel);
        PERMANENT_LAW_ZONES.add(zone);

        broadcastToNearby(serverLevel, entity,
                "[Order Law] [" + type.displayName + "] is now Law here.",
                ChatFormatting.GOLD);
    }

    // ── Damage Immunity Event ─────────────────────────────────────────────────

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (IMMUNE_ENTITIES.contains(event.getEntity().getUUID())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (!IMMUNE_ENTITIES.contains(event.getEntity().getUUID())) return;
        if (event.getEffectInstance().getEffect().is(ModEffects.LOOSING_CONTROL)) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    private static void broadcastToNearby(ServerLevel level, LivingEntity source, String message, ChatFormatting color) {
        Component msg = Component.literal(message).withStyle(color);
        level.getServer().getPlayerList().getPlayers().forEach(p -> {
            if (p.level().equals(level) && p.distanceTo(source) <= PROHIBITION_RADIUS) {
                p.sendSystemMessage(msg);
            }
        });
    }

    // ── Inner Types ───────────────────────────────────────────────────────────

    public record PermanentProhibitionZone(UUID ownerId, OrderProhibitionType type, Vec3 center, ServerLevel level) {
        public boolean isInZone(Vec3 pos, ServerLevel lvl) {
            return lvl.equals(level) && pos.distanceTo(center) <= PROHIBITION_RADIUS;
        }
    }

    public record PermanentLawZone(UUID ownerId, LawType type, Vec3 center, ServerLevel level) {
        public boolean isInZone(Vec3 pos, ServerLevel lvl) {
            return lvl.equals(level) && pos.distanceTo(center) <= LAW_RADIUS;
        }
    }

    public enum OrderProhibitionType {
        RESURRECTING("Resurrecting"),
        DYING("Dying"),
        DEMIGODS("Demigods");

        public final String displayName;

        OrderProhibitionType(String name) {
            this.displayName = name;
        }
    }

    public enum LawType {
        COMBAT("Combat"),
        LOSING_CONTROL("Losing Control");

        public final String displayName;

        LawType(String name) {
            this.displayName = name;
        }
    }
}
