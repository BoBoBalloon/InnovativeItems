package me.boboballoon.innovativeitems.keywords.keyword;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Class that represents a usable keyword in an ability config file
 */
public abstract class Keyword {
    private final String identifier;
    private final int argumentsLength;

    /**
     * A constructor that builds a keyword
     * @param identifier the reference used to get a keyword and used in config files
     * @param argumentsLength the amount of provided arguments
     */
    public Keyword(@NotNull String identifier, int argumentsLength) {
        this.identifier = identifier;
        this.argumentsLength = argumentsLength;
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
     * A method that returns the amount of arguments required to execute this keyword
     *
     * @return the amount of arguments required to execute this keyword
     */
    public int getArgumentsLength() {
        return this.argumentsLength;
    }

    /**
     * A method that executes code that will be fired by the keyword
     *
     * @param arguments the arguments that are used to execute the keyword (empty if no arguments are needed)
     */
    public abstract void execute(List<Object> arguments);

    /**
     * A method that should be used to parse and initialize arguments
     *
     * @param context the context in which the keyword was used in
     */
    public abstract List<Object> load(KeywordContext context);
}
