package me.boboballoon.innovativeitems.items;

import com.google.common.collect.Sets;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;

import java.util.Set;

/**
 * A class used to make sure that custom items are not use in vanilla minecraft mechanics
 */
public final class ItemDefender implements Listener {
    private final Set<Class<? extends Inventory>> blacklistedInventories = Sets.newHashSet(AnvilInventory.class, BeaconInventory.class, BrewerInventory.class, CartographyInventory.class, CraftingInventory.class, EnchantingInventory.class, FurnaceInventory.class, GrindstoneInventory.class, LoomInventory.class, MerchantInventory.class, SmithingInventory.class, StonecutterInventory.class);

    public ItemDefender() {
        LogUtil.log(LogUtil.Level.INFO, "New item defender initialized!");
    }

    /**
     * A method used to get all the inventory types that cannot be used with a custom item
     *
     * @return a mutable set that all the inventory types that cannot be used with a custom item are contained
     */
    public Set<Class<? extends Inventory>> getBlacklistedInventories() {
        return this.blacklistedInventories;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        InnovativeCache cache = InnovativeItems.getInstance().getItemCache();
        Inventory inventory = event.getClickedInventory();

        if (inventory == null || (cache.fromItemStack(event.getCurrentItem()) == null && cache.fromItemStack(event.getCursor()) == null)) {
            return;
        }

        if (event.getClick().isShiftClick() || this.contains(inventory.getClass())) {
            event.setCancelled(true);
        }
    }

    /**
     * A util method that will check if the provided subtype is within the blacklisted set
     *
     * @param clazz the type to check
     * @return a boolean that is true if the provided subtype is within the blacklisted set
     */
    private boolean contains(Class<? extends Inventory> clazz) {
        for (Class<? extends Inventory> type : this.blacklistedInventories) {
            if (type.isAssignableFrom(clazz)) {
                return true;
            }
        }

        return false;
    }
}
