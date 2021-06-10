package me.boboballoon.innovativeitems.keywords.keyword;

import org.jetbrains.annotations.Nullable;

/**
 * A class that represents an argument for a keyword
 */
public class KeywordArgument {
    private final boolean omissible;
    private final String fallback;

    /**
     * Represents a normal no fallback argument
     */
    public static final KeywordArgument BLANK = new KeywordArgument();

    /**
     * A constructor used to build a keyword argument that is possible to omit
     * @param fallback the fallback return value when no argument is provided
     */
    public KeywordArgument(String fallback) {
        this.omissible = true;
        this.fallback = fallback;
    }

    /**
     * A constructor used to build a keyword argument that is not possible to omit
     */
    private KeywordArgument() {
        this.omissible = false;
        this.fallback = null;
    }

    /**
     * A method that returns a boolean that represents whether this argument can be omitted
     * @return a boolean that represents whether this argument can be omitted
     */
    public boolean isOmissible() {
        return this.omissible;
    }


    /**
     * A method that returns a fallback value for when no other value is present
     * @return a fallback value for when no other value is present
     */
    @Nullable
    public String getFallback() {
        return this.fallback;
    }
}
