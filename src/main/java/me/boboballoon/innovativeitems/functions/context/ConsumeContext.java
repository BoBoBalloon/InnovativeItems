package me.boboballoon.innovativeitems.functions.context;

import me.boboballoon.innovativeitems.functions.context.interfaces.ItemContext;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.entity.Player;

/**
 * A class that represents context where an item was eaten that can assist execution that cannot be cached and must be parsed during runtime separately
 */
public class ConsumeContext extends RuntimeContext implements ItemContext {
    private final CustomItem item;

    public ConsumeContext(Player player, Ability ability, CustomItem item) {
        super(player, ability);
        this.item = item;
    }

    /**
     * A method that returns the custom item being consumed
     *
     * @return the custom item that was involved with this context
     */
    @Override
    public CustomItem getItem() {
        return this.item;
    }
}
