package com.cuferius.sentinelspear.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.List;

/**
 * The Sentinel Chain Spear: base damage 36, attack speed 1.4.
 * Behaves like a sword for base melee purposes (sweep attack, blocking interactions),
 * but all its real power comes from the two bound abilities (see AbilityHandler).
 */
public class SentinelChainSpearItem extends SwordItem {

    public static final float BASE_ATTACK_DAMAGE = 36.0F;
    public static final float BASE_ATTACK_SPEED = 1.4F;

    // Cooldowns, in ticks (20 ticks = 1 second)
    public static final int CHAIN_THROW_COOLDOWN_TICKS = 40;   // 2 seconds
    public static final int ULTRA_SWEEP_COOLDOWN_TICKS = 80;   // 4 seconds

    public static final int ABILITY_RANGE = 15; // blocks
    public static final float SWEEP_RADIUS = 7.0F; // blocks

    public SentinelChainSpearItem(Item.Properties properties) {
        super(customTier(), properties.attributes(buildAttributes()));
    }

    /** A fake custom tier - durability/enchantability are irrelevant since this item is essentially indestructible. */
    private static Tier customTier() {
        return Tiers.NETHERITE;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Legendary Weapon").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        tooltip.add(Component.literal("\"Forged in Argent chains, bound in demon fire.\"")
                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Chain Throw & Pull [X]").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("  Hook & yank a target up to 15 blocks away").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.literal("Ultra Sweep Attack [Z]").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("  360° spin, 7-block radius devastation").withStyle(ChatFormatting.DARK_GRAY));
    }

    private static ItemAttributeModifiers buildAttributes() {
        // SwordItem normally adds its own damage/speed attributes based on tier + damage bonus.
        // We override explicitly here so the final numbers are exactly 36 damage / 1.4 speed,
        // matching vanilla's convention where "attack damage" attribute = base(1) + bonus.
        ResourceLocation damageId = ResourceLocation.fromNamespaceAndPath("sentinelspear", "weapon.base_damage");
        ResourceLocation speedId = ResourceLocation.fromNamespaceAndPath("sentinelspear", "weapon.attack_speed");

        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(damageId, BASE_ATTACK_DAMAGE - 1.0D, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(speedId, BASE_ATTACK_SPEED - 4.0D, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }
}
