package me.boboballoon.innovativeitems.items;

import de.tr7zw.nbtapi.NBTItem;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * @param async     a boolean that is true when this method should be scheduled to fire async
     * @param player    a player that the inventory revolves around
     */
    public void cleanInventory(@NotNull Inventory inventory, boolean async, @Nullable Player player) {
        if (!this.enabled) {
            LogUtil.log(LogUtil.Level.WARNING, "The garbage collector tried to run while disabled!");
            return;
        }

        List<ItemStack> items = new ArrayList<>(Arrays.asList(inventory.getContents()));

        if (player != null) {
            items.add(player.getItemOnCursor());
        }

        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(InnovativeItems.getInstance(), () -> this.cleanup(items, inventory.getType(), inventory.getLocation()));
        } else if (!Bukkit.isPrimaryThread()) { //code below runs if "!async && !Bukkit.isPrimaryThread()"
            Bukkit.getScheduler().runTask(InnovativeItems.getInstance(), () -> this.cleanup(items, inventory.getType(), inventory.getLocation()));
        } else { //code below runs if "!async && Bukkit.isPrimaryThread()"
            this.cleanup(items, inventory.getType(), inventory.getLocation());
        }
    }

    /**
     * A method that will clean an inventory based on the options set in the garbage collector
     *
     * @param inventory the inventory that will be cleaned
     * @param async     a boolean that is true when this method should be scheduled to fire async
     */
    public void cleanInventory(@NotNull Inventory inventory, boolean async) {
        this.cleanInventory(inventory, async, null);
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
     * A utility method that will correct all custom items inside this list of itemstacks
     *
     * @param items    the list of itemstacks
     * @param type     the type of inventory this item collection of item represents
     * @param location the location of the inventory
     */
    private void cleanup(@NotNull List<ItemStack> items, @NotNull InventoryType type, @NotNull Location location) {
        for (ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            NBTItem nbt = new NBTItem(item);

            if (!nbt.hasKey("innovativeplugin-customitem")) {
                continue;
            }

            String identifier = nbt.getString("innovativeplugin-customitem-id");
            CustomItem customItem = InnovativeItems.getInstance().getItemCache().getItem(identifier);

            if (this.shouldDelete && customItem == null) {
                LogUtil.log(LogUtil.Level.NOISE, "Deleting custom item " + identifier + " in " + type.name() + " at " + location.toString());
                item.setAmount(0); //delete itemstack
                continue;
            }

            if (!this.shouldUpdate || customItem == null || bruteCompare(customItem, item)) {
                continue;
            }

            ItemStack customItemData = customItem.getItemStack();

            item.setType(customItemData.getType());
            item.setData(customItemData.getData());
            item.setItemMeta(customItemData.getItemMeta());

            LogUtil.log(LogUtil.Level.NOISE, "Updating item " + customItem.getIdentifier() + " in " + type.name() + " at " + location.toString());
        }
    }

    /**
     * Listen for when a player joins and check their inventory
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        LogUtil.log(LogUtil.Level.NOISE, "Cleaning up " + player.getName() + "'s inventory because they joined the game!");

        this.cleanInventory(player.getInventory(), true, player);
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
    private void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Inventory inventory = player.getInventory();

        ItemStack item = event.getItem().getItemStack();
        NBTItem nbt = new NBTItem(item);

        if (!nbt.hasKey("innovativeplugin-customitem")) {
            return;
        }

        LogUtil.log(LogUtil.Level.NOISE, "Cleaning up " + player.getName() + "'s inventory because they picked up a custom item!");

        this.cleanInventory(inventory, true, player);
    }

    /**
     * An unoptimized way to compare if two items are the same (without considering amount or durability)
     */
    private static boolean bruteCompare(@NotNull CustomItem item, @NotNull ItemStack two) {
        ItemStack one = item.getItemStack();

        if (one.getType() != two.getType() || !two.hasItemMeta()) {
            return false;
        }

        ItemMeta metaOne = one.getItemMeta();
        ItemMeta metaTwo = two.getItemMeta();

        boolean customDamageable = metaOne instanceof Damageable;
        boolean stackDamageable = metaTwo instanceof Damageable;

        if (customDamageable != stackDamageable) {
            return false;
        }

        if (customDamageable) { //already passed condition above, so if this is true, both are true
            metaOne = metaOne.clone();
            metaTwo = metaTwo.clone();

            ((Damageable) metaOne).setDamage(1);
            ((Damageable) metaTwo).setDamage(1);
        }

        return Bukkit.getItemFactory().equals(metaOne, metaTwo);
    }
}
