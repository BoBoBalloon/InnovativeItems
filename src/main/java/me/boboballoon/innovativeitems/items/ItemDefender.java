package me.boboballoon.innovativeitems.items;

import com.google.common.collect.Sets;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * A class used to make sure that custom items are not use in vanilla minecraft mechanics
 */
public final class ItemDefender implements Listener {
    private final Set<Class<? extends Inventory>> blacklistedInventories = Sets.newHashSet(AnvilInventory.class, BeaconInventory.class, BrewerInventory.class, CartographyInventory.class, EnchantingInventory.class, GrindstoneInventory.class, LoomInventory.class, MerchantInventory.class, SmithingInventory.class, StonecutterInventory.class);
    private boolean enabled;

    public ItemDefender(boolean enabled) {
        this.enabled = enabled;
        LogUtil.log(LogUtil.Level.INFO, "New item defender initialized!");
    }

    /**
     * A method used to get all the inventory types that cannot be used with a custom item
     *
     * @return a mutable set that all the inventory types that cannot be used with a custom item are contained
     */
    @NotNull
    public Set<Class<? extends Inventory>> getBlacklistedInventories() {
        return this.blacklistedInventories;
    }

    /**
     * A method used to determine if the item defender system is enabled
     *
     * @return if the item defender system is enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * A method used to set if the item defender system is enabled
     *
     * @param enabled if the item defender system is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!this.enabled) {
            return;
        }

        InnovativeCache cache = InnovativeItems.getInstance().getItemCache();
        Inventory clicked = event.getClickedInventory();
        Inventory top = event.getInventory();

        CustomItem slot = cache.fromItemStack(event.getCurrentItem());
        CustomItem cursor = cache.fromItemStack(event.getCursor());

        if (clicked == null || ((slot != null && !slot.shouldUpdateItem()) || (cursor != null && !cursor.shouldUpdateItem())) || !(this.contains(clicked.getClass()) || (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && top.getType() != InventoryType.CRAFTING && this.contains(top.getClass()))) || (slot == null && cursor == null)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!this.enabled) {
            return;
        }

        InnovativeCache cache = InnovativeItems.getInstance().getItemCache();
        Inventory inventory = event.getInventory();
        CustomItem cursor = cache.fromItemStack(event.getOldCursor());

        if (cursor == null || (cursor != null && !cursor.shouldUpdateItem()) || !this.contains(inventory.getClass())) {
            return;
        }

        Set<Inventory> inventories = new HashSet<>();
        for (int slot : event.getRawSlots()) {
            inventories.add(event.getView().getInventory(slot));
        }

        if (inventories.size() > 1 || this.contains((Class<? extends Inventory>) inventories.toArray()[0].getClass())) {
            event.setCancelled(true);
        }
    }

    /**
     * A util method that will check if the provided subtype is within the blacklisted set
     *
     * @param clazz the type to check
     * @return a boolean that is true if the provided subtype is within the blacklisted set
     */
    private boolean contains(@NotNull Class<? extends Inventory> clazz) {
        for (Class<? extends Inventory> type : this.blacklistedInventories) {
            if (type.isAssignableFrom(clazz)) {
                return true;
            }
        }

        return false;
    }

    /**
     * A method used to safely obtain a class based on its qualified name (for later updates)
     *
     * @param name the classes fully qualified name
     * @return the instance of the class, or null
     */
    @Nullable
    private Class<? extends Inventory> findClass(@NotNull String name) {
        try {
            return (Class<? extends Inventory>) Class.forName(name);
        } catch (ClassNotFoundException | ClassCastException e) {
            return null;
        }
    }
}
