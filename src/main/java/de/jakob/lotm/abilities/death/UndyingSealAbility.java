package de.jakob.lotm.abilities.death;

import de.jakob.lotm.abilities.core.Ability;
import de.jakob.lotm.abilities.core.interaction.InteractionHandler;
import de.jakob.lotm.util.BeyonderData;
import de.jakob.lotm.util.data.Location;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UndyingSealAbility extends Ability {

    private static final int DURATION_TICKS = 20 * 60; // 60 seconds

    /** Maps player UUID → server game-time tick at which the seal expires. */
    private static final ConcurrentHashMap<UUID, Long> sealedPlayers = new ConcurrentHashMap<>();

    public UndyingSealAbility(String id) {
        super(id, 120f);
        canBeUsedByNPC = false;
    }

    @Override
    public Map<String, Integer> getRequirements() {
        return new HashMap<>(Map.of("death", 4));
    }

    @Override
    protected float getSpiritualityCost() {
        return 350;
    }

    @Override
    public void onAbilityUse(Level level, LivingEntity entity) {
        if (level.isClientSide()) return;
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!(entity instanceof ServerPlayer player)) return;

        if (InteractionHandler.isInteractionPossibleStrictlyHigher(new Location(entity.position(), serverLevel), "purification", BeyonderData.getSequence(entity), -1)) return;

        long expiresAt = serverLevel.getGameTime() + DURATION_TICKS;
        sealedPlayers.put(player.getUUID(), expiresAt);

        player.sendSystemMessage(Component.translatable("ability.lotmcraft.undying_seal.activated")
                .withStyle(ChatFormatting.DARK_AQUA));
    }

    /**
     * Called by {@link de.jakob.lotm.artifacts.SealedArtifactEffectHandler} each tick
     * to check whether artifact negative effects should be suppressed for this player.
     */
    public static boolean isSealed(UUID playerUUID, long currentGameTime) {
        Long expiresAt = sealedPlayers.get(playerUUID);
        if (expiresAt == null) return false;
        if (currentGameTime >= expiresAt) {
            sealedPlayers.remove(playerUUID);
            return false;
        }
        return true;
    }
}
