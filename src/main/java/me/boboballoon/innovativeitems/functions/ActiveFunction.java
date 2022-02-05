package me.boboballoon.innovativeitems.functions;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;

/**
 * A class that represents a function after being parsed
 */
public abstract class ActiveFunction<T> {
    private final InnovativeFunction<T> base;
    private final ImmutableList<Object> arguments;

    /**
     * A constructor used to build a function after being parsed
     *
     * @param base the base function being used
     * @param arguments the list of arguments with targeters already parsed
     */
    public ActiveFunction(@NotNull InnovativeFunction<T> base, @NotNull ImmutableList<Object> arguments) {
        this.base = base;
        this.arguments = arguments;
    }

    /**
     * A method that returns the base function being used
     *
     * @return the base function being used
     */
    public InnovativeFunction<T> getBase() {
        return this.base;
    }

    /**
     * A method that returns all the arguments being used to be passed into the function
     *
     * @return all the arguments being used to be passed into the function
     */
    public final ImmutableList<Object> getArguments() {
        return this.arguments;
    }

    /**
     * A method that executes the base function given the provided context (will always be fired async)
     *
     * @param context context that can assist execution that cannot be cached and must be parsed during runtime separately
     */
    @Nullable
    public final T execute(@NotNull RuntimeContext context) {
        try {
            return this.base.execute(this.arguments, context);
        } catch (ExecutionException e) {
            LogUtil.log(LogUtil.Level.SEVERE, "There was an error trying to execute the " + this.base.getIdentifier() + " function!");
            if (InnovativeItems.getInstance().getConfigManager().getDebugLevel() >= LogUtil.Level.DEV.getDebugLevel()) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
