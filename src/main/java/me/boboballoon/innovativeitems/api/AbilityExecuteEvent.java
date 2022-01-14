package me.boboballoon.innovativeitems.api;

import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.items.ability.Ability;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * A class used to represent an event that is called when an ability is executed
 * Does not override {@link org.bukkit.event.player.PlayerEvent} due to the fact that it is not possible to make it async without reflection and that's dumb
 */
public class AbilityExecuteEvent extends Event implements Cancellable {
    private final RuntimeContext context;
    private boolean cancelled = false;

    private static final HandlerList HANDLER = new HandlerList();

    public AbilityExecuteEvent(RuntimeContext context) {
        super(true);
        this.context = context;
    }

    /**
     * A method used to return the context given to the ability
     *
     * @return the context given to the ability
     */
    public RuntimeContext getContext() {
        return this.context;
    }

    /**
     * A method used to return the ability attempting to be executed
     *
     * @return the ability attempting to be executed
     */
    public Ability getAbility() {
        return this.context.getAbility();
    }

    /**
     * A method used to return the player executing the ability
     *
     * @return the player executing the ability
     */
    public Player getPlayer() {
        return this.context.getPlayer();
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLER;
    }

    public static HandlerList getHandlerList() {
        return HANDLER;
    }
}
