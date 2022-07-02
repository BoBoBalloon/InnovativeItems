package me.boboballoon.innovativeitems.items.ability.trigger;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * A class that represents an ability trigger
 *
 * @param <T> the type of event this trigger listens for
 * @param <S> the type of runtime context that this event wraps
 */
public abstract class AbilityTrigger<T extends Event, S extends RuntimeContext> implements Listener {
    private final String identifier;
    private final String regex;
    private final Class<T> eventClass;
    private final Class<S> contextClass;
    private final InventoryIterator<T> iterator;
    private final Predicate<T> predicate;
    private final ImmutableSet<FunctionTargeter> targeters;

    private AbilityTrigger(@NotNull String identifier, @Nullable String regex, @NotNull Class<T> eventClass, @NotNull Class<S> contextClass, @NotNull InventoryIterator<T> iterator, @Nullable Predicate<T> predicate, @NotNull ImmutableSet<FunctionTargeter> targeters) {
        this.identifier = identifier;
        this.regex = regex != null ? regex : identifier;
        this.eventClass = eventClass;
        this.contextClass = contextClass;
        this.iterator = iterator;
        this.predicate = predicate != null ? predicate : event -> true;
        this.targeters = targeters;
    }

    public AbilityTrigger(@NotNull String identifier, @Nullable String regex, @NotNull Class<T> eventClass, @NotNull Class<S> contextClass, @NotNull InventoryIterator<T> iterator, @Nullable Predicate<T> predicate, @NotNull Collection<FunctionTargeter> targeters) {
        this(identifier, regex, eventClass, contextClass, iterator, predicate, toSet(targeters));
    }

    public AbilityTrigger(@NotNull String identifier, @Nullable String regex, @NotNull Class<T> eventClass, @NotNull Class<S> contextClass, @NotNull InventoryIterator<T> iterator, @Nullable Predicate<T> predicate, @NotNull FunctionTargeter... targeters) {
        this(identifier, regex, eventClass, contextClass, iterator, predicate, Sets.newHashSet(targeters));
    }

    /**
     * A method that returns the identifier that is used to identify and sometimes parse the ability trigger
     *
     * @return the identifier that is used to identify and sometimes parse the ability trigger
     */
    @NotNull
    public final String getIdentifier() {
        return this.identifier;
    }

    /**
     * A method that returns the specific regex that is used to parse the ability trigger
     *
     * @return the specific regex that is used to parse the ability trigger
     */
    @NotNull
    public final String getRegex() {
        return this.regex;
    }

    /**
     * A method that returns the class of the bukkit event this ability trigger will wrap
     *
     * @return the class of the bukkit event this ability trigger will wrap
     */
    @NotNull
    public final Class<T> getEventClass() {
        return this.eventClass;
    }

    /**
     * A method that returns the class of the runtime context that is expected to be used for each ability trigger
     *
     * @return the class of the runtime context that is expected to be used for each ability trigger
     */
    @NotNull
    public final Class<S> getContextClass() {
        return this.contextClass;
    }

    /**
     * A method that returns the functional interface used to return an instance of an iterable object from a player's inventory
     *
     * @return the functional interface used to return an instance of an iterable object from a player's inventory
     */
    @NotNull
    public final InventoryIterator<T> getIterator() {
        return this.iterator;
    }

    /**
     * A method that returns a predicate that must be true for the trigger to fire before any items are iterated over
     *
     * @return a predicate that must be true for the trigger to fire before any items are iterated over
     */
    @NotNull
    public final Predicate<T> getPredicate() {
        return this.predicate;
    }

    /**
     * A method that returns the set of targeters that can be used alongside this ability trigger
     *
     * @return the set of targeters that can be used alongside this ability trigger
     */
    @NotNull
    public final ImmutableSet<FunctionTargeter> getTargeters() {
        return this.targeters;
    }

    /**
     * A method used to get an instance of the player object from the provided event
     *
     * @param event the provided event
     * @return an instance of the player object from the provided event
     */
    @NotNull
    public abstract Player fromEvent(@NotNull T event);

    /**
     * A method that is fired once for every custom item that has this trigger on the provided event
     *
     * @param event the event that was called
     * @param item the custom item that was involved in this event
     * @param ability the ability on the custom item that MUST BE PASSED INTO {@link S}
     * @return a runtime context that will be used to fire the wrapped ability async (null if it should be cancelled)
     */
    @Nullable
    public abstract S trigger(@NotNull T event, @NotNull CustomItem item, @NotNull Ability ability);

    /**
     * A method that is fired ONCE right after an ability is first initialized, it will be fired once per ability that uses this trigger
     *
     * @param ability the ability that has this trigger
     */
    public void init(@NotNull Ability ability) {}

    /**
     * A method used to convert a collection of targeters to an immutable set
     *
     * @param targeters a collection of targeters
     * @return an immutable set
     */
    @NotNull
    private static ImmutableSet<FunctionTargeter> toSet(@NotNull Collection<FunctionTargeter> targeters) {
        if (!targeters.contains(FunctionTargeter.PLAYER)) {
            targeters.add(FunctionTargeter.PLAYER);
        }

        return ImmutableSet.copyOf(targeters);
    }

    /**
     * A method used to check if the current ability trigger is compatible with the specified ability trigger
     * This will only return true if this trigger can use at least every targeter of the provided trigger ()
     *
     * @param origin the ability trigger you are starting from
     * @param target the ability trigger you wish to execute next
     * @return a boolean that is true when the origin ability trigger is compatible with the target ability trigger
     */
    public static boolean isCompatible(@NotNull AbilityTrigger<?, ?> origin, @NotNull AbilityTrigger<?, ?> target) {
        for (FunctionTargeter targeter : target.getTargeters()) {
            if (!origin.getTargeters().contains(targeter)) {
                return false;
            }
        }

        return true;
    }
}
