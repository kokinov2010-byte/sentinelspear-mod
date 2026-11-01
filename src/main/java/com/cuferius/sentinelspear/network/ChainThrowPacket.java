package com.cuferius.sentinelspear.network;

import com.cuferius.sentinelspear.SentinelSpearMod;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Sent client -> server when the player presses the Chain Throw & Pull key (default X). */
public record ChainThrowPacket() implements CustomPacketPayload {

    public static final Type<ChainThrowPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SentinelSpearMod.MOD_ID, "chain_throw"));

    public static final StreamCodec<io.netty.buffer.ByteBuf, ChainThrowPacket> STREAM_CODEC =
            StreamCodec.unit(new ChainThrowPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
