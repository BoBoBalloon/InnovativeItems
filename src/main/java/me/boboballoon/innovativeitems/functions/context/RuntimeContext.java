package me.boboballoon.innovativeitems.functions.context;

import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.AbilityTrigger;
import org.bukkit.entity.Player;

/**
 * A class that represents context that can assist execution that cannot be cached and must be parsed during runtime separately
 */
public class RuntimeContext {
    private final Player player;
    private final Ability ability;

    /**
     * A constructor that builds the runtime context
     *
     * @param player the player involved with the execution
     * @param ability the ability involved with the execution
     */
    public RuntimeContext(Player player, Ability ability) {
        this.player = player;
        this.ability = ability;
    }

    /**
     * A method that returns the player responsible with the execution of the function
     *
     * @return the player responsible with the execution of the keyword
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * A method that returns the ability involved with execution of the function
     *
     * @return the ability involved with execution of the function
     */
    public Ability getAbility() {
        return this.ability;
    }

    /**
     * A method that returns the name of the ability that fired this function
     *
     * @return the name of the ability that fired this keyword
     */
    public String getAbilityName() {
        return this.ability.getIdentifier();
    }

    /**
     * A method that returns the trigger of the ability that fired this function
     *
     * @return the trigger of the ability that fired this keyword
     */
    public AbilityTrigger getAbilityTrigger() {
        return this.ability.getTrigger();
    }
}
