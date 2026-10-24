package de.jakob.lotm.beyonders.abilities.sun.notarization.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record Candidate(UUID id, String name, int sequence) {
    public static final StreamCodec<ByteBuf, Candidate> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, Candidate::id,
            ByteBufCodecs.STRING_UTF8, Candidate::name,
            ByteBufCodecs.VAR_INT, Candidate::sequence,
            Candidate::new);
    public static final StreamCodec<ByteBuf, java.util.List<Candidate>> LIST_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());
}
