package me.boboballoon.innovativeitems.functions.arguments;

import me.boboballoon.innovativeitems.functions.FunctionContext;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A class used to wrap the ExpectedManual interface to provided support for error messages
 */
public class ExpectedManual implements ExpectedArguments {
    private final ExpectedFunction manual;
    private final Consumer<FunctionContext> onError;

    public ExpectedManual(@NotNull ExpectedFunction manual, @NotNull Consumer<FunctionContext> onError) {
        this.manual = manual;
        this.onError = onError;
    }

    public ExpectedManual(@NotNull ExpectedFunction manual, @NotNull String fieldName) {
        this(manual, context -> LogUtil.logFunctionError(context, fieldName));
    }

    public ExpectedManual(@NotNull ExpectedFunction manual) {
        this(manual, ExpectedArguments.DEFAULT_ERROR);
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

        return value;
    }

    /**
     * Java does not define classes by shape, I just don't want argument inception in this class
     */
    public interface ExpectedFunction {
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
    }
}
