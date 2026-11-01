package com.cuferius.sentinelspear.network;

import com.cuferius.sentinelspear.SentinelSpearMod;
import com.cuferius.sentinelspear.ability.AbilityHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(SentinelSpearMod.MOD_ID).versioned("1.0.0");

        registrar.playToServer(
                ChainThrowPacket.TYPE,
                ChainThrowPacket.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        AbilityHandler.handleChainThrow(context.player()))
        );

        registrar.playToServer(
                UltraSweepPacket.TYPE,
                UltraSweepPacket.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        AbilityHandler.handleUltraSweepRequest(context.player()))
        );

        registrar.playToClient(
                UltraSweepCuePacket.TYPE,
                UltraSweepCuePacket.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        com.cuferius.sentinelspear.client.ClientUltraSweepHandler.handleCue(payload))
        );

        registrar.playToClient(
                CooldownSyncPacket.TYPE,
                CooldownSyncPacket.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    com.cuferius.sentinelspear.client.CooldownOverlay.chainThrowRemainingTicks = payload.chainThrowTicks();
                    com.cuferius.sentinelspear.client.CooldownOverlay.ultraSweepRemainingTicks = payload.ultraSweepTicks();
                })
        );
    }
}
