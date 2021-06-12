package me.boboballoon.innovativeitems.keywords.keyword.context;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

/**
 * A class that represents context where the item was clicked that can assist execution that cannot be cached and must be parsed during runtime separately
 */
public class InteractContext extends RuntimeContext {
    private final Block block;
    private final Action action;
    private final EquipmentSlot hand;

    public InteractContext(Player player, String abilityName, Block block, Action action, EquipmentSlot hand) {
        super(player, abilityName);
        this.block = block;
        this.action = action;
        this.hand = hand;
    }

    /**
     * A method that returns the block involved with this context (may be null if not present)
     *
     * @return the block involved with this context (may be null if not present)
     */
    @Nullable
    public Block getBlock() {
        return this.block;
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
