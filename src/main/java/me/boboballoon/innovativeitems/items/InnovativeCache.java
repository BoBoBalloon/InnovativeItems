package me.boboballoon.innovativeitems.items;

import me.boboballoon.innovativeitems.items.item.Ability;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

/**
 * A class that is responsible for holding all items and abilities in memory during runtime
 */
public final class InnovativeCache {
    private final HashMap<String, Ability> abilities;
    private final HashMap<String, CustomItem> items;

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
        String name = ability.getName();

        if (this.contains(name)) {
            LogUtil.log(Level.WARNING, "Ability with the name of " + name + ", is already registered! Skipping...");
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
        String name = item.getName();

        if (this.contains(name)) {
            LogUtil.log(Level.WARNING, "Item with the name of " + name + ", is already registered! Skipping...");
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
}