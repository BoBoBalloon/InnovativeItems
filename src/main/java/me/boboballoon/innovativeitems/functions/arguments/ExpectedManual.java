package me.boboballoon.innovativeitems.functions.arguments;

import me.boboballoon.innovativeitems.functions.FunctionContext;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * A class used to wrap the ExpectedManual interface to provided support for error messages
 */
public class ExpectedManual implements ExpectedArguments {
    private final ExpectedArguments manual;
    private final Consumer<FunctionContext> onError;

    public ExpectedManual(@NotNull ExpectedArguments manual, @NotNull Consumer<FunctionContext> onError) {
        this.manual = manual;
        this.onError = onError;
    }

    public ExpectedManual(@NotNull ExpectedArguments manual, @NotNull String fieldName) {
        this(manual, context -> LogUtil.logFunctionError(context, fieldName));
    }

    /**
     * A method that returns the method used to get the desired object
     *
     * @return the method used to get the desired object
     */
    public ExpectedArguments getManual() {
        return this.manual;
    }

    /**
     * A method that returns the method to be called on if the parsing fails for any reason
     *
     * @return the method to be called on if the parsing fails for any reason
     */
    @Override
    @NotNull
    public Consumer<FunctionContext> getOnError() {
        return this.onError;
    }

    /**
     * A method used to get the object called by the current manual method stored in this object
     *
     * @param rawValue the raw value of the number
     * @param context the context in which the argument is being parsed
     * @return the object called by the current manual method stored in this object
     */
    @Override
    public Object getValue(@NotNull String rawValue, @NotNull FunctionContext context) {
        Object value;
        try {
            value = this.manual.getValue(rawValue, context);
        } catch (Exception e) {
            return null;
        }

        if (value == null) {
            return null;
        }

        return value;
    }
}
