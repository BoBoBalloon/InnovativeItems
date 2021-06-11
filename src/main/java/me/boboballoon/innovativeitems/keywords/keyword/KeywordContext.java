package me.boboballoon.innovativeitems.keywords.keyword;

/**
 * Represents the context in which a keyword was used in
 */
public class KeywordContext {
    private final String[] context;
    private final String abilityName;

    public KeywordContext(String[] context, String abilityName) {
        this.context = context;
        this.abilityName = abilityName;
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
}
