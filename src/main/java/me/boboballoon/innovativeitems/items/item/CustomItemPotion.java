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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * A class that represents a custom item that is a potion
 */
public class CustomItemPotion implements CustomItem {
    private final String name;
    private final Ability ability;
    private final ItemStack itemStack;

    public CustomItemPotion(@NotNull String name, @Nullable Ability ability, @NotNull Material material, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, @Nullable Color rgb, @Nullable Color color, @Nullable List<PotionEffect> effects) {
        //if not potion
        if (!isPotion(material)) {
            LogUtil.log(Level.SEVERE, "Error while loading item " + name + " because material is not an instance of a potion!");

            this.name = null;
            this.ability = null;
            this.itemStack = null;

            return;
        }

        this.name = name;
        this.ability = ability;
        this.itemStack = this.generateItem(material, itemName, lore, enchantments, flags, attributes, customModelData, rgb, color, effects);
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
     * @param rgb             color of the potion via rgb
     * @param color           color of the potion via color name
     * @param effects         all the custom potion effects applied on this item
     * @return the itemstack
     */
    private ItemStack generateItem(@NotNull Material material, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, @Nullable Color rgb, @Nullable Color color, @Nullable List<PotionEffect> effects) {
        ItemStack item = CustomItem.generateItem(this.name, material, itemName, lore, enchantments, flags, attributes, customModelData, false, true);
        PotionMeta meta = (PotionMeta) item.getItemMeta();

        if (rgb != null) {
            meta.setColor(rgb);
        }

        if (color != null && rgb == null) {
            meta.setColor(color);
        }

        if (effects != null) {
            for (PotionEffect effect : effects) {
                meta.addCustomEffect(effect, true);
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Util method to check if a material is a potion
     *
     * @param material the material you want to check
     * @return a boolean that is true if the material provided is a potion
     */
    public static boolean isPotion(Material material) {
        return (material == Material.POTION ||
                material == Material.LINGERING_POTION ||
                material == Material.SPLASH_POTION);
    }
}
