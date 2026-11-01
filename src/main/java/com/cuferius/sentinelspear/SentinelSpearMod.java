package com.cuferius.sentinelspear;

import com.cuferius.sentinelspear.item.ModItems;
import com.cuferius.sentinelspear.network.NetworkHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Sentinel Chain Spear - a legendary DOOM: The Dark Ages inspired weapon mod.
 */
@Mod(SentinelSpearMod.MOD_ID)
public class SentinelSpearMod {

    public static final String MOD_ID = "sentinelspear";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public SentinelSpearMod(IEventBus modEventBus) {
        ModItems.ITEMS.register(modEventBus);
        modEventBus.addListener(NetworkHandler::register);

        // Register the creative tab contents / common setup if needed.
        modEventBus.addListener((FMLCommonSetupEvent event) ->
                LOGGER.info("Sentinel Chain Spear mod initialized."));
    }
}
