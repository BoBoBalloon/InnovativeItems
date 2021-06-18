package me.boboballoon.innovativeitems.keywords.keyword;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Class that represents a usable keyword in an ability config file
 */
public abstract class Keyword {
    private final String identifier;
    private final List<Boolean> isTargeter;

    /**
     * A constructor that builds a keyword
     * @param identifier the reference used to get a keyword and used in config files
     * @param isTargeters whether the index of the boolean should have a targeter present
     */
    public Keyword(@NotNull String identifier, Boolean... isTargeters) {
        this.identifier = identifier;
        this.isTargeter = Arrays.asList(isTargeters);
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
    public ImmutableList<Boolean> getArguments() {
        return ImmutableList.copyOf(this.isTargeter);
    }

    /**
     * A method that should be used to parse and initialize arguments
     *
     * @param context the context in which the keyword was used in
     * @return the parsed arguments (null if an error occurred)
     */
    @Nullable
    public abstract List<Object> load(KeywordContext context);

    /**
     * A method that executes code that will be fired by the keyword
     *
     * @param arguments the arguments that are used to execute the keyword (empty if no arguments are needed)
     * @param context context that can assist execution that cannot be cached and must be parsed during runtime separately
     */
    protected abstract void call(List<Object> arguments, RuntimeContext context);

    /**
     * A method that returns the identifier of each keyword targeter allowed
     *
     * @return the identifier of each keyword targeter allowed
     */
    public abstract ImmutableList<String> getValidTargeters();

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
    public void execute(List<Object> arguments, RuntimeContext context) {
        if (this.isAsync()) {
            this.call(arguments, context);
            return;
        }

        Bukkit.getScheduler().runTask(InnovativeItems.getInstance(), () -> {
            this.call(arguments, context);
            this.unpause();
        });

        this.pause();
    }

    /**
     * A util method to pause the current thread
     */
    private synchronized void unpause() {
        this.notify();
    }

    /**
     * A util method to unpause the current thread
     */
    private synchronized void pause() {
        try {
            this.wait();
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
