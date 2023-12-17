package me.boboballoon.innovativeitems.functions.arguments;

import me.boboballoon.innovativeitems.functions.FunctionContext;
import me.boboballoon.innovativeitems.util.InitializationUtil;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.TextUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A class that represents a "primitive" type that can be parsed for a functions arguments
 */
public class ExpectedPrimitive implements ExpectedArguments {
    private final PrimitiveType primitive;
    private final Consumer<FunctionContext> onError;
    private final Predicate<Object> condition;

    public ExpectedPrimitive(@NotNull PrimitiveType primitive, @Nullable Consumer<FunctionContext> onError, @Nullable Predicate<Object> condition) {
        this.primitive = primitive;
        this.onError = onError != null ? onError : ExpectedArguments.DEFAULT_ERROR;
        this.condition = condition != null ? condition : value -> true;
    }

    public ExpectedPrimitive(@NotNull PrimitiveType primitive, @Nullable Consumer<FunctionContext> onError) {
        this(primitive, onError, null);
    }

    public ExpectedPrimitive(@NotNull PrimitiveType primitive, @Nullable Predicate<Object> condition) {
        this(primitive, (Consumer<FunctionContext>) null, condition);
    }

    public ExpectedPrimitive(@NotNull PrimitiveType primitive, @NotNull String fieldName, @Nullable Predicate<Object> condition) {
        this(primitive, context -> LogUtil.logFunctionError(context, fieldName), condition);
    }

    public ExpectedPrimitive(@NotNull PrimitiveType primitive, @NotNull String fieldName) {
        this(primitive, fieldName, null);
    }

    public ExpectedPrimitive(@NotNull PrimitiveType primitive) {
        this(primitive, (Consumer<FunctionContext>) null, null);
    }

    /**
     * A method that returns the expected value to parse
     *
     * @return the expected value to parse
     */
    public PrimitiveType getPrimitive() {
        return this.primitive;
    }

    /**
     * A method that returns the method to be called on if the parsing fails for any reason
     *
     * @return the method to be called on if the parsing fails for any reason
     */
    @NotNull
    @Override
    public Consumer<FunctionContext> getOnError() {
        return this.onError;
    }

    /**
     * A method that returns a predicate that will be used to test the final parsed object
     *
     * @return a predicate that will be used to test the final parsed object
     */
    @Nullable
    public Predicate<Object> getCondition() {
        return this.condition;
    }

    /**
     * A method used to parse a given value based on the primitive type selected on this class
     *
     * @param rawValue the raw value
     * @param context the context in which the argument is being parsed
     * @return the parsed value
     */
    @Nullable
    @Override
    public Object getValue(@NotNull String rawValue, @NotNull FunctionContext context) {
        Object value;

        if (this.primitive == PrimitiveType.STRING) {
            value = TextUtil.format(rawValue);
        } else if (this.primitive == PrimitiveType.CHAR) {
            value = InitializationUtil.initChar(rawValue);
        } else if (this.primitive == PrimitiveType.BOOLEAN) {
            value = InitializationUtil.initBoolean(rawValue);
        } else { //since the enum is a constant, anything else must be a number
            value = InitializationUtil.initNumber(rawValue, (Class<? extends Number>) this.primitive.getRepresentingClass());
        }

        return this.condition.test(value) ? value : null;
    }

    @Override
    public boolean validate(@NotNull String rawValue) {
        return this.primitive.validate(rawValue);
    }

    /**
     * A class used to list all possible primitive types to be parsed
     */
    public enum PrimitiveType {
        BYTE(Byte.class, rawValue -> {
            Byte.parseByte(rawValue); //if it fails it is caught by try-catch block
            return true;
        }), //all numerical types must be the wrappers due to reflection usage on the InitializationUtil.initNumber() method
        SHORT(Short.class, rawValue -> {
            Short.parseShort(rawValue); //if it fails it is caught by try-catch block
            return true;
        }),
        INTEGER(Integer.class, rawValue -> {
            Integer.parseInt(rawValue); //if it fails it is caught by try-catch block
            return true;
        }),
        LONG(Long.class, rawValue -> {
            Long.parseLong(rawValue); //if it fails it is caught by try-catch block
            return true;
        }),
        FLOAT(Float.class, rawValue -> {
            Float.parseFloat(rawValue); //if it fails it is caught by try-catch block
            return true;
        }),
        DOUBLE(Double.class, rawValue -> {
            Double.parseDouble(rawValue); //if it fails it is caught by try-catch block
            return true;
        }),
        BOOLEAN(boolean.class, rawValue -> rawValue.equalsIgnoreCase("true") || rawValue.equalsIgnoreCase("false")),
        CHAR(char.class, rawValue -> rawValue.length() == 1),
        STRING(String.class, null);

        private final Class<?> clazz;
        private final Predicate<String> verify;

        PrimitiveType(@NotNull Class<?> clazz, @Nullable Predicate<String> verify) {
            this.clazz = clazz;
            this.verify = verify;
        }

        /**
         * A method used to return what class each type represents
         *
         * @return what class each type represents
         */
        @NotNull
        public Class<?> getRepresentingClass() {
            return this.clazz;
        }

        /**
         * A method to check if the provided rawValue can be parsed as the type
         *
         * @param rawValue rawValue
         * @return if the provided rawValue can be parsed as the type
         */
        public boolean validate(@NotNull String rawValue) {
            if (this.verify == null) {
                return true;
            }

            try {
                return this.verify.test(rawValue);
            } catch (Exception e) {
                return false;
            }
        }
    }
}
