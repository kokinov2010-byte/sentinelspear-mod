package com.cuferius.sentinelspear.network;

import com.cuferius.sentinelspear.SentinelSpearMod;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Sent client -> server when the player presses the Ultra Sweep Attack key (default Z). */
public record UltraSweepPacket() implements CustomPacketPayload {

    public static final Type<UltraSweepPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SentinelSpearMod.MOD_ID, "ultra_sweep"));

    public static final StreamCodec<io.netty.buffer.ByteBuf, UltraSweepPacket> STREAM_CODEC =
            StreamCodec.unit(new UltraSweepPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
