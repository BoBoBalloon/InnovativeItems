package me.boboballoon.innovativeitems.functions.context;

import me.boboballoon.innovativeitems.items.ability.AbilityTrigger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A class that represents context where an item was eaten that can assist execution that cannot be cached and must be parsed during runtime separately
 */
public class ConsumeContext extends RuntimeContext {
    private final ItemStack item;

    public ConsumeContext(Player player, String abilityName, AbilityTrigger abilityTrigger, ItemStack item) {
        super(player, abilityName, abilityTrigger);
        this.item = item;
    }

    /**
     * A method that returns the itemstack being consumed
     *
     * @return the living entity that was involved with this context
     */
    public ItemStack getItem() {
        return this.item;
    }
}
