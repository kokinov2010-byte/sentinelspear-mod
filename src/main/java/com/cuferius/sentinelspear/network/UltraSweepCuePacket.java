package com.cuferius.sentinelspear.network;

import com.cuferius.sentinelspear.SentinelSpearMod;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Sent server -> all nearby clients right before the Ultra Sweep lands, so every client
 * can show the title text / sweep cue in sync, and sent to the attacker specifically to
 * force their camera into third person.
 */
public record UltraSweepCuePacket(int entityId, boolean isSelf) implements CustomPacketPayload {

    public static final Type<UltraSweepCuePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SentinelSpearMod.MOD_ID, "ultra_sweep_cue"));

    public static final StreamCodec<io.netty.buffer.ByteBuf, UltraSweepCuePacket> STREAM_CODEC =
            StreamCodec.composite(
                    net.minecraft.network.codec.ByteBufCodecs.VAR_INT, UltraSweepCuePacket::entityId,
                    net.minecraft.network.codec.ByteBufCodecs.BOOL, UltraSweepCuePacket::isSelf,
                    UltraSweepCuePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
