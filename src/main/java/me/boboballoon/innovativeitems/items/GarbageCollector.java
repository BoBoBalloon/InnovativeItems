package me.boboballoon.innovativeitems.items;

import de.tr7zw.nbtapi.NBTItem;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * A class that is responsible for cleaning up "dead" items in game
 */
public final class GarbageCollector implements Listener {
    private boolean enabled;
    private boolean shouldUpdate;
    private boolean shouldDelete;

    /**
     * The constructor that builds the basic garbage collector class
     *
     * @param shouldUpdate whether the garbage collector should update items that don't match their cached counterpart
     * @param shouldDelete whether the garbage collector should delete items that are not currently in the cache
     */
    public GarbageCollector(boolean shouldUpdate, boolean shouldDelete) {
        this.enabled = true;
        this.shouldUpdate = shouldUpdate;
        this.shouldDelete = shouldDelete;
        LogUtil.log(LogUtil.Level.INFO, "New garbage collector initialized!");
    }

    /**
     * A method that returns a boolean that is true when the garbage collector is enabled
     *
     * @return a boolean that is true when the garbage collector is enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * A method that will set a boolean that when true the garbage collector will be enabled
     *
     * @param enabled a boolean that when true the garbage collector will be enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * A method that returns a boolean that is true when the garbage collector is set to update item mismatches
     *
     * @return a boolean that is true when the garbage collector is set to update item mismatches
     */
    public boolean shouldUpdateItems() {
        return this.shouldUpdate;
    }

    /**
     * A method that will set a boolean that when true the garbage collector will update item mismatches
     *
     * @param shouldUpdate a boolean that when true the garbage collector will update item mismatches
     */
    public void setShouldUpdate(boolean shouldUpdate) {
        this.shouldUpdate = shouldUpdate;
    }

    /**
     * A method that returns a boolean that is true when the garbage collector is set to delete items not found in cache
     *
     * @return a boolean that is true when the garbage collector is set to delete items not found in cache
     */
    public boolean shouldDeleteItems() {
        return this.shouldDelete;
    }

    /**
     * A method that will set a boolean that when true the garbage collector will delete items not found in cache
     *
     * @param shouldDelete a boolean that when true the garbage collector will delete items not found in cache
     */
    public void setShouldDelete(boolean shouldDelete) {
        this.shouldDelete = shouldDelete;
    }

    /**
     * A method that will clean an inventory based on the options set in the garbage collector
     *
     * @param inventory the inventory that will be cleaned
     * @param async a boolean that is true when this method should be scheduled to fire async
     */
    public void cleanInventory(Inventory inventory, boolean async) {
        if (!this.enabled) {
            LogUtil.log(LogUtil.Level.WARNING, "The garbage collector tried to run while disabled!");
            return;
        }

        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(InnovativeItems.getInstance(), () -> this.cleanup(inventory));
        } else {
            this.cleanup(inventory);
        }
    }

    /**
     * A method that will clean out all players inventories online
     *
     * @param async whether or not this method should clean up all player inventories on another thread
     */
    public void cleanAllPlayerInventories(boolean async) {
        if (!this.enabled) {
            LogUtil.log(LogUtil.Level.WARNING, "The garbage collector tried to run while disabled!");
            return;
        }

        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(InnovativeItems.getInstance(), this::cleanupAll);
        } else {
            this.cleanupAll();
        }
    }

    /**
     * A method used to clean all player inventories
     */
    private void cleanupAll() {
        LogUtil.log(LogUtil.Level.INFO, "Starting player inventory cleanup...");
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.cleanInventory(player.getInventory(), false);
        }
        LogUtil.log(LogUtil.Level.INFO, "Player inventory cleanup complete!");
    }

    /**
     * A method used to clean one inventory
     *
     * @param inventory the inventory to cleanup
     */
    private void cleanup(Inventory inventory) {
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];

            if (item == null) {
                continue;
            }

            NBTItem nbtItem = new NBTItem(item);

            if (!nbtItem.hasKey("innovativeplugin-customitem")) {
                continue;
            }

            CustomItem customItem = InnovativeItems.getInstance().getItemCache().getItem(nbtItem.getString("innovativeplugin-customitem-id"));

            if (customItem == null) {
                if (this.shouldDelete) {
                    item.setAmount(0); //delete itemstack
                }

                continue;
            }

            if (!this.shouldUpdate) {
                continue;
            }

            ItemStack customItemstack = customItem.getItemStack();

            if (item.isSimilar(customItemstack)) {
                continue;
            }

            ItemStack newItem = customItemstack.clone();

            newItem.setAmount(item.getAmount());

            inventory.setItem(i, newItem);
        }
    }

    /**
     * Listen for when a player joins and check their inventory
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        LogUtil.log(LogUtil.Level.NOISE, "Cleaning up " + player.getName() + "'s inventory because they joined the game!");

        this.cleanInventory(player.getInventory(), true);
    }

    /**
     * Listen for when a container opens
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder() instanceof Container)) {
            return;
        }

        LogUtil.log(LogUtil.Level.NOISE, "Cleaning up container inventory that " + event.getPlayer().getName() + " opened!");

        this.cleanInventory(inventory, true);
    }

    /**
     * Listen for when an item is picked up
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onItemPickup(InventoryPickupItemEvent event) {
        NBTItem nbtItem = new NBTItem(event.getItem().getItemStack());

        if (!nbtItem.hasKey("innovativeplugin-customitem")) {
            return;
        }

        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder() instanceof Player)) {
            return;
        }

        Player player = (Player) inventory.getHolder();

        LogUtil.log(LogUtil.Level.NOISE, "Cleaning up " + player.getName() + "'s inventory because they picked up a custom item!");

        this.cleanInventory(inventory, true);
    }
}
