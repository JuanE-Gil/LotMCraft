package de.jakob.lotm.util.mixin;

import de.jakob.lotm.attachments.ControllingDataComponent;
import de.jakob.lotm.attachments.MirrorWorldTraversalComponent;
import de.jakob.lotm.attachments.ModAttachments;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to stop spectator mod teleportation while in mirror world, or while being controlled by another player
 * Reason: if not done, players can teleport to other players
 * @author ThzAbdou
 */

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {

    @Shadow public ServerPlayer player;

    @Inject(
            method = "handleTeleportToEntityPacket",
            at = @At("HEAD"),
            cancellable = true
    )
    private void stopSpectatorTeleport(ServerboundTeleportToEntityPacket packet, CallbackInfo ci) {
        MirrorWorldTraversalComponent mirrorWorldTraversalComponent = player.getData(ModAttachments.MIRROR_WORLD_COMPONENT);
        ControllingDataComponent controllingDataComponent = player.getData(ModAttachments.CONTROLLING_DATA);
        if(mirrorWorldTraversalComponent.isInMirrorWorld()) {
            ci.cancel();
        } else if (controllingDataComponent.isControlled()) {
            ci.cancel();
        }

    }
}
