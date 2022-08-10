package me.boboballoon.innovativeitems.listeners;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * A class that contains that listeners for crafting support
 */
public final class CraftingListener implements Listener {

    /**
     * Listener used to check if the result of a crafting recipe is a vanilla item with a custom item as an ingredient
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        InnovativeCache cache = InnovativeItems.getInstance().getItemCache();

        boolean containsCustomItems = false;
        for (ItemStack itemStack : event.getInventory().getMatrix()) {
            CustomItem item = cache.fromItemStack(itemStack);

            if (item != null) {
                containsCustomItems = true;
                break;
            }
        }

        if (!containsCustomItems) {
            return;
        }

        CustomItem result = cache.fromItemStack(event.getInventory().getResult());

        if (result != null || result != null && event.isRepair()) {
            return;
        }

        event.getInventory().setResult(null);
    }

    /**
     * Listener used to check if the result of a furnace recipe is a vanilla item with a custom item as an ingredient
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockCook(BlockCookEvent event) {
        Bukkit.broadcastMessage("fired: " + event.getClass().getSimpleName());

        InnovativeCache cache = InnovativeItems.getInstance().getItemCache();

        if (cache.fromItemStack(event.getSource()) == null || cache.fromItemStack(event.getResult()) == null) {
            return;
        }

        event.setCancelled(true);
    }

    /**
     * Listener used to check if the result of a furnace recipe is a vanilla item with a custom item as an ingredient
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        if (InnovativeItems.getInstance().getItemCache().fromItemStack(event.getFuel()) == null) {
            return;
        }

        FurnaceInventory inventory = (FurnaceInventory) ((InventoryHolder) event.getBlock().getBlockData()).getInventory();

        inventory.setFuel(null);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), event.getFuel());

        event.setBurning(false);
        event.setCancelled(true);
    }
}