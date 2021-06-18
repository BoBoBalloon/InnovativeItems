package me.boboballoon.innovativeitems.keywords.keyword;

import me.boboballoon.innovativeitems.items.ability.AbilityTrigger;

/**
 * Represents the context in which a keyword was used in
 */
public class KeywordContext {
    private final String[] context;
    private final String abilityName;
    private final AbilityTrigger abilityTrigger;

    public KeywordContext(String[] context, String abilityName, AbilityTrigger abilityTrigger) {
        this.context = context;
        this.abilityName = abilityName;
        this.abilityTrigger = abilityTrigger;
    }

    /**
     * A method that returns the array of arguments used for the keyword
     *
     * @return the array of arguments used for the keyword
     */
    public String[] getContext() {
        return this.context;
    }

    /**
     * A method that returns the trigger of the ability that contains this keyword
     *
     * @return the trigger of the ability that contains this keyword
     */
    public AbilityTrigger getAbilityTrigger() {
        return this.abilityTrigger;
    }

    /**
     * A method that returns the name of the ability that contains this keyword
     *
     * @return the name of the ability that contains this keyword
     */
    public String getAbilityName() {
        return this.abilityName;
    }
}
