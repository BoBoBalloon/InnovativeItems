package me.boboballoon.innovativeitems.listeners;

import de.tr7zw.nbtapi.NBTItem;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.util.TextUtil;
import me.boboballoon.innovativeitems.util.armorevent.ArmorEquipEvent;
import org.bukkit.GameMode;
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

        if (!nbtItem.hasKey("innovativeplugin-customitem") || nbtItem.getBoolean("innovativeplugin-customitem-placeable")) {
            return;
        }

        event.setCancelled(true);

        String placeMessage = InnovativeItems.getInstance().getConfigManager().getFailedItemPlaceMessage();

        if (!placeMessage.equals("null")) {
            event.getPlayer().sendMessage(placeMessage);
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

            if (!nbtItem.hasKey("innovativeplugin-customitem") || !nbtItem.getBoolean("innovativeplugin-customitem-soulbound")) {
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

    /**
     * Listener used to check when a player equips armor
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerEquipArmor(ArmorEquipEvent event) {
        if (event.isCancelled()) {
            return;
        }

        ItemStack item = event.getNewArmorPiece();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        NBTItem nbt = new NBTItem(item);

        if (!nbt.hasKey("innovativeplugin-customitem") || nbt.getBoolean("innovativeplugin-customitem-wearable")) {
            return;
        }

        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        } else {
            TextUtil.sendMessage(player, "&r&cYou cannot equip this item, but you are in creative mode so I guess you get a pass...");
        }
    }
}