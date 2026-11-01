package com.cuferius.sentinelspear.item;

import com.cuferius.sentinelspear.SentinelSpearMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(SentinelSpearMod.MOD_ID);

    // Custom tier so the spear isn't locked to vanilla mining-level balance.
    public static final DeferredHolder<Item, SentinelChainSpearItem> SENTINEL_CHAIN_SPEAR =
            ITEMS.register("sentinel_chain_spear", () ->
                    new SentinelChainSpearItem(new Item.Properties()
                            .stacksTo(1)
                            .rarity(net.minecraft.world.item.Rarity.EPIC)
                            .fireResistant()));
}
