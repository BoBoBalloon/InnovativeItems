package me.boboballoon.innovativeitems.functions.context.interfaces;

import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.jetbrains.annotations.NotNull;

/**
 * An interface used to mark context that have a custom item involved
 */
public interface ItemContext {
    /**
     * A method that returns the custom item involved with this context
     *
     * @return the custom item involved with this context
     */
    @NotNull
    CustomItem getItem();
}
