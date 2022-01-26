package me.boboballoon.innovativeitems.functions.context.interfaces;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * An interface used to mark context that have another entity involved
 */
public interface EntityContext {
    /**
     * A method that returns the other entity involved with this context
     *
     * @return the other entity involved with this context
     */
    @NotNull
    LivingEntity getEntity();
}
