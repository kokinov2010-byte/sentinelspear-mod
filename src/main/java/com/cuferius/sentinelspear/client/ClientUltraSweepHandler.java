package com.cuferius.sentinelspear.client;

import com.cuferius.sentinelspear.network.UltraSweepCuePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ClientUltraSweepHandler {

    private static CameraType previousCameraType = null;
    private static int revertAtClientTick = -1;
    private static int clientTickCounter = 0;

    public static void handleCue(UltraSweepCuePacket payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (payload.isSelf()) {
            // Remember the player's normal view so we can restore it once the spin finishes.
            if (previousCameraType == null) {
                previousCameraType = mc.options.getCameraType();
            }
            mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
            revertAtClientTick = clientTickCounter + 45; // ~2.25s: covers wind-up + spin animation
        }

        // Bold title text in the center of the screen, shown just before the strike lands.
        Component title = Component.literal("cuferius доминирует")
                .withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_RED);

        mc.gui.setTitle(title);
        mc.gui.setTitlesAnimation(2, 25, 5);
    }

    /** Call once per client tick (see ClientModEvents) to handle restoring the player's camera. */
    public static void tick() {
        clientTickCounter++;
        if (revertAtClientTick >= 0 && clientTickCounter >= revertAtClientTick) {
            Minecraft mc = Minecraft.getInstance();
            if (previousCameraType != null) {
                mc.options.setCameraType(previousCameraType);
            }
            previousCameraType = null;
            revertAtClientTick = -1;
        }
    }
}
