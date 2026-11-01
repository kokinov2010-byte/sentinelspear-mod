package com.cuferius.sentinelspear.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

public class KeyBindings {

    public static final String CATEGORY = "key.categories.sentinelspear";

    public static final KeyMapping CHAIN_THROW = new KeyMapping(
            "key.sentinelspear.chain_throw",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_X,
            CATEGORY
    );

    public static final KeyMapping ULTRA_SWEEP = new KeyMapping(
            "key.sentinelspear.ultra_sweep",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_Z,
            CATEGORY
    );
}
