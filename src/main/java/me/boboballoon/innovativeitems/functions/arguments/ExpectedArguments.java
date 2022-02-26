package me.boboballoon.innovativeitems.functions.arguments;

import me.boboballoon.innovativeitems.functions.FunctionContext;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A functional interface used to set the value of an argument in a keyword
 */
@FunctionalInterface
public interface ExpectedArguments {
    /**
     * A constant field that is used as the default error response to a mismatched data type or error while parsing
     */
    Consumer<FunctionContext> DEFAULT_ERROR = context -> {
        String argumentType = context.getFunction() instanceof Keyword ? "keyword" : context.getFunction() instanceof Condition ? "condition" : "unknown";
        LogUtil.log(LogUtil.Level.WARNING, "Line number " + context.getLineNumber() + " on " + argumentType + " " +  context.getFunction().getIdentifier() + " on ability " + context.getAbilityName() + " was unable to be parsed... Are you sure you provided the correct data type?");
    };

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
    @NotNull
    default Consumer<FunctionContext> getOnError() {
        return DEFAULT_ERROR;
    }
}
