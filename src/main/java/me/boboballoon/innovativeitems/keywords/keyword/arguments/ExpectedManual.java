package me.boboballoon.innovativeitems.keywords.keyword.arguments;

import me.boboballoon.innovativeitems.keywords.keyword.KeywordContext;
import org.jetbrains.annotations.Nullable;

/**
 * A functional interface used to manually set the value of an argument in a keyword
 */
@FunctionalInterface
public interface ExpectedManual extends ExpectedArguments {
    /**
     * A method used to manually set the value of an argument in a keyword
     *
     * @param rawValue the raw value of the argument in the configuration file
     * @param context the context in which the keyword was parsed
     * @return the desired argument to be placed in the list
     * @throws Exception when parsing fails for any reason
     */
    @Nullable
    Object getValue(String rawValue, KeywordContext context) throws Exception;
}
