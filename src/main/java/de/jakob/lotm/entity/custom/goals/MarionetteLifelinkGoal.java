package de.jakob.lotm.entity.custom.goals;

import de.jakob.lotm.attachments.ModAttachments;
import de.jakob.lotm.util.helper.marionettes.MarionetteComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.UUID;

public class MarionetteLifelinkGoal extends Goal {
    private final Mob marionette;
    private int separationTimer = 0;
    private int checkInterval = 0;

    public MarionetteLifelinkGoal(Mob marionette) {
        this.marionette = marionette;
        // This goal doesn't interfere with other behaviors
        this.setFlags(EnumSet.noneOf(Goal.Flag.class));
    }

    @Override
    public boolean canUse() {
        MarionetteComponent component = marionette.getData(ModAttachments.MARIONETTE_COMPONENT.get());
        return component.isMarionette();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void tick() {
        // Only check every 20 ticks (1 second) to avoid performance issues
        checkInterval++;
        if (checkInterval < 20) return;
        checkInterval = 0;

        MarionetteComponent component = marionette.getData(ModAttachments.MARIONETTE_COMPONENT.get());
        if (!component.isMarionette()) return;

        String controllerUUID = component.getControllerUUID();
        if (controllerUUID.isEmpty()) {
            killMarionette();
        }
    }

    private Player findPlayerAcrossAllLevels(String uuidString) {
        try {
            UUID uuid = UUID.fromString(uuidString);
            
            // Check all levels on the server
            if (marionette.getServer() != null) {
                for (ServerLevel level : marionette.getServer().getAllLevels()) {
                    Player player = level.getPlayerByUUID(uuid);
                    if (player != null) {
                        return player;
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            // Invalid UUID
        }
        
        return null;
    }

    private void killMarionette() {
        // Optional: Add death effects
        marionette.level().broadcastEntityEvent(marionette, (byte) 3); // Death particles
        
        // Kill the marionette
        marionette.hurt(marionette.damageSources().generic(), Float.MAX_VALUE);
    }
}