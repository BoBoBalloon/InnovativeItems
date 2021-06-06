package me.boboballoon.innovativeitems.items;

import me.boboballoon.innovativeitems.config.ConfigManager;
import org.bukkit.inventory.ItemStack;

/**
 * A class that represents a custom item
 */
public class Item {
    private final String name;
    private final Ability ability;
    private final ItemStack itemStack;

    public Item(String name, String abilityName, String itemName) { //incomplete
        this.name = name;
        this.ability = ConfigManager.ABILITIES.get(abilityName);
        this.itemStack = this.generateItem();
    }
    
    private ItemStack generateItem() {
        return null;
    }
}
