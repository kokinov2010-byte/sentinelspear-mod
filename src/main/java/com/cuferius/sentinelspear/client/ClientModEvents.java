package com.cuferius.sentinelspear.client;

import com.cuferius.sentinelspear.SentinelSpearMod;
import com.cuferius.sentinelspear.item.SentinelChainSpearItem;
import com.cuferius.sentinelspear.network.ChainThrowPacket;
import com.cuferius.sentinelspear.network.UltraSweepPacket;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = SentinelSpearMod.MOD_ID, value = net.neoforged.api.distmarker.Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.CHAIN_THROW);
        event.register(KeyBindings.ULTRA_SWEEP);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        ClientUltraSweepHandler.tick();

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        boolean holdingSpear = mc.player.getMainHandItem().getItem() instanceof SentinelChainSpearItem;

        while (KeyBindings.CHAIN_THROW.consumeClick()) {
            if (holdingSpear) {
                PacketDistributor.sendToServer(new ChainThrowPacket());
            }
        }

        while (KeyBindings.ULTRA_SWEEP.consumeClick()) {
            if (holdingSpear) {
                PacketDistributor.sendToServer(new UltraSweepPacket());
            }
        }
    }
}
