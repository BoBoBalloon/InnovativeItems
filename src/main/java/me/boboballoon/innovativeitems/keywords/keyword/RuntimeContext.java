package me.boboballoon.innovativeitems.keywords.keyword;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * A class that represents context that can assist execution that cannot be cached and must be parsed during runtime separately
 */
public class RuntimeContext {
    private final Player player;

    //optional
    @Nullable
    private final LivingEntity damaged;

    /**
     * A constructor that builds the runtime context with all parameters present
     *
     * @param player the player involved with the execution
     * @param damaged a living entity that was damaged and cause the trigger of this ability
     */
    public RuntimeContext(Player player, @Nullable LivingEntity damaged) {
        this.player = player;
        this.damaged = damaged;
    }

    /**
     * A constructor that builds the runtime context with a default player
     *
     * @param player the player involved with the execution
     */
    public RuntimeContext(Player player) {
        this(player, null);
    }

    /**
     * A method that returns the player responsible with the execution of the keyword
     *
     * @return the player responsible with the execution of the keyword
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * A method that returns the living entity that was damaged and caused the trigger of this ability
     *
     * @return the living entity that was damaged and caused the trigger of this ability (null if was not the cause)
     */
    @Nullable
    public LivingEntity getDamaged() {
        return this.damaged;
    }
}
