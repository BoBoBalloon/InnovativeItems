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
import java.util.logging.Level;

/**
 * A class that represents a custom item that is a banner
 */
public class CustomItemBanner implements CustomItem {
    private final String name;
    private final Ability ability;
    private final ItemStack itemStack;

    public CustomItemBanner(@NotNull String name, @Nullable Ability ability, @NotNull Material material, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, boolean unbreakable, boolean placeable, @Nullable List<Pattern> patterns) {
        //if not banner
        if (!isBanner(material)) {
            LogUtil.log(Level.SEVERE, "Error while loading item " + name + " because material is not an instance of a banner!");

            this.name = null;
            this.ability = null;
            this.itemStack = null;

            return;
        }

        this.name = name;
        this.ability = ability;
        this.itemStack = this.generateItem(material, itemName, lore, enchantments, flags, attributes, customModelData, unbreakable, placeable, patterns);
    }

    /**
     * A method used to get the internal name of the custom item
     *
     * @return the internal name of the custom item
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * A method used to get the ability associated with this custom item (can be null if nothing is present)
     *
     * @return the ability associated with this custom item (can be null if nothing is present)
     */
    @Nullable
    @Override
    public Ability getAbility() {
        return this.ability;
    }

    /**
     * A method used to get the itemstack that represents this custom item
     *
     * @return an itemstack that represents this custom item
     */
    @Override
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    /**
     * A method used to generate an itemstack based on this items internal values
     *
     * @param material        the material of the itemstack
     * @param itemName        the display name of the itemstack
     * @param lore            the lore of the itemstack
     * @param enchantments    the enchantments on the itemstack
     * @param flags           the item flags on the itemstack
     * @param attributes      all attributes for this item
     * @param customModelData the custom model data on the itemstack
     * @param unbreakable     if the custom item is unbreakable
     * @param placeable       if the material is a block and can be placed
     * @param patterns        the patterns to be applied on the banner itemstack
     * @return the itemstack
     */
    private ItemStack generateItem(@NotNull Material material, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, boolean unbreakable, boolean placeable, @Nullable List<Pattern> patterns) {
        ItemStack item = CustomItem.generateItem(this.name, material, itemName, lore, enchantments, flags, attributes, customModelData, unbreakable, placeable);
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
