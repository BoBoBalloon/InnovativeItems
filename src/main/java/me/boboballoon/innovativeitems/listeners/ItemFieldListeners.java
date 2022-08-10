package me.boboballoon.innovativeitems.listeners;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.DurabilityUtil;
import me.boboballoon.innovativeitems.util.armorevent.ArmorEquipEvent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * A class that contains all listeners for custom item field options
 */
public final class ItemFieldListeners implements Listener {
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

        CustomItem item = InnovativeItems.getInstance().getItemCache().fromItemStack(event.getItemInHand());

        if (item == null || item.isPlaceable()) {
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
            ItemStack stack = drops.get(i);
            CustomItem item = InnovativeItems.getInstance().getItemCache().fromItemStack(stack);

            if (item == null || !item.isSoulbound()) {
                continue;
            }

            UUID uuid = event.getEntity().getUniqueId();

            if (!this.soulboundItems.containsKey(uuid)) {
                this.soulboundItems.put(uuid, new ArrayList<>());
            }

            this.soulboundItems.get(uuid).add(stack);
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

        CustomItem item = InnovativeItems.getInstance().getItemCache().fromItemStack(event.getNewArmorPiece());

        if (item == null || item.isWearable() || event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return; //will not cancel event if item is null or player is in creative mode
        }

        event.setCancelled(true);
    }

    /**
     * Listener used to determine when to calculate durability level on item
     * (unbreaking is already accounted for due to the event wont event fire if unbreaking is triggered)
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        ItemStack stack = event.getItem();
        CustomItem item = InnovativeItems.getInstance().getItemCache().fromItemStack(stack);

        if (item == null || stack.getItemMeta().isUnbreakable()) {
            return;
        }

        DurabilityUtil.setDurability(stack, DurabilityUtil.getDurability(stack) - 1);

        if (stack.getType() == Material.AIR) { //if item was deleted since it became broken
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
        }

        event.setCancelled(true);
    }
}