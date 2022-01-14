package me.boboballoon.innovativeitems.functions;

import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;

/**
 * Represents the context in which a function was used in
 */
public class FunctionContext {
    private final InnovativeFunction<?> function;
    private final String[] rawArguments;
    private final String abilityName;
    private final AbilityTrigger<?, ?> abilityTrigger;
    private final int lineNumber;

    public FunctionContext(InnovativeFunction<?> function, String[] rawArguments, String abilityName, AbilityTrigger<?, ?> abilityTrigger, int lineNumber) {
        this.function = function;
        this.rawArguments = rawArguments;
        this.abilityName = abilityName;
        this.abilityTrigger = abilityTrigger;
        this.lineNumber = lineNumber;
    }

    /**
     * A method that returns the type of function being used as a base for the active function
     *
     * @return the type of keyword being used as a base for the active function
     */
    public InnovativeFunction<?> getFunction() {
        return this.function;
    }

    /**
     * A method that returns the array of arguments used for the function
     *
     * @return the array of arguments used for the function
     */
    public String[] getRawArguments() {
        return this.rawArguments;
    }

    /**
     * A method that returns the name of the ability that contains this function
     *
     * @return the name of the ability that contains this function
     */
    public String getAbilityName() {
        return this.abilityName;
    }

    /**
     * A method that returns the trigger of the ability that contains this function
     *
     * @return the trigger of the ability that contains this function
     */
    public AbilityTrigger<?, ?> getAbilityTrigger() {
        return this.abilityTrigger;
    }

    /**
     * A method that returns the line number of the function in the keywords list of the ability
     *
     * @return the line number of the keyword in the function list of the ability
     */
    public int getLineNumber() {
        return this.lineNumber;
    }
}
