package me.boboballoon.innovativeitems.keywords.keyword;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedArguments;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a usable keyword in an ability config file
 */
public abstract class Keyword {
    private final String identifier;
    private final ImmutableList<ExpectedArguments> arguments;

    /**
     * A constructor that builds a keyword
     * @param identifier the reference used to get a keyword and used in config files
     * @param arguments all the possible targeters in a given argument
     */
    public Keyword(@NotNull String identifier, ExpectedArguments... arguments) {
        this.identifier = identifier;
        this.arguments = ImmutableList.copyOf(arguments);
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
     * A method that returns whether each argument is expected to be a targeter
     *
     * @return whether each argument is expected to be a targeter
     */
    public ImmutableList<ExpectedArguments> getArguments() {
        return this.arguments;
    }

    /**
     * A method that executes code that will be fired by the keyword
     *
     * @param arguments the arguments that are used to execute the keyword (empty if no arguments are needed)
     * @param context context that can assist execution that cannot be cached and must be parsed during runtime separately
     */
    protected abstract void call(ImmutableList<Object> arguments, RuntimeContext context);

    /**
     * A method that returns a boolean that is true when the keyword will be run async
     *
     * @return a boolean that is true when the keyword will be run async
     */
    public abstract boolean isAsync();

    /**
     * A method that executes code that will be fired by the keyword (FIRE ASYNC)
     *
     * @param arguments the arguments that are used to execute the keyword (empty if no arguments are needed)
     * @param context context that can assist execution that cannot be cached and must be parsed during runtime separately
     */
    public void execute(ImmutableList<Object> arguments, RuntimeContext context) {
        if (this.isAsync()) {
            this.call(arguments, context);
            return;
        }

        Bukkit.getScheduler().runTask(InnovativeItems.getInstance(), () -> {
            this.call(arguments, context);
            this.unpause(); //fires after pause
        });

        this.pause(); //fires before unpause due to time it takes to go through bukkit scheduler
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
