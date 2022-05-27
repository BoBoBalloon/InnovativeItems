package me.boboballoon.innovativeitems.items.item;

import com.google.common.collect.Multimap;
import me.boboballoon.innovativeitems.items.ability.Ability;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * A class that represents a custom item that is a shield that can have banner pattens
 */
public class CustomItemShield extends CustomItemBanner {
    public CustomItemShield(@NotNull String identifier, @Nullable Ability ability, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, boolean soulbound, boolean wearable, int maxDurability, @Nullable List<Pattern> patterns, @Nullable DyeColor baseColor) {
        super(identifier, ability, CustomItemShield.generateItem(identifier, itemName, lore, enchantments, flags, attributes, customModelData, maxDurability, patterns, baseColor), false, soulbound, wearable, maxDurability);
    }

    /**
     * A method used to generate an itemstack based on this items internal values
     *
     * @param identifier      the internal name of the custom item
     * @param itemName        the display name of the itemstack
     * @param lore            the lore of the itemstack
     * @param enchantments    the enchantments on the itemstack
     * @param flags           the item flags on the itemstack
     * @param attributes      all attributes for this item
     * @param customModelData the custom model data on the itemstack
     * @param durability      the max durability of the item
     * @param patterns        the patterns to be applied on the banner itemstack
     * @param baseColor       the base color of the shield
     * @return the itemstack
     */
    private static ItemStack generateItem(@NotNull String identifier, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, int durability, @Nullable List<Pattern> patterns, @Nullable DyeColor baseColor) {
        ItemStack item = CustomItem.generateItem(identifier, Material.SHIELD, itemName, lore, enchantments, flags, attributes, customModelData, false, durability);
        BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
        Banner banner = (Banner) meta.getBlockState();

        if (baseColor != null) {
            banner.setBaseColor(baseColor);
        }

        if (patterns != null) {
            banner.setPatterns(patterns);
        }

        banner.update();

        meta.setBlockState(banner);
        item.setItemMeta(meta);
        return item;
    }
}
