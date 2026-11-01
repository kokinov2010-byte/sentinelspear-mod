package com.cuferius.sentinelspear.client;

import com.cuferius.sentinelspear.SentinelSpearMod;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = SentinelSpearMod.MOD_ID, value = net.neoforged.api.distmarker.Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skin);
            if (renderer != null) {
                renderer.addLayer(new ChainArmLayer(renderer));
            }
        }
    }
}
