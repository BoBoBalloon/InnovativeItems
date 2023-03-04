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
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

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

        /*
        //code written for crafting repair but it turns out it doesn't even work in the first place
        Stream<ItemStack> stream = Arrays.stream(event.getInventory().getMatrix()).filter(Objects::nonNull);

        if (!(result != null && event.isRepair() && stream.allMatch(item -> cache.fromItemStack(item) != null))) {
            //logic for when result is custom item and not repair
            event.getInventory().setResult(null);
            return;
        }

        //logic for when custom item is repaired
        ItemStack[] bases = stream.toArray(ItemStack[]::new);
        CustomItem[] items = stream.map(cache::fromItemStack).toArray(CustomItem[]::new);

        if (bases.length != 2) {
            LogUtil.logUnblocked(LogUtil.Level.SEVERE, "An internal error occurred with custom crafting repair, please report this immediately!");
            LogUtil.logUnblocked(LogUtil.Level.SEVERE, "-------------------------------------------------------------------");
            Arrays.stream(items).forEach(item -> LogUtil.logUnblocked(LogUtil.Level.SEVERE, item.getIdentifier()));
            LogUtil.logUnblocked(LogUtil.Level.SEVERE, "-------------------------------------------------------------------");
            event.getInventory().setResult(null);
            return;
        }

        //Following formula stolen from https://minecraft.fandom.com/wiki/Item_repair#:~:text=min(Item%20A%20uses%20%2B%20Item%20B%20uses%20%2B%20floor(Max%20uses%20/%2020)%2C%20Max%20uses)
        double max = items[0].getMaxDurability();
        int sum = DurabilityUtil.getDurability(bases[0]) + DurabilityUtil.getDurability(bases[1]);
        DurabilityUtil.setDurability(event.getInventory().getResult(), (int) Math.min(sum + Math.floor(max / 20), max));
         */
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
        ItemStack stack = item.getItemStack();
        ItemStack slot = inventory.getFuel();
        return stack.getType().isFuel() && ((slot == null || slot.getType() == Material.AIR) || (stack.getType() == slot.getType() && slot.getAmount() + stack.getAmount() <= slot.getType().getMaxStackSize()));
    }
}