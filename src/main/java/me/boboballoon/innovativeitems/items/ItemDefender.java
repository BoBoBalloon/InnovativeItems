package me.boboballoon.innovativeitems.items;

import de.tr7zw.nbtapi.NBTItem;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;

/**
 * A class used to make sure that custom items are not use in vanilla minecraft mechanics
 */
public final class ItemDefender implements Listener {
    private boolean allowCrafting;
    private boolean allowSmelting;
    private boolean allowBrewing;

    public ItemDefender(boolean allowCrafting, boolean allowSmelting, boolean allowBrewing) {
        this.allowCrafting = allowCrafting;
        this.allowSmelting = allowSmelting;
        this.allowBrewing = allowBrewing;
        LogUtil.log(LogUtil.Level.INFO, "New item defender initialized!");
    }

    /**
     * A method that returns a boolean that is true when custom items are allowed to be used in crafting recipes
     *
     * @return a boolean that is true when custom items are allowed to be used in crafting recipes
     */
    public boolean isCraftingAllowed() {
        return this.allowCrafting;
    }

    /**
     * A method that returns a boolean that is true when custom items are allowed to be used in smelting recipes
     *
     * @return a boolean that is true when custom items are allowed to be used in smelting recipes
     */
    public boolean isSmeltingAllowed() {
        return this.allowSmelting;
    }

    /**
     * A method that returns a boolean that is true when custom items are allowed to be used in brewing recipes
     *
     * @return a boolean that is true when custom items are allowed to be used in brewing recipes
     */
    public boolean isBrewingAllowed() {
        return this.allowBrewing;
    }

    /**
     * A method that is used to set if the defender should allow custom items to be used in crafting recipes
     *
     * @param allowCrafting a boolean that is true when custom items are allowed to be used in crafting recipes
     */
    public void shouldAllowCrafting(boolean allowCrafting) {
        this.allowCrafting = allowCrafting;
    }

    /**
     * A method that is used to set if the defender should allow custom items to be used in smelting recipes
     *
     * @param allowSmelting a boolean that is true when custom items are allowed to be used in smelting recipes
     */
    public void shouldAllowSmelting(boolean allowSmelting) {
        this.allowSmelting = allowSmelting;
    }

    /**
     * A method that is used to set if the defender should allow custom items to be used in brewing recipes
     *
     * @param allowBrewing a boolean that is true when custom items are allowed to be used in brewing recipes
     */
    public void shouldAllowBrewing(boolean allowBrewing) {
        this.allowBrewing = allowBrewing;
    }

    /**
     * Listen for when an anvil is preparing to modify an item
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        for (int i = 0; i <= 1; i++) {
            ItemStack item = inventory.getItem(i);

            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            NBTItem customItem = new NBTItem(item);

            if (customItem.hasKey("innovativeplugin-customitem")) {
                event.setResult(null);
                inventory.getViewers().stream().filter(player -> player instanceof Player).forEach(human -> {
                    Player player = (Player) human;
                    player.updateInventory();
                });
                return;
            }
        }
    }

    /**
     * Listen for when an item is about to be crafted
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPrepareItemCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();

        if (this.allowCrafting || inventory.getResult() == null) {
            return;
        }

        for (ItemStack item : inventory.getMatrix()) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            NBTItem customItem = new NBTItem(item);

            if (customItem.hasKey("innovativeplugin-customitem")) {
                inventory.setResult(null);
                return;
            }
        }
    }

    /**
     * Listen for when an item is about to be enchanted
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        this.onCancellable(event, event.getItem());
    }

    /**
     * Listen for when an item is about to be modified in a smithing table
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPrepareSmithing(PrepareSmithingEvent event) {
        SmithingInventory inventory = event.getInventory();
        for (int i = 0; i <= 1; i++) {
            ItemStack item = inventory.getItem(i);

            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            NBTItem customItem = new NBTItem(item);

            if (customItem.hasKey("innovativeplugin-customitem")) {
                event.setResult(null);
                inventory.getViewers().stream().filter(player -> player instanceof Player).forEach(human -> {
                    Player player = (Player) human;
                    player.updateInventory();
                });
                return;
            }
        }
    }

    /**
     * Listen for when an item is smelted
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onFurnaceSmelt(FurnaceSmeltEvent event) {
        if (this.allowSmelting) {
            return;
        }

        this.onCancellable(event, event.getSource());
    }

    /**
     * Listen for when an item is smelted
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onFurnaceBurn(FurnaceBurnEvent event) {
        if (this.allowSmelting) {
            return;
        }

        this.onCancellable(event, event.getFuel());
    }

    /**
     * Listen for when an item is used in a brewing stand
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBrew(BrewEvent event) {
        if (this.allowBrewing) {
            return;
        }

        this.onCancellable(event, event.getContents().getIngredient());
    }

    /**
     * Listen for when an item is used in a brewing stand
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBrewingStandFuel(BrewingStandFuelEvent event) {
        if (this.allowBrewing) {
            return;
        }

        this.onCancellable(event, event.getFuel());
    }

    /**
     * Logic to be used on cancellable related events to prevent repetitive code
     */
    private void onCancellable(Cancellable cancellable, ItemStack item) {
        NBTItem customItem = new NBTItem(item);

        if (customItem.hasKey("innovativeplugin-customitem")) {
            cancellable.setCancelled(true);
        }
    }
}
