package me.boboballoon.innovativeitems.functions.arguments;

import me.boboballoon.innovativeitems.functions.FunctionContext;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A class that represents an enum that can be parsed for a keywords arguments
 */
public class ExpectedEnum<T extends Enum<T>> implements ExpectedArguments {
    private final Class<T> clazz;
    private final Consumer<FunctionContext> onError;
    private final Predicate<T> condition;

    public ExpectedEnum(@NotNull Class<T> clazz, @NotNull Consumer<FunctionContext> onError, @Nullable Predicate<T> condition) {
        this.clazz = clazz;
        this.onError = onError;
        this.condition = condition != null ? condition : type -> true;
    }

    public ExpectedEnum(@NotNull Class<T> clazz, @Nullable Predicate<T> condition, @NotNull String fieldName) {
        this(clazz, context -> LogUtil.logFunctionError(context, fieldName), condition);
    }

    public ExpectedEnum(@NotNull Class<T> clazz, @NotNull String fieldName) {
        this(clazz, null, fieldName);
    }

    public ExpectedEnum(@NotNull Class<T> clazz, @Nullable Predicate<T> condition) {
        this(clazz, ExpectedArguments.DEFAULT_ERROR, condition);
    }

    public ExpectedEnum(@NotNull Class<T> clazz) {
        this(clazz, (Predicate<T>) null);
    }

    /**
     * A method used to get the class of the enum being parsed
     *
     * @return the class of the enum being parsed
     */
    public Class<T> getClazz() {
        return this.clazz;
    }

    /**
     * A method used to get the condition that must be true for an error not to be thrown
     *
     * @return the condition that must be true for an error not to be thrown
     */
    public Predicate<T> getCondition() {
        return this.condition;
    }

    @Override
    @Nullable
    public T getValue(@NotNull String rawValue, @NotNull FunctionContext context) {
        T value = Enum.valueOf(this.clazz, rawValue.toUpperCase());

        return this.condition.test(value) ? value : null;
    }

    @Override
    @NotNull
    public Consumer<FunctionContext> getOnError() {
        return this.onError;
    }
}
