package me.boboballoon.innovativeitems.listeners;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A class that contains all listeners for the block placeable field
 */
public class BlockPlaceableListener implements Listener {
    /**
     * Listener used to check when a custom block is placed
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.canBuild() || event.isCancelled()) {
            return;
        }

        ItemStack item = event.getItemInHand();

        if (item.getType() == Material.AIR) {
            return;
        }

        NBTItem nbtItem = new NBTItem(item);

        if (!nbtItem.hasKey("innovativeplugin-customitem")) {
            return;
        }

        boolean placeable = nbtItem.getBoolean("innovativeplugin-customitem-placeable");

        if (!placeable) {
            event.setCancelled(true);
        }
    }
}