package me.boboballoon.innovativeitems.util;

import me.boboballoon.innovativeitems.InnovativeItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A class used to store util methods regarding inventories
 */
public final class InventoryUtil {
    /**
     * Constructor to prevent people from using this util class in an object oriented way
     */
    private InventoryUtil() {}

    /**
     * A method that is used to give a player an item safely
     *
     * @param player the player you want to give an item to
     * @param item the item you want to give them
     * @param amount the amount of times to add the itemstack to the inventory
     */
    public static void giveItem(@NotNull Player player, @NotNull ItemStack item, int amount) {
        boolean mainThread = Bukkit.isPrimaryThread();
        Runnable add = () -> {
            for (int i = 0; i < amount; i++) {
                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(item);
                    continue;
                }

                int difference = amount - i;
                Runnable drop = () -> {
                    for (int j = 0; j < difference; j++) {
                        player.getWorld().dropItemNaturally(player.getLocation(), item);
                    }
                };


                if (!mainThread) {
                    Bukkit.getScheduler().runTask(InnovativeItems.getInstance(), drop);
                } else {
                    drop.run();
                }
                return;
            }
        };

        if (mainThread) {
            Bukkit.getScheduler().runTaskAsynchronously(InnovativeItems.getInstance(), add);
        } else {
            add.run();
        }
    }
}
