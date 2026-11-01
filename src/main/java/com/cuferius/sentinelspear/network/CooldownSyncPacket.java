package com.cuferius.sentinelspear.network;

import com.cuferius.sentinelspear.SentinelSpearMod;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Sent server -> client every tick (while holding the spear) to keep the cooldown HUD accurate. */
public record CooldownSyncPacket(int chainThrowTicks, int ultraSweepTicks) implements CustomPacketPayload {

    public static final Type<CooldownSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SentinelSpearMod.MOD_ID, "cooldown_sync"));

    public static final StreamCodec<io.netty.buffer.ByteBuf, CooldownSyncPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, CooldownSyncPacket::chainThrowTicks,
                    ByteBufCodecs.VAR_INT, CooldownSyncPacket::ultraSweepTicks,
                    CooldownSyncPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
