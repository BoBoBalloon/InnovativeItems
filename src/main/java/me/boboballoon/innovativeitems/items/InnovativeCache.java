package me.boboballoon.innovativeitems.items;

import de.tr7zw.nbtapi.NBTItem;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A class that is responsible for holding all items and abilities in memory during runtime
 */
public final class InnovativeCache {
    private final Map<String, Ability> abilities;
    private final Map<String, CustomItem> items;

    public InnovativeCache() {
        this.abilities = new HashMap<>();
        this.items = new HashMap<>();
    }

    /**
     * A method used to register a new ability in the cache
     *
     * @param ability the ability
     */
    public void registerAbility(@NotNull Ability ability) {
        String name = ability.getIdentifier();

        if (this.contains(name)) {
            LogUtil.log(LogUtil.Level.WARNING, "Ability with the name of " + name + ", is already registered! Skipping...");
            return;
        }

        this.abilities.put(name, ability);
    }

    /**
     * A method used to register a new item in the cache
     *
     * @param item the item
     */
    public void registerItem(@NotNull CustomItem item) {
        String name = item.getIdentifier();

        if (this.contains(name)) {
            LogUtil.log(LogUtil.Level.WARNING, "Item with the name of " + name + ", is already registered! Skipping...");
            return;
        }

        this.items.put(name, item);
    }

    /**
     * A method used to get an ability already registered in the cache
     *
     * @param name the name (id) of the ability
     * @return the ability (null if not present in cache)
     */
    @Nullable
    public Ability getAbility(String name) {
        return this.abilities.get(name);
    }

    /**
     * A method used to get an item already registered in the cache
     *
     * @param name the name (id) of the item
     * @return the item (null if not present in cache)
     */
    @Nullable
    public CustomItem getItem(String name) {
        return this.items.get(name);
    }

    /**
     * A method used to get a custom item from the cache via an itemstack
     *
     * @param item the item to get a custom item from
     * @return the custom item that the itemstack represents (null if no custom item that matches exists)
     */
    @Nullable
    public CustomItem fromItemStack(@Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }

        NBTItem nbtItem = new NBTItem(item);

        if (!nbtItem.hasKey("innovativeplugin-customitem")) {
            return null;
        }

        return this.getItem(nbtItem.getString("innovativeplugin-customitem-id"));
    }

    /**
     * A method used to wipe all data currently in the cache
     */
    public void clearCache() {
        this.abilities.clear();
        this.items.clear();
    }

    /**
     * A method used to check whether the cache contains an element with the provided name
     *
     * @param name name the name (id) of the item
     * @return a boolean that is true when said name is present
     */
    public boolean contains(String name) {
        return (this.abilities.containsKey(name) || this.items.containsKey(name));
    }

    /**
     * A method that returns a set of all ids of all registered abilities
     *
     * @return a set of all ids of all registered abilities
     */
    public Set<String> getAbilityIdentifiers() {
        return this.abilities.keySet();
    }

    /**
     * A method that returns a set of all ids of all registered items
     *
     * @return a set of all ids of all registered items
     */
    public Set<String> getItemIdentifiers() {
        return this.items.keySet();
    }

    /**
     * A method used to return the amount of items registered in the item cache
     *
     * @return the amount of items registered in the item cache
     */
    public int getItemAmount() {
        return this.items.size();
    }

    /**
     * A method used to return the amount of abilities registered in the item cache
     *
     * @return the amount of abilities registered in the item cache
     */
    public int getAbilitiesAmount() {
        return this.abilities.size();
    }
}