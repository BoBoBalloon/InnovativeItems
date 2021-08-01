package me.boboballoon.innovativeitems.items.item;

import com.google.common.collect.Multimap;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * A class that represents a custom item that is leather armor
 */
public class CustomItemLeatherArmor extends CustomItem {
    public CustomItemLeatherArmor(@NotNull String identifier, @Nullable Ability ability, @NotNull Material material, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, boolean unbreakable, @Nullable Color rgb, @Nullable Color color) {
        super(identifier, ability, CustomItemLeatherArmor.generateItem(identifier, material, itemName, lore, enchantments, flags, attributes, customModelData, unbreakable, rgb, color));
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
     * @param unbreakable     if the custom item is unbreakable
     * @param rgb             color of the leather armor via rgb
     * @param color           color of the leather armor via color name
     * @return the itemstack
     */
    private static ItemStack generateItem(@NotNull String identifier, @NotNull Material material, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, boolean unbreakable, @Nullable Color rgb, @Nullable Color color) {
        //if not leather armor
        if (!CustomItemLeatherArmor.isLeatherArmor(material)) {
            LogUtil.log(LogUtil.Level.DEV, "Error while loading item " + identifier + " because material is not an instance of leather armor!");
            throw new IllegalArgumentException("Illegal material provided in CustomItemLeatherArmor constructor");
        }

        ItemStack item = CustomItem.generateItem(identifier, material, itemName, lore, enchantments, flags, attributes, customModelData, unbreakable, true);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

        if (rgb != null) {
            meta.setColor(rgb);
        }

        if (color != null && rgb == null) {
            meta.setColor(color);
        }

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Util method to check if a material is leather armor
     *
     * @param material the material you want to check
     * @return a boolean that is true if the material provided is leather armor
     */
    public static boolean isLeatherArmor(Material material) {
        return (material == Material.LEATHER_HELMET ||
                material == Material.LEATHER_CHESTPLATE ||
                material == Material.LEATHER_LEGGINGS ||
                material == Material.LEATHER_BOOTS);
    }
}
