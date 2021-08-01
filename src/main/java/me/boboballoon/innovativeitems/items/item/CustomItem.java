package me.boboballoon.innovativeitems.items.item;

import com.google.common.collect.Multimap;
import de.tr7zw.nbtapi.NBTItem;
import me.boboballoon.innovativeitems.items.ability.Ability;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * A class that is the superclass of all custom items
 */
public abstract class CustomItem {
    private final String identifier;
    private final Ability ability;
    private final ItemStack item;

    public CustomItem(String identifier, Ability ability, ItemStack item) {
        this.identifier = identifier;
        this.ability = ability;
        this.item = item;
    }

    /**
     * A method used to get the internal name of the custom item
     *
     * @return the internal name of the custom item
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * A method used to get the ability associated with this custom item (can be null if nothing is present)
     *
     * @return the ability associated with this custom item (can be null if nothing is present)
     */
    @Nullable
    public Ability getAbility() {
        return this.ability;
    }

    /**
     * A method used to get the itemstack that represents this custom item
     *
     * @return an itemstack that represents this custom item
     */
    public ItemStack getItemStack() {
        return this.item;
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
     * @return the itemstack
     */
    public static ItemStack generateItem(@NotNull String identifier, @NotNull Material material, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, boolean unbreakable, boolean placeable) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (itemName != null) {
            meta.setDisplayName(itemName);
        } else {
            meta.setDisplayName(identifier);
        }

        if (lore != null) {
            meta.setLore(lore);
        }

        if (enchantments != null) {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }

        if (flags != null) {
            for (ItemFlag flag : flags) {
                meta.addItemFlags(flag);
            }
        }

        if (attributes != null) {
            meta.setAttributeModifiers(attributes);
        }

        if (customModelData != null) {
            meta.setCustomModelData(customModelData);
        }

        if (unbreakable) {
            meta.setUnbreakable(true);
        }

        item.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(item, true);
        nbtItem.setBoolean("innovativeplugin-customitem", true);
        nbtItem.setString("innovativeplugin-customitem-id", identifier);
        nbtItem.setBoolean("innovativeplugin-customitem-placeable", placeable);

        return item;
    }
}
