package me.boboballoon.innovativeitems.functions.arguments;

import me.boboballoon.innovativeitems.functions.FunctionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A functional interface used to set the value of an argument in a keyword
 */
@FunctionalInterface
public interface ExpectedArguments {
    /**
     * A method used to manually set the value of an argument in a keyword
     *
     * @param rawValue the raw value of the argument in the configuration file
     * @param context the context in which the keyword was parsed
     * @return the desired argument to be placed in the list, null if an error should be thrown
     * @throws Exception when parsing fails for any reason
     */
    @Nullable
    Object getValue(@NotNull String rawValue, @NotNull FunctionContext context) throws Exception;

    /**
     * A method that returns the method to be called on if the parsing fails for any reason
     *
     * @return the method to be called on if the parsing fails for any reason
     */
    @Nullable
    default Consumer<FunctionContext> getOnError() {
        return null;
    }
}
