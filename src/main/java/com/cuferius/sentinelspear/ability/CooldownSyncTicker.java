package com.cuferius.sentinelspear.ability;

import com.cuferius.sentinelspear.SentinelSpearMod;
import com.cuferius.sentinelspear.item.SentinelChainSpearItem;
import com.cuferius.sentinelspear.network.CooldownSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = SentinelSpearMod.MOD_ID)
public class CooldownSyncTicker {

    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        tickCounter++;
        if (tickCounter % 5 != 0) return; // sync 4x/second, plenty for a HUD readout

        if (event.getServer() == null) return;
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (player.getMainHandItem().getItem() instanceof SentinelChainSpearItem) {
                int chain = (int) AbilityHandler.getChainThrowRemainingTicks(player);
                int sweep = (int) AbilityHandler.getUltraSweepRemainingTicks(player);
                PacketDistributor.sendToPlayer(player, new CooldownSyncPacket(chain, sweep));
            }
        }
    }
}
