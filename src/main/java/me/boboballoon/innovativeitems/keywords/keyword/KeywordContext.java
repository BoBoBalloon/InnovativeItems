package me.boboballoon.innovativeitems.keywords.keyword;

import me.boboballoon.innovativeitems.items.ability.AbilityTrigger;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;

/**
 * Represents the context in which a keyword was used in
 */
public class KeywordContext {
    private final Keyword keyword;
    private final String[] context;
    private final String abilityName;
    private final AbilityTrigger abilityTrigger;
    private final int lineNumber;

    public KeywordContext(Keyword keyword, String[] context, String abilityName, AbilityTrigger abilityTrigger, int lineNumber) {
        this.keyword = keyword;
        this.context = context;
        this.abilityName = abilityName;
        this.abilityTrigger = abilityTrigger;
        this.lineNumber = lineNumber;
    }

    /**
     * A method that returns the type of keyword being used as a base for the active keyword
     *
     * @return the type of keyword being used as a base for the active keyword
     */
    public Keyword getKeyword() {
        return this.keyword;
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
     * A method that returns the name of the ability that contains this keyword
     *
     * @return the name of the ability that contains this keyword
     */
    public String getAbilityName() {
        return this.abilityName;
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
     * A method that returns the line number of the keyword in the keywords list of the ability
     *
     * @return the line number of the keyword in the keywords list of the ability
     */
    public int getLineNumber() {
        return this.lineNumber;
    }
}
