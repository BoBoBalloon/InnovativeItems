package me.boboballoon.innovativeitems.functions.context;

import me.boboballoon.innovativeitems.items.ability.Ability;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents context where the item was clicked that can assist execution that cannot be cached and must be parsed during runtime separately
 */
public class InteractContext extends RuntimeContext {
    private final Action action;
    private final EquipmentSlot hand;

    public InteractContext(@NotNull Player player, @NotNull Ability ability, @NotNull Action action, @NotNull EquipmentSlot hand) {
        super(player, ability);
        this.action = action;
        this.hand = hand;
    }

    /**
     * A method that returns the click action involved with this context
     *
     * @return the click action involved with this context
     */
    public Action getAction() {
        return this.action;
    }

    /**
     * A method that returns what hand slot was involved with this context
     *
     * @return what hand slot was involved with this context
     */
    public EquipmentSlot getHand() {
        return this.hand;
    }
}
