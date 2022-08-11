package me.boboballoon.innovativeitems.listeners;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Campfire;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        InnovativeCache cache = InnovativeItems.getInstance().getItemCache();

        if (cache.fromItemStack(event.getSource()) == null || cache.fromItemStack(event.getResult()) != null) {
            return;
        }

        event.setCancelled(true);

        if (event.getBlock().getState() instanceof Furnace) {
            Furnace furnace = (Furnace) event.getBlock().getState();

            furnace.setCookTime((short) 0);
            furnace.setCookTimeTotal(Integer.MAX_VALUE);
            furnace.update();
        } else if (event.getBlock().getState() instanceof Campfire) {
            Campfire campfire = (Campfire) event.getBlock().getState();
            Location location = campfire.getLocation().clone().add(0, 1, 0);
            for (int i = 0; i < campfire.getSize(); i++) {
                ItemStack stack = campfire.getItem(i);
                CustomItem item = cache.fromItemStack(stack);
                if (item != null && (item.getRecipes() == null || item.getRecipes().stream().noneMatch(recipe -> recipe instanceof CampfireRecipe && ((CampfireRecipe) recipe).getInputChoice().test(stack)))) {
                    campfire.setItem(i, null);
                    location.getWorld().dropItem(location, stack);
                    campfire.update();
                    i--;
                }
            }
        }
    }

    /**
     * Listener used to check if a custom item is being placed in a furnace when it shouldn't be
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        InnovativeCache cache = InnovativeItems.getInstance().getItemCache();

        if ((event.getClickedInventory() instanceof FurnaceInventory && event.getRawSlot() == 1 && cache.fromItemStack(event.getCursor()) != null) ||
                (event.getClickedInventory() instanceof PlayerInventory && event.getView().getTopInventory() instanceof FurnaceInventory && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && cache.fromItemStack(event.getCurrentItem()) != null && this.isIllegalShiftClick((FurnaceInventory) event.getView().getTopInventory(), cache.fromItemStack(event.getCurrentItem())))) {
            event.setCancelled(true); //1 is the fuel slot of a furnace inventory
        }
    }

    /**
     * Listener used to check if a custom item is being placed in a furnace when it shouldn't be
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getInventory() instanceof FurnaceInventory)) { //the instanceof check also checks if the object is null so we good
            return;
        }

        if (!event.getRawSlots().contains(1) || InnovativeItems.getInstance().getItemCache().fromItemStack(event.getOldCursor()) == null) { //one is the fuel slot of a furnace inventory
            return;
        }

        event.setCancelled(true);
    }

    /**
     * A method used to check if a player shift-clicking a custom item into a furnace is valid
     *
     * @param inventory the furnace inventory being clicked into
     * @param item the custom item to be moved into it
     * @return if a player shift-clicking a custom item into a furnace is valid
     */
    private boolean isIllegalShiftClick(@NotNull FurnaceInventory inventory, @NotNull CustomItem item) {
        return this.canMoveToSlot(inventory.getFuel(), item) || (this.canMoveToSlot(inventory.getResult(), item) && !this.hasValidRecipe(inventory, item));
    }

    /**
     * A method used to check if an item can be moved into an inventory slot
     *
     * @param slot the item that represents said inventory slot
     * @param item the custom item to be moved to said slot
     * @return if an item can be moved into an inventory slot
     */
    private boolean canMoveToSlot(@Nullable ItemStack slot, @NotNull CustomItem item) {
        ItemStack stack = item.getItemStack();
        return (slot == null || slot.getType() == Material.AIR) || (stack.getType() == slot.getType() && slot.getAmount() + stack.getAmount() <= slot.getType().getMaxStackSize());
    }

    /**
     * A method used to check if the provided custom item has a crafting recipe that is valid in said type of furnace
     *
     * @param inventory the furnace inventory
     * @param item the custom item to be smelted
     * @return if the provided custom item has a crafting recipe that is valid in said type of furnace
     */
    private boolean hasValidRecipe(@NotNull FurnaceInventory inventory, @NotNull CustomItem item) {
        if (item.getRecipes() == null) {
            return false;
        }

        for (Recipe recipe : item.getRecipes()) {
            if ((recipe instanceof BlastingRecipe && inventory.getType() == InventoryType.BLAST_FURNACE) ||
                    (recipe instanceof SmokingRecipe && inventory.getType() == InventoryType.SMOKER) ||
                    (recipe instanceof FurnaceRecipe && inventory.getType() == InventoryType.FURNACE)) {
                return true;
            }
        }

        return false;
    }
}