package me.boboballoon.innovativeitems.items.ability.trigger;


import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;

@FunctionalInterface
public interface InventoryIterator<T extends Event> {
    /**
     * A method that returns an iterable object that can iterate over custom items from the provided event (individual elements can be null)
     *
     * @param event  the provided event
     * @param player the player involved with this methods execution
     * @return an iterable object that can iterate over custom items from the provided event
     */
    @NotNull
    Iterable<CustomItem> getItems(@NotNull T event, @NotNull Player player);

    /**
     * A method used to build an InventoryIterator from the provided function
     *
     * @param function the provided function
     * @param <T>      the type of event
     * @return an InventoryIterator from the provided function
     */
    @NotNull
    static <T extends Event> InventoryIterator<T> fromFunction(@NotNull BiFunction<T, PlayerInventory, Collection<ItemStack>> function) {
        return (event, player) -> {
            Collection<ItemStack> raw = function.apply(event, player.getInventory());

            return InnovativeItems.getInstance().getItemCache().fromItemStacks(raw);
        };
    }

    /**
     * A method used to build an InventoryIterator from the provided item from the provided function
     *
     * @param function the provided function
     * @param <T>      the type of event
     * @return an InventoryIterator from the provided item from the provided function
     */
    @NotNull
    static <T extends Event> InventoryIterator<T> fromFunctionSingleton(@NotNull BiFunction<T, PlayerInventory, ItemStack> function) {
        return fromFunction((event, inventory) -> Collections.singleton(function.apply(event, inventory)));
    }

    /**
     * An inner class used to hold constants and util methods
     */
    final class Constants {
        private static final InventoryIterator<?> ARMOR = fromFunction((event, inventory) -> Arrays.asList(inventory.getArmorContents()));
        private static final InventoryIterator<?> MAIN_HAND = fromFunctionSingleton((event, inventory) -> inventory.getItemInMainHand());
        private static final InventoryIterator<?> BOTH_HANDS = fromFunction((event, inventory) -> Arrays.asList(inventory.getItemInMainHand(), inventory.getItemInOffHand()));
        private static final InventoryIterator<?> ARMOR_AND_HANDS = fromFunction((event, inventory) -> {
            List<ItemStack> items = new ArrayList<>();
            Collections.addAll(items, inventory.getArmorContents());
            items.add(inventory.getItemInMainHand());
            items.add(inventory.getItemInOffHand());

            return items;
        });

        /**
         * Constructor to prevent people from using this util class in an object oriented way
         */
        private Constants() {}

        /**
         * A method used to return the armor iterator constant
         *
         * @param <T> the type of inventory
         * @return the armor iterator constant
         */
        @NotNull
        public static <T extends Event> InventoryIterator<T> armor() {
            return (InventoryIterator<T>) ARMOR;
        }

        /**
         * A method used to return the main hand iterator constant
         *
         * @param <T> the type of inventory
         * @return the main hand iterator constant
         */
        @NotNull
        public static <T extends Event> InventoryIterator<T> mainHand() {
            return (InventoryIterator<T>) MAIN_HAND;
        }

        /**
         * A method used to return the both hands iterator constant
         *
         * @param <T> the type of inventory
         * @return the both hands iterator constant
         */
        @NotNull
        public static <T extends Event> InventoryIterator<T> bothHands() {
            return (InventoryIterator<T>) BOTH_HANDS;
        }

        /**
         * A method used to return the armor and hands iterator constant
         *
         * @param <T> the type of inventory
         * @return the armor and hands iterator constant
         */
        @NotNull
        public static <T extends Event> InventoryIterator<T> armorAndHands() {
            return (InventoryIterator<T>) ARMOR_AND_HANDS;
        }
    }
}
