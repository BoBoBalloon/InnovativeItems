package me.boboballoon.innovativeitems.keywords.context;

import org.bukkit.entity.Player;

/**
 * A class that represents context that can assist execution that cannot be cached and must be parsed during runtime separately
 */
public class RuntimeContext {
    private final Player player;
    private final String abilityName;

    /**
     * A constructor that builds the runtime context
     *
     * @param player the player involved with the execution
     */
    public RuntimeContext(Player player, String abilityName) {
        this.player = player;
        this.abilityName = abilityName;
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
     * A method that returns the name of the ability that fired this keyword
     *
     * @return the name of the ability that fired this keyword
     */
    public String getAbilityName() {
        return this.abilityName;
    }
}
