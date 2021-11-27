package me.boboballoon.innovativeitems.listeners;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * A class that contains all listeners for custom item field options
 */
public class ItemFieldListeners implements Listener {
    private final Map<UUID, List<ItemStack>> soulboundItems;

    public ItemFieldListeners() {
        this.soulboundItems = new HashMap<>();
    }

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

    /**
     * Listener used to check when a player dies
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getKeepInventory()) {
            return;
        }

        List<ItemStack> drops = event.getDrops();

        for (int i = 0; i < drops.size(); i++) {
            ItemStack item = drops.get(i);

            if (item.getType() == Material.AIR) {
                continue;
            }

            NBTItem nbtItem = new NBTItem(item);

            if (!nbtItem.hasKey("innovativeplugin-customitem")) {
                continue;
            }

            boolean soulbound = nbtItem.getBoolean("innovativeplugin-customitem-soulbound");

            if (!soulbound) {
                continue;
            }

            UUID uuid = event.getEntity().getUniqueId();

            if (!this.soulboundItems.containsKey(uuid)) {
                this.soulboundItems.put(uuid, new ArrayList<>());
            }

            this.soulboundItems.get(uuid).add(item);
            drops.remove(i);
            i--; //to make sure that we move one index back because we just removed an index
        }
    }

    /**
     * Listener used to check when a player respawns
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!this.soulboundItems.containsKey(uuid)) {
            return;
        }

        ItemStack[] items = this.soulboundItems.remove(uuid).toArray(new ItemStack[0]);

        player.getInventory().addItem(items);
    }
}