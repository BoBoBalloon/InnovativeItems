package me.boboballoon.innovativeitems.items;

import com.google.common.collect.Sets;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * A class used to make sure that custom items are not use in vanilla minecraft mechanics
 */
public final class ItemDefender implements Listener {
    private final Set<Class<? extends Inventory>> blacklistedInventories = Sets.newHashSet(AnvilInventory.class, BeaconInventory.class, BrewerInventory.class, CartographyInventory.class, CraftingInventory.class, EnchantingInventory.class, FurnaceInventory.class, GrindstoneInventory.class, LoomInventory.class, MerchantInventory.class, SmithingInventory.class, StonecutterInventory.class);
    private boolean enabled;
    private boolean closeInventories;

    public ItemDefender(boolean enabled, boolean closeInventories) {
        this.enabled = enabled;
        this.closeInventories = closeInventories;

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

    /**
     * A method used to get if the item defender should forcibly close inventories
     *
     * @return if the item defender should forcibly close inventories
     */
    public boolean shouldCloseInventories() {
        return this.closeInventories;
    }

    /**
     * A method used to set if the item defender should forcibly close inventories
     *
     * @param shouldCloseInventories if the item defender should forcibly close inventories
     */
    public void setShouldCloseInventories(boolean shouldCloseInventories) {
        this.closeInventories = shouldCloseInventories;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!this.enabled) {
            return;
        }

        InnovativeCache cache = InnovativeItems.getInstance().getItemCache();
        Inventory inventory = event.getClickedInventory();

        if (inventory == null || (cache.fromItemStack(event.getCurrentItem()) == null && cache.fromItemStack(event.getCursor()) == null)) {
            return;
        }

        if (event.getClick().isShiftClick() || this.contains(inventory.getClass())) {
            return;
        }

        event.setCancelled(true);

        if (this.closeInventories) {
            TextUtil.sendMessage(event.getWhoClicked(), "&r&cPlease do not place a custom item in this inventory, it could be destroyed...");
            Bukkit.getScheduler().runTask(InnovativeItems.getInstance(), () -> event.getWhoClicked().closeInventory());
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
