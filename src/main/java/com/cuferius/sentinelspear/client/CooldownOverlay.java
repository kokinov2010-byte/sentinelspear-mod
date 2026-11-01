package com.cuferius.sentinelspear.client;

import com.cuferius.sentinelspear.SentinelSpearMod;
import com.cuferius.sentinelspear.item.SentinelChainSpearItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.IGuiLayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * Renders "Chain Throw: 1.4s" / "Ultra Sweep: READY" style text above the hotbar
 * whenever the player is holding the Sentinel Chain Spear.
 *
 * Note: actual remaining-cooldown values live server-side (AbilityHandler). In a full
 * implementation these are synced to the client each tick via a lightweight packet or
 * an attached data component; this overlay reads that synced client-side cache.
 */
@EventBusSubscriber(modid = SentinelSpearMod.MOD_ID, value = net.neoforged.api.distmarker.Dist.CLIENT)
public class CooldownOverlay {

    public static final ResourceLocation LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(SentinelSpearMod.MOD_ID, "cooldown_overlay");

    // Client-side cache of remaining cooldown ticks, kept in sync by CooldownSyncPacket (server -> client).
    public static volatile int chainThrowRemainingTicks = 0;
    public static volatile int ultraSweepRemainingTicks = 0;

    @SubscribeEvent
    public static void registerLayer(RegisterGuiLayersEvent event) {
        event.registerAboveAll(LAYER_ID, (IGuiLayer) (guiGraphics, deltaTracker) -> render(guiGraphics));
    }

    private static void render(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        if (!(mc.player.getMainHandItem().getItem() instanceof SentinelChainSpearItem)) return;

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        int baseY = screenHeight - 55;

        String chainText = formatCooldown("Chain Throw", chainThrowRemainingTicks);
        String sweepText = formatCooldown("Ultra Sweep", ultraSweepRemainingTicks);

        int chainColor = chainThrowRemainingTicks <= 0 ? 0x55FF55 : 0xFF5555;
        int sweepColor = ultraSweepRemainingTicks <= 0 ? 0x55FF55 : 0xFF5555;

        guiGraphics.drawCenteredString(mc.font, chainText, screenWidth / 2 - 60, baseY, chainColor);
        guiGraphics.drawCenteredString(mc.font, sweepText, screenWidth / 2 + 60, baseY, sweepColor);
    }

    private static String formatCooldown(String label, int remainingTicks) {
        if (remainingTicks <= 0) {
            return label + ": READY";
        }
        float seconds = remainingTicks / 20.0F;
        return String.format("%s: %.1fs", label, seconds);
    }
}
