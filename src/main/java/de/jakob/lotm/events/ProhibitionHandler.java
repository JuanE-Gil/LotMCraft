package de.jakob.lotm.events;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.core.AbilityUseEvent;
import de.jakob.lotm.abilities.justiciar.BalancingAbility;
import de.jakob.lotm.abilities.justiciar.ProhibitionAbility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = LOTMCraft.MOD_ID)
public class ProhibitionHandler {

    @SubscribeEvent
    public static void onAbilityUse(AbilityUseEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;

        if (isInZone(entity.position(), serverLevel, ProhibitionAbility.ProhibitionType.BEYONDER_ABILITIES)) {
            event.setCanceled(true);
            if (entity instanceof ServerPlayer sp) {
                sp.sendSystemMessage(Component.literal("[Beyonder Abilities] is Prohibited here")
                        .withStyle(ChatFormatting.RED));
            }
            return;
        }

        // Outside World — cancel ability use by players who are near-but-outside the zone
        long now = serverLevel.getGameTime();
        for (ProhibitionAbility.ProhibitionZone zone : ProhibitionAbility.ACTIVE_ZONES) {
            if (zone.type != ProhibitionAbility.ProhibitionType.OUTSIDE_WORLD) continue;
            if (!zone.level.equals(serverLevel)) continue;
            if (zone.expiryTick < now) continue;
            double dist = entity.position().distanceTo(zone.center);
            if (dist > 40.0 && dist <= 50.0) {
                event.setCanceled(true);
                if (entity instanceof ServerPlayer sp) {
                    sp.sendSystemMessage(Component.literal("[Outside World] Abilities from outside are Prohibited here")
                            .withStyle(ChatFormatting.RED));
                }
                break;
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;
        if (!(attacker.level() instanceof ServerLevel serverLevel)) return;

        if (isInZone(attacker.position(), serverLevel, ProhibitionAbility.ProhibitionType.COMBAT)) {
            event.setNewDamage(0);
            if (attacker instanceof ServerPlayer sp) {
                sp.sendSystemMessage(Component.literal("[Combat] is Prohibited here")
                        .withStyle(ChatFormatting.RED));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        // Clean expired zones
        long now = serverLevel.getGameTime();
        ProhibitionAbility.ACTIVE_ZONES.removeIf(z -> z.expiryTick < now);

        Vec3 pos = player.position();

        // Flying prohibition
        if (isInZone(pos, serverLevel, ProhibitionAbility.ProhibitionType.FLYING)) {
            if (player.getAbilities().flying) {
                player.getAbilities().flying = false;
                player.getAbilities().mayfly = false;
                player.onUpdateAbilities();
                player.sendSystemMessage(Component.literal("[Flying] is Prohibited here")
                        .withStyle(ChatFormatting.RED));
            }
        }

        // Players prohibition — push non-owner players out
        for (ProhibitionAbility.ProhibitionZone zone : ProhibitionAbility.ACTIVE_ZONES) {
            if (!zone.type.equals(ProhibitionAbility.ProhibitionType.PLAYERS)) continue;
            if (!zone.level.equals(serverLevel)) continue;
            if (!zone.isActive()) continue;
            if (zone.ownerId.equals(player.getUUID())) continue;

            double dist = pos.distanceTo(zone.center);
            if (dist <= 40.0) {
                Vec3 direction = pos.subtract(zone.center).normalize();
                if (direction.lengthSqr() < 0.001) {
                    direction = new Vec3(1, 0, 0);
                }
                player.setDeltaMovement(direction.scale(1.5));
                player.hurtMarked = true;
                player.sendSystemMessage(Component.literal("[Players] is Prohibited here")
                        .withStyle(ChatFormatting.RED));
            }
        }

        // Outside World — push players away from outside the zone who approach too close
        for (ProhibitionAbility.ProhibitionZone zone : ProhibitionAbility.ACTIVE_ZONES) {
            if (!zone.type.equals(ProhibitionAbility.ProhibitionType.OUTSIDE_WORLD)) continue;
            if (!zone.level.equals(serverLevel)) continue;
            if (!zone.isActive()) continue;
            if (zone.ownerId.equals(player.getUUID())) continue;

            double dist = pos.distanceTo(zone.center);
            if (dist > 40.0 && dist <= 43.0) {
                Vec3 direction = pos.subtract(zone.center).normalize();
                if (direction.lengthSqr() < 0.001) direction = new Vec3(1, 0, 0);
                player.setDeltaMovement(direction.scale(1.5));
                player.hurtMarked = true;
                player.sendSystemMessage(Component.literal("[Outside World] is Prohibited here")
                        .withStyle(ChatFormatting.RED));
            }
        }
    }

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        if (isInZone(player.position(), serverLevel, ProhibitionAbility.ProhibitionType.ITEM_USE)) {
            event.setCanceled(true);
            player.sendSystemMessage(Component.literal("[Item Use] is Prohibited here")
                    .withStyle(ChatFormatting.RED));
        }
    }

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (!(event.getEntity() instanceof LivingEntity le)) return;
        if (!(le.level() instanceof ServerLevel sl)) return;
        Vec3 pos = le.position();
        boolean inZone = BalancingAbility.ACTIVE_ZONES.stream()
                .anyMatch(z -> z.isActive() && z.isInZone(pos, sl));
        if (inZone) event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
    }

    public static boolean isInStandInsZone(Vec3 pos, ServerLevel level) {
        return isInZone(pos, level, ProhibitionAbility.ProhibitionType.STAND_INS);
    }

    public static boolean isInMarionetteZone(Vec3 pos, ServerLevel level) {
        return isInZone(pos, level, ProhibitionAbility.ProhibitionType.MARIONETTE_INTERCHANGE);
    }

    private static boolean isInZone(Vec3 pos, ServerLevel level, ProhibitionAbility.ProhibitionType type) {
        long now = level.getGameTime();
        for (ProhibitionAbility.ProhibitionZone zone : ProhibitionAbility.ACTIVE_ZONES) {
            if (zone.type != type) continue;
            if (!zone.level.equals(level)) continue;
            if (zone.expiryTick < now) continue;
            if (pos.distanceTo(zone.center) <= 40.0) return true;
        }
        return false;
    }
}