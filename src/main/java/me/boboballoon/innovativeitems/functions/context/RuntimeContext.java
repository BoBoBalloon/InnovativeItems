package me.boboballoon.innovativeitems.functions.context;

import me.boboballoon.innovativeitems.items.ability.AbilityTrigger;
import org.bukkit.entity.Player;

/**
 * A class that represents context that can assist execution that cannot be cached and must be parsed during runtime separately
 */
public class RuntimeContext {
    private final Player player;
    private final String abilityName;
    private final AbilityTrigger abilityTrigger;

    /**
     * A constructor that builds the runtime context
     *
     * @param player the player involved with the execution
     * @param abilityName the name of the ability involved with the execution
     * @param abilityTrigger the trigger of the ability involved with the execution
     */
    public RuntimeContext(Player player, String abilityName, AbilityTrigger abilityTrigger) {
        this.player = player;
        this.abilityName = abilityName;
        this.abilityTrigger = abilityTrigger;
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

    /**
     * A method that returns the trigger of the ability that fired this keyword
     *
     * @return the trigger of the ability that fired this keyword
     */
    public AbilityTrigger getAbilityTrigger() {
        return this.abilityTrigger;
    }
}
