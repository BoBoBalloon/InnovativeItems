package me.boboballoon.innovativeitems.keywords.keyword;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class that represents a usable keyword in an ability config file
 */
public abstract class Keyword {
    private final String identifier;
    private final List<KeywordArgument> expectedArguments;

    /**
     * A constructor that builds a keyword
     * @param identifier the reference used to get a keyword and used in config files
     * @param expectedArguments the expected arguments provided in a config file
     */
    public Keyword(@NotNull String identifier, @NotNull KeywordArgument... expectedArguments) {
        this.identifier = identifier;
        this.expectedArguments = Arrays.asList(expectedArguments);
    }

    /**
     * A constructor that builds a keyword without any arguments
     * @param identifier the reference used to get a keyword and used in config files
     */
    public Keyword(@NotNull String identifier) {
        this.identifier = identifier;
        this.expectedArguments = Collections.emptyList();
    }

    /**
     * A method that returns the identifier that represents this keyword
     *
     * @return the identifier that represents this keyword
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * A method that returns the expected arguments provided in a config file
     *
     * @return the expected arguments provided in a config file (will return an empty list if no arguments are expected)
     */
    public List<KeywordArgument> getExpectedArguments() {
        return this.expectedArguments;
    }

    /**
     * A method that executes code that will be fired by the keyword
     *
     * @param context the context in which arguments are used
     */
    public abstract void execute(KeywordContext context);
}
