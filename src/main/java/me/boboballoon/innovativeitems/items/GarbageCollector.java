package me.boboballoon.innovativeitems.items;

import de.tr7zw.nbtapi.NBTItem;
import me.boboballoon.innovativeitems.InnovativeItems;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     */
    public void cleanInventory(@NotNull Inventory inventory, boolean async) {
        if (!this.enabled) {
            LogUtil.log(LogUtil.Level.WARNING, "The garbage collector tried to run while disabled!");
            return;
        }

        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(InnovativeItems.getInstance(), () -> this.cleanup(inventory.getContents(), inventory.getType(), inventory.getLocation()));
        } else if (!Bukkit.isPrimaryThread()) { //code below runs if "!async && !Bukkit.isPrimaryThread()"
            Bukkit.getScheduler().runTask(InnovativeItems.getInstance(), () -> this.cleanup(inventory.getContents(), inventory.getType(), inventory.getLocation()));
        } else { //code below runs if "!async && Bukkit.isPrimaryThread()"
            this.cleanup(inventory.getContents(), inventory.getType(), inventory.getLocation());
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

        LogUtil.log(LogUtil.Level.INFO, "Starting player inventory cleanup...");
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.cleanInventory(player.getInventory(), async);
        }
        LogUtil.log(LogUtil.Level.INFO, "Player inventory cleanup complete!");
    }

    /**
     * A utility method that will correct the provided item to be updated based on the cache
     *
     * @param item      the itemstack to update
     * @param type      the type of inventory this item collection of item represents
     * @param location  the location of the inventory
     */
    private void cleanup(@Nullable ItemStack item, @NotNull InventoryType type, @NotNull Location location) {
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        NBTItem nbt = new NBTItem(item, true);

        if (!nbt.hasKey("innovativeplugin-customitem")) {
            return;
        }

        String identifier = nbt.getString("innovativeplugin-customitem-id");
        CustomItem customItem = InnovativeItems.getInstance().getItemCache().getItem(identifier);

        if (this.shouldDelete && customItem == null) {
            LogUtil.log(LogUtil.Level.NOISE, "Deleting custom item " + identifier + " in " + type.name() + " at " + location.toString());
            item.setAmount(0); //delete itemstack
            return;
        }

        if (!this.shouldUpdate || customItem == null || !customItem.shouldUpdateItem() || bruteCompare(customItem, item)) {
            return;
        }

        ItemStack customItemData = customItem.getItemStack();

        item.setType(customItemData.getType());
        item.setData(customItemData.getData());
        item.setItemMeta(customItemData.getItemMeta());

        if (nbt.getInteger("innovativeplugin-customitem-durability") > customItem.getMaxDurability()) {
            nbt.setInteger("innovativeplugin-customitem-durability", customItem.getMaxDurability());
        }

        LogUtil.log(LogUtil.Level.NOISE, "Updating item " + customItem.getIdentifier() + " in " + type.name() + " at " + location.toString());
    }

    /**
     * A utility method that will correct all custom items inside this array of itemstacks
     *
     * @param items     the array of itemstacks
     * @param type      the type of inventory this item collection of item represents
     * @param location  the location of the inventory
     */
    private void cleanup(@NotNull ItemStack[] items, @NotNull InventoryType type, @NotNull Location location) {
        for (ItemStack item : items) {
            this.cleanup(item, type, location);
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

        this.cleanInventory(inventory, true);
    }

    /**
     * An unoptimized way to compare if two items are the same (without considering amount or durability)
     */
    private static boolean bruteCompare(@NotNull CustomItem item, @NotNull ItemStack two) {
        ItemStack one = item.getItemStack();

        if (one.getType() != two.getType() || !two.hasItemMeta() || (one.getItemMeta() instanceof Damageable != two.getItemMeta() instanceof Damageable && one.getItemMeta() instanceof Damageable)) {
            return false;
        }

        NBTItem nbtOne = new NBTItem(one);
        NBTItem nbtTwo = new NBTItem(two);

        nbtOne.setInteger("Damage", 1); //vanilla durability
        nbtTwo.setInteger("Damage", 1);
        nbtOne.setInteger("innovativeplugin-customitem-durability", 1);
        nbtTwo.setInteger("innovativeplugin-customitem-durability", 1);

        return nbtOne.equals(nbtTwo);
    }
}
