package com.cuferius.sentinelspear.item;

import com.cuferius.sentinelspear.SentinelSpearMod;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(modid = SentinelSpearMod.MOD_ID)
public class ModCreativeTab {

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.SENTINEL_CHAIN_SPEAR);
        }
    }
}
