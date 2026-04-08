package de.jakob.lotm.network.packets.toClient;


import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.util.ClientBeyonderCache;
import de.jakob.lotm.util.beyonderMap.PathwayHistory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncBeyonderDataPacket(String pathway, int sequence, float spirituality, boolean griefingEnabled, float digestionProgress, PathwayHistory pathwayHistory) implements CustomPacketPayload {
    public static final Type<SyncBeyonderDataPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(LOTMCraft.MOD_ID, "sync_beyonder_data"));

    private static final StreamCodec<FriendlyByteBuf, PathwayHistory> PATHWAY_HISTORY_CODEC =
            StreamCodec.of(
                    (buf, history) -> {
                        for (int i = 0; i < 10; i++) {
                            String p = history.get(i);
                            buf.writeUtf(p != null ? p : "");
                        }
                    },
                    buf -> {
                        String[] pathways = new String[10];
                        for (int i = 0; i < 10; i++) {
                            String p = buf.readUtf();
                            pathways[i] = p.isEmpty() ? null : p;
                        }
                        return new PathwayHistory(pathways);
                    }
            );

    public static final StreamCodec<FriendlyByteBuf, SyncBeyonderDataPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, SyncBeyonderDataPacket::pathway,
                    ByteBufCodecs.VAR_INT, SyncBeyonderDataPacket::sequence,
                    ByteBufCodecs.FLOAT, SyncBeyonderDataPacket::spirituality,
                    ByteBufCodecs.BOOL, SyncBeyonderDataPacket::griefingEnabled,
                    ByteBufCodecs.FLOAT, SyncBeyonderDataPacket::digestionProgress,
                    PATHWAY_HISTORY_CODEC, SyncBeyonderDataPacket::pathwayHistory,
                    SyncBeyonderDataPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncBeyonderDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientBeyonderCache.updateData(
                    context.player().getUUID(),
                    packet.pathway(),
                    packet.sequence(),
                    packet.spirituality(),
                    packet.griefingEnabled(),
                    true,
                    packet.digestionProgress(),
                    packet.pathwayHistory()
            );
        });
    }
}