package me.boboballoon.innovativeitems.listeners;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A class that contains that listeners for crafting support
 */
public final class CraftingListener implements Listener {

    /**
     * Listener used to check if the result of a crafting recipe is a vanilla item with a custom item as an ingredient
     */
    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
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

        if (result != null && event.isRepair()) {
            return;
        }

        event.getInventory().setResult(null);
    }
}