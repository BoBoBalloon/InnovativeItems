package me.boboballoon.innovativeitems.items;

import com.google.common.collect.ImmutableList;
import de.tr7zw.nbtapi.NBTItem;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A class that is responsible for holding all items and abilities in memory during runtime
 */
public final class InnovativeCache {
    private final List<Ability> abilities;
    private final List<CustomItem> items;

    public InnovativeCache() {
        this.abilities = new ArrayList<>();
        this.items = new ArrayList<>();
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

        this.abilities.add(ability);
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

        this.items.add(item);

        if (item.getRecipes() == null) {
            return;
        }

        Runnable task = () -> {
            for (Recipe recipe : item.getRecipes()) {
                try {
                    Bukkit.addRecipe(recipe);
                } catch (IllegalStateException e) {
                    LogUtil.log(LogUtil.Level.INFO, "It seems like there was a duplicate recipe registered for an item... Normally this is not a problem but if a bug occurs this might be the cause...");
                    if (InnovativeItems.getInstance().getConfigManager().getDebugLevel() >= LogUtil.Level.DEV.getDebugLevel()) {
                        e.printStackTrace();
                    }
                }
            }
        };

        if (Bukkit.isPrimaryThread()) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(InnovativeItems.getInstance(), task);
        }
    }

    /**
     * A method used to get an ability already registered in the cache
     *
     * @param name the name (id) of the ability
     * @return the ability (null if not present in cache)
     */
    @Nullable
    public Ability getAbility(@NotNull String name) {
        return this.abilities.stream().filter(ability -> ability.getIdentifier().equals(name)).findAny().orElse(null);
    }

    /**
     * A method used to get an item already registered in the cache
     *
     * @param name the name (id) of the item
     * @return the item (null if not present in cache)
     */
    @Nullable
    public CustomItem getItem(@NotNull String name) {
        return this.items.stream().filter(item -> item.getIdentifier().equals(name)).findAny().orElse(null);
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

        return this.fromNBTItem(new NBTItem(item));
    }

    /**
     * A method used to get a custom item from the cache via an nbt item
     *
     * @param item the item nbt data
     * @return the custom item that the nbt data represents (null if no custom item that matches exists)
     */
    @Nullable
    public CustomItem fromNBTItem(@NotNull NBTItem item) {
        if (!item.hasKey("innovativeplugin-customitem")) {
            return null;
        }

        return this.getItem(item.getString("innovativeplugin-customitem-id"));
    }

    /**
     * A method used to get a custom item from the cache via a collection of itemstacks
     *
     * @param items the collection of itemstacks (the collection cannot be null, individual elements can)
     * @param includeNulls a boolean that represents if itemstacks in the provided collection that are not custom items should be placed in the output collection as null or simply ignored
     * @return a collection of custom items from the collection of itemstacks
     */
    @NotNull
    public Collection<CustomItem> fromItemStacks(@NotNull Collection<ItemStack> items, boolean includeNulls) {
        List<CustomItem> output = new ArrayList<>();

        for (ItemStack stack : items) {
            CustomItem item = this.fromItemStack(stack);

            if (item == null && !includeNulls) {
                continue;
            }

            output.add(item);
        }

        return !output.isEmpty() ? output : Collections.emptyList();
    }

    /**
     * A method used to get a custom item from the cache via a collection of itemstacks
     *
     * @param items the collection of itemstacks (the collection cannot be null, individual elements can)
     * @return a collection of custom items from the collection of itemstacks
     */
    @NotNull
    public Collection<CustomItem> fromItemStacks(@NotNull Collection<ItemStack> items) {
        return this.fromItemStacks(items, false);
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
    public boolean contains(@NotNull String name) {
        return (this.abilities.stream().anyMatch(ability -> ability.getIdentifier().equals(name)) || this.items.stream().anyMatch(item -> item.getIdentifier().equals(name)));
    }

    /**
     * A method that returns all of the registered abilities
     *
     * @return all of the registered abilities
     */
    @NotNull
    public ImmutableList<Ability> getAbilities() {
        return ImmutableList.copyOf(this.abilities);
    }

    /**
     * A method that returns all of the registered items
     *
     * @return all of the registered items
     */
    @NotNull
    public ImmutableList<CustomItem> getItems() {
        return ImmutableList.copyOf(this.items);
    }
}