package me.boboballoon.innovativeitems.items.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import de.tr7zw.nbtapi.NBTItem;
import me.boboballoon.innovativeitems.items.ability.Ability;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * A class that is the superclass of all custom items
 */
public final class CustomItem {
    private final String identifier;
    private final ImmutableList<Ability> abilities;
    private final ItemStack item;
    private final boolean placeable;
    private final boolean soulbound;
    private final boolean wearable;
    private final int maxDurability;
    private final boolean updateItem;
    private final ImmutableList<Recipe> recipes;

    public CustomItem(@NotNull String identifier, @NotNull ImmutableList<Ability> abilities, @NotNull Material material, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, boolean unbreakable, boolean placeable, boolean soulbound, boolean wearable, int maxDurability, boolean updateItem, @Nullable ImmutableList<Recipe> recipes) {
        this(identifier, abilities, CustomItem.generateItem(identifier, material, itemName, lore, enchantments, flags, attributes, customModelData, unbreakable, material.getMaxDurability() != 0 && maxDurability > 0 ? maxDurability : material.getMaxDurability() != 0 ? material.getMaxDurability() : 0), placeable, soulbound, wearable, maxDurability, updateItem, recipes);
    }

    public CustomItem(@NotNull String identifier, @NotNull ImmutableList<Ability> abilities, @NotNull ItemStack item, boolean placeable, boolean soulbound, boolean wearable, int maxDurability, boolean updateItem, @Nullable ImmutableList<Recipe> recipes) {
        this.identifier = identifier;
        this.abilities = abilities;
        this.item = item;
        this.placeable = placeable;
        this.soulbound = soulbound;
        this.wearable = wearable;
        this.maxDurability = item.getType().getMaxDurability() != 0 && maxDurability > 0 ? maxDurability : item.getType().getMaxDurability() != 0 ? item.getType().getMaxDurability() : 0;
        this.updateItem = updateItem;
        this.recipes = recipes;
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
     * A method used to get the abilities on this item
     *
     * @return the abilities linked to this item
     */
    @NotNull
    public ImmutableList<Ability> getAbilities() {
        return this.abilities;
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
     * A method used to get if this custom item can be placed on the ground
     *
     * @return a boolean that is true if this custom item can be placed on the ground
     */
    public boolean isPlaceable() {
        return this.placeable;
    }

    /**
     * A method used to get if this custom item will be kept on death
     *
     * @return a boolean that is true if this custom item will be kept on death
     */
    public boolean isSoulbound() {
        return this.soulbound;
    }

    /**
     * A method used to get if this custom item can be worn by a player
     *
     * @return a boolean that is true if this custom item can be worn by a player
     */
    public boolean isWearable() {
        return this.wearable;
    }


    /**
     * A method used to get the maximum durability this custom item has
     *
     * @return the maximum durability this custom item has
     */
    public int getMaxDurability() {
        return this.maxDurability;
    }

    /**
     * A method used to get if the garbage collector should update this item or ignore it
     *
     * @return true if the garbage collector should update this item or false if it should ignore it
     */
    public boolean shouldUpdateItem() {
        return this.updateItem;
    }

    /**
     * A method used to get the crafting recipe that can be used to create this custom item or null if none exist
     *
     * @return the crafting recipe that can be used to create this custom item or null if none exist
     */
    @Nullable
    public ImmutableList<Recipe> getRecipes() {
        return this.recipes;
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
     * @param durability      the current durability of the item
     * @return the itemstack
     */
    public static ItemStack generateItem(@NotNull String identifier, @NotNull Material material, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, boolean unbreakable, int durability) {
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
        nbtItem.setInteger("innovativeplugin-customitem-durability", durability);

        return item;
    }
}
