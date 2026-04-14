package de.jakob.lotm.abilities.death.passives;

import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.abilities.PassiveAbilityHandler;
import de.jakob.lotm.abilities.PassiveAbilityItem;
import de.jakob.lotm.attachments.DisabledAbilitiesComponent;
import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.damage.ModDamageTypes;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.scheduling.ServerScheduler;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.RandomSource;

@EventBusSubscriber(modid = LOTMCraft.MOD_ID)
public class ReincarnationAbility extends PassiveAbilityItem {

    private static final String NBT_COOLDOWN_TIME = "reincarnation_cooldown_until";
    private static final String SEAL_CAUSE = "reincarnation_debuff";

    /** 5 minutes of invisibility in ticks */
    private static final int CONCEALMENT_TICKS = 20 * 60 * 5;

    /** Cooldown in ms by sequence (index = sequence number) */
    private static long getCooldownMs(int sequence) {
        if (sequence <= 3) return 12L * 60 * 60 * 1000; // 12 hours
        return 24L * 60 * 60 * 1000;                    // 24 hours (seq 4+)
    }

    /** Seal duration in ticks by sequence */
    private static int getSealDurationTicks(int sequence) {
        if (sequence <= 3) return 20 * 60 * 15; // 15 minutes
        return 20 * 60 * 30;                    // 30 minutes (seq 4+)
    }

    public ReincarnationAbility(Properties properties) {
        super(properties);
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("death", 3));
    }

    @Override
    public void tick(Level level, LivingEntity entity) {
        // Timed passive — no per-tick behaviour
    }

    // -------------------------------------------------------------------------
    // Death intercept — HIGHEST so we run before BeyonderEventHandler's regression
    // -------------------------------------------------------------------------

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // Never trigger on losing-control deaths
        if (event.getSource().is(ModDamageTypes.LOOSING_CONTROL)) return;

        if (!((ReincarnationAbility) PassiveAbilityHandler.REINCARNATION.get()).shouldApplyTo(player)) return;
        if (!BeyonderData.isBeyonder(player)) return;

        int sequence = BeyonderData.getSequence(player);
        long cooldownMs = getCooldownMs(sequence);
        int sealTicks = getSealDurationTicks(sequence);

        // Cooldown check (real-world time so it survives restarts)
        long now = System.currentTimeMillis();
        long cooldownUntil = player.getPersistentData().getLong(NBT_COOLDOWN_TIME);
        if (now < cooldownUntil) {
            long remainingMinutes = (cooldownUntil - now) / 60000;
            player.sendSystemMessage(Component.translatable("ability.lotmcraft.reincarnation.on_cooldown",
                    remainingMinutes).withStyle(ChatFormatting.DARK_RED));
            return;
        }

        // Cancel death entirely — inventory, XP, and sequence are all preserved automatically
        event.setCanceled(true);

        // Restore full health
        player.setHealth(player.getMaxHealth());

        // Set cooldown
        player.getPersistentData().putLong(NBT_COOLDOWN_TIME, now + cooldownMs);

        // Teleport to a random safe location within the world border (deferred 1 tick so death handling finishes first)
        if (player.level() instanceof ServerLevel serverLevel) {
            BlockPos safePos = findSafeTeleportPos(serverLevel, player.getRandom(), player.blockPosition());
            if (safePos != null) {
                ServerScheduler.scheduleDelayed(1, () -> {
                    player.teleportTo(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);

                    // Visual burst at destination
                    for (int i = 0; i < 40; i++) {
                        double ox = (player.getRandom().nextDouble() - 0.5) * 2;
                        double oy = player.getRandom().nextDouble() * player.getBbHeight();
                        double oz = (player.getRandom().nextDouble() - 0.5) * 2;
                        serverLevel.sendParticles(ParticleTypes.SOUL,
                                player.getX() + ox, player.getY() + oy, player.getZ() + oz,
                                1, 0, 0.05, 0, 0.05);
                    }
                }, serverLevel);
            }
        }

        // Seal all current-sequence abilities (Reincarnation Debuff)
        DisabledAbilitiesComponent disabledAbilities = player.getData(ModAttachments.DISABLED_ABILITIES_COMPONENT);
        disabledAbilities.disableAbilityUsageForTime(SEAL_CAUSE, sealTicks, player);

        // Also seal each current-sequence ability individually for clarity
        if (BeyonderData.isBeyonder(player)) {
            String pathway = BeyonderData.getPathway(player);
            LOTMCraft.abilityHandler.getByPathwayAndSequence(pathway, sequence).forEach(ability -> {
                if (!ability.canAlwaysBeUsed) {
                    disabledAbilities.disableSpecificAbilityForTime(ability.getId(), SEAL_CAUSE, sealTicks);
                }
            });
        }

        // Grant 5 minutes of concealment
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,
                CONCEALMENT_TICKS, 0, false, false, false));

        int sealMinutes = sealTicks / (20 * 60);
        player.sendSystemMessage(Component.translatable("ability.lotmcraft.reincarnation.triggered")
                .withStyle(ChatFormatting.DARK_AQUA));
        player.sendSystemMessage(Component.translatable("ability.lotmcraft.reincarnation.debuff_applied", sealMinutes)
                .withStyle(ChatFormatting.DARK_RED));
    }

    private static final int MIN_DISTANCE = 500;

    /**
     * Finds a random safe spawn position within the world border,
     * at least MIN_DISTANCE blocks away from the origin.
     * Tries up to 32 candidates; falls back to null if none found.
     */
    private static BlockPos findSafeTeleportPos(ServerLevel level, RandomSource random, BlockPos origin) {
        WorldBorder border = level.getWorldBorder();
        double minX = border.getMinX();
        double maxX = border.getMaxX();
        double minZ = border.getMinZ();
        double maxZ = border.getMaxZ();

        for (int attempt = 0; attempt < 32; attempt++) {
            int x = (int) (minX + random.nextDouble() * (maxX - minX));
            int z = (int) (minZ + random.nextDouble() * (maxZ - minZ));

            // Reject candidates that are too close to the death location
            double dx = x - origin.getX();
            double dz = z - origin.getZ();
            if (dx * dx + dz * dz < (double) MIN_DISTANCE * MIN_DISTANCE) continue;

            // Use the world surface heightmap to find the top solid block
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

            if (y <= level.getMinBuildHeight()) continue;

            BlockPos candidate = new BlockPos(x, y, z);

            // Confirm the two blocks the player occupies are air (not inside a tree, etc.)
            if (level.getBlockState(candidate).isAir()
                    && level.getBlockState(candidate.above()).isAir()
                    && level.getBlockState(candidate.below()).isSolid()) {
                return candidate;
            }
        }

        return null; // Could not find a safe spot — player stays where they are
    }
}
