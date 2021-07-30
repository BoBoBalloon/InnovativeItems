package me.boboballoon.innovativeitems.functions.context.interfaces;

import org.bukkit.block.Block;

/**
 * An interface used to mark context that have a block involved
 */
public interface BlockContext {
    /**
     * A method that returns the block involved with this context
     *
     * @return the block involved with this context
     */
    Block getBlock();
}
