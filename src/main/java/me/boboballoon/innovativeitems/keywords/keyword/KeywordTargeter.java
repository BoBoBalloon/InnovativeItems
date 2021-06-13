package me.boboballoon.innovativeitems.keywords.keyword;

import org.jetbrains.annotations.Nullable;

/**
 * A class that contains all possible targeters used in a keyword
 */
public enum KeywordTargeter {
    PLAYER("?player"),
    ENTITY("?entity"),
    BLOCK("?block");

    private final String identifier;

    KeywordTargeter(String identifier) {
        this.identifier = identifier;
    }

    /**
     * A method that returns the identifier that is used to parse the keyword targeter
     *
     * @return the identifier that is used to parse the keyword targeter
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * A method used to identify a keyword targeter via its identifier
     *
     * @param identifier the targeters identifier
     * @return the corresponding keyword targeter (null if nothing matches)
     */
    @Nullable
    public static KeywordTargeter getFromIdentifier(String identifier) {
        for (KeywordTargeter targeter : KeywordTargeter.values()) {
            if (identifier.equalsIgnoreCase(targeter.getIdentifier())) {
                return targeter;
            }
        }

        return null;
    }
}
