package me.boboballoon.innovativeitems.functions;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedArguments;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Class that represents a usable and callable function in an ability config file
 */
public abstract class InnovativeFunction<T> {
    private final String identifier;
    private final ImmutableList<ExpectedArguments> arguments;

    /**
     * A constructor that builds a function
     * @param identifier the reference used to get a function and used in config files
     * @param arguments all the possible targeters in a given argument
     */
    public InnovativeFunction(@NotNull String identifier, @NotNull ExpectedArguments... arguments) {
        this.identifier = identifier;
        this.arguments = ImmutableList.copyOf(arguments);
    }

    /**
     * A method that returns the identifier that represents this function
     *
     * @return the identifier that represents this keyword
     */
    public final String getIdentifier() {
        return this.identifier;
    }

    /**
     * A method that returns each expected argument
     *
     * @return whether each argument is expected to be a targeter
     */
    public final ImmutableList<ExpectedArguments> getArguments() {
        return this.arguments;
    }

    /**
     * A method that executes code that will be fired by the keyword
     *
     * @param arguments the arguments that are used to execute the keyword (empty if no arguments are needed)
     * @param context context that can assist execution that cannot be cached and must be parsed during runtime separately
     */
    protected abstract T call(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context);

    /**
     * A method that returns a boolean that is true when the function will be run async
     *
     * @return a boolean that is true when the function will be run async
     */
    public abstract boolean isAsync();

    /**
     * A method that executes code that will be fired by the keyword (FIRE ASYNC)
     *
     * @param arguments the arguments that are used to execute the keyword (empty if no arguments are needed)
     * @param context context that can assist execution that cannot be cached and must be parsed during runtime separately
     */
    @Nullable
    public final T execute(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) throws ExecutionException {
        if (Bukkit.getServer().isPrimaryThread()) {
            throw new IllegalStateException("The function execute method cannot be called from the main thread!");
        }

        if (this.isAsync()) {
            return this.call(arguments, context);
        }

        Future<T> future = Bukkit.getScheduler().callSyncMethod(InnovativeItems.getInstance(), () -> {
            T value = this.call(arguments, context);
            this.unpause();
            return value;
        });

        while (!future.isDone()) {
            this.pause(); //fires before unpause due to time it takes to go through bukkit scheduler
        }

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ExecutionException("The value of " + this.getIdentifier() + " was unable to be initialized in time.", e);
        }
    }

    /**
     * A util method to unpause the current thread
     */
    private synchronized void unpause() {
        this.notify();
    }

    /**
     * A util method to pause the current thread
     */
    private synchronized void pause() {
        try {
            this.wait(10000); //timeout of 10 seconds (time provided in milliseconds)
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
