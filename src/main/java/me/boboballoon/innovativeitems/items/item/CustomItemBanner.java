package me.boboballoon.innovativeitems.items.item;

import com.google.common.collect.Multimap;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * A class that represents a custom item that is a banner
 */
public class CustomItemBanner extends CustomItem {
    public CustomItemBanner(@NotNull String identifier, @Nullable Ability ability, @NotNull Material material, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, boolean placeable, boolean soulbound, boolean wearable, @Nullable List<Pattern> patterns) {
        this(identifier, ability, CustomItemBanner.generateItem(identifier, material, itemName, lore, enchantments, flags, attributes, customModelData, placeable, soulbound, wearable, patterns));
    }

    protected CustomItemBanner(@NotNull String identifier, @Nullable Ability ability, @NotNull ItemStack item) {
        super(identifier, ability, item);
    }

    /**
     * A method used to generate an itemstack based on this items internal values
     *
     * @param identifier      the internal name of the custom item
     * @param material        the material of the itemstack
     * @param itemName        the display name of the itemstack
     * @param lore            the lore of the itemstack
     * @param enchantments    the enchantments on the itemstack
     * @param flags           the item flags on the itemstack
     * @param attributes      all attributes for this item
     * @param customModelData the custom model data on the itemstack
     * @param placeable       if the material is a block and can be placed
     * @param soulbound       if the item is saved on death
     * @param wearable        if the item can be worn by users
     * @param patterns        the patterns to be applied on the banner itemstack
     * @return the itemstack
     */
    private static ItemStack generateItem(@NotNull String identifier, @NotNull Material material, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, boolean placeable, boolean soulbound, boolean wearable, @Nullable List<Pattern> patterns) {
        //if not banner
        if (!CustomItemBanner.isBanner(material)) {
            LogUtil.log(LogUtil.Level.DEV, "Error while loading item " + identifier + " because material is not an instance of a banner!");
            throw new IllegalArgumentException("Illegal material provided in CustomItemBanner constructor");
        }

        ItemStack item = CustomItem.generateItem(identifier, material, itemName, lore, enchantments, flags, attributes, customModelData, false, placeable, soulbound, wearable);
        BannerMeta meta = (BannerMeta) item.getItemMeta();

        if (patterns != null) {
            meta.setPatterns(patterns);
        }

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Util method to check if a material is a banner
     *
     * @param material the material you want to check
     * @return a boolean that is true if the material provided is a banner
     */
    public static boolean isBanner(Material material) {
        return (material == Material.BLACK_BANNER ||
                material == Material.BLUE_BANNER ||
                material == Material.BROWN_BANNER ||
                material == Material.CYAN_BANNER ||
                material == Material.GRAY_BANNER ||
                material == Material.GREEN_BANNER ||
                material == Material.LIGHT_BLUE_BANNER ||
                material == Material.LIGHT_GRAY_BANNER ||
                material == Material.LIME_BANNER ||
                material == Material.PINK_BANNER ||
                material == Material.MAGENTA_BANNER ||
                material == Material.WHITE_BANNER ||
                material == Material.ORANGE_BANNER ||
                material == Material.RED_BANNER ||
                material == Material.YELLOW_BANNER ||
                material == Material.PURPLE_BANNER);
    }
}
