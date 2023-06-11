package me.boboballoon.innovativeitems.functions;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedVarArg;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.ManuallyRegister;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A class that is responsible for holding all functions in memory during runtime
 */
public final class FunctionManager {
    private final Map<String, Keyword> keywords;
    private final Map<String, Condition> conditions;
    private final Map<String, AbilityTrigger<?, ?>> triggers;

    public FunctionManager() {
        this.keywords = new LinkedHashMap<>(); //used to preserve order
        this.conditions = new LinkedHashMap<>();
        this.triggers = new LinkedHashMap<>();

        //unblocked because debug level is null
        LogUtil.logUnblocked(LogUtil.Level.INFO, "Function manager initialized!");
    }

    /**
     * A method used to register a new keyword in the cache
     *
     * @param keyword the keyword you wish to register
     */
    public void registerKeyword(@NotNull Keyword keyword) {
        String identifier = keyword.getIdentifier();

        if (this.isInvalidIdentifier(identifier)) {
            //unblocked because debug level may be null at this point
            LogUtil.logUnblocked(LogUtil.Level.DEV, "Keyword with the identifier of " + identifier + " is not valid! Skipping...");
            return;
        }

        if (!this.hasValidArguments(keyword)) {
            //unblocked because debug level may be null at this point
            LogUtil.logUnblocked(LogUtil.Level.DEV, "Keyword with the identifier of " + identifier + " is not valid due to incorrect usage of var-args! Skipping...");
            return;
        }

        this.keywords.put(identifier, keyword);
    }

    /**
     * A method used to register a new keyword in the cache
     *
     * @param depend the name of the plugin this keyword depends on
     * @param keyword the keyword you wish to register
     */
    public void registerKeyword(@NotNull String depend, @NotNull Keyword keyword) {
        if (Bukkit.getPluginManager().getPlugin(depend) == null) {
            //silently fail
            return;
        }

        this.registerKeyword(keyword);
    }

    /**
     * A method used to register a new condition in the cache
     *
     * @param condition the condition you wish to register
     */
    public void registerCondition(@NotNull Condition condition) {
        String identifier = condition.getIdentifier();

        if (this.isInvalidIdentifier(identifier)) {
            //unblocked because debug level may be null at this point
            LogUtil.logUnblocked(LogUtil.Level.DEV, "Condition with the identifier of " + identifier + " is not valid! Skipping...");
            return;
        }

        if (!this.hasValidArguments(condition)) {
            //unblocked because debug level may be null at this point
            LogUtil.logUnblocked(LogUtil.Level.DEV, "Condition with the identifier of " + identifier + " is not valid due to incorrect usage of var-args! Skipping...");
            return;
        }

        this.conditions.put(identifier, condition);
    }

    /**
     * A method used to register a new keyword in the cache
     *
     * @param depend the name of the plugin this keyword depends on
     * @param condition the condition you wish to register
     */
    public void registerCondition(@NotNull String depend, @NotNull Condition condition) {
        if (Bukkit.getPluginManager().getPlugin(depend) == null) {
            //silently fail
            return;
        }

        this.registerCondition(condition);
    }

    /**
     * A method used to register new keywords in the cache
     *
     * @param keywords all the keywords you wish to register
     */
    public void registerKeywords(@NotNull Keyword... keywords) {
        for (Keyword keyword : keywords) {
            this.registerKeyword(keyword);
        }
    }

    /**
     * A method used to register new keywords in the cache that depend on a specified plugin
     *
     * @param depend the name of the plugin these keywords depend on
     * @param keywords all the keywords you wish to register
     */
    public void registerKeywords(@NotNull String depend, @NotNull Keyword... keywords) {
        if (Bukkit.getPluginManager().getPlugin(depend) == null) {
            //silently fail
            return;
        }

        this.registerKeywords(keywords);
    }

    /**
     * A method used to register new conditions in the cache
     *
     * @param conditions all the conditions you wish to register
     */
    public void registerConditions(@NotNull Condition... conditions) {
        for (Condition condition : conditions) {
            this.registerCondition(condition);
        }
    }

    /**
     * A method used to register new conditions in the cache that depend on a specified plugin
     *
     * @param depend the name of the plugin these conditions depend on
     * @param conditions all the conditions you wish to register
     */
    public void registerConditions(@NotNull String depend, @NotNull Condition... conditions) {
        if (Bukkit.getPluginManager().getPlugin(depend) == null) {
            //silently fail
            return;
        }

        this.registerConditions(conditions);
    }

    /**
     * A method used to register a new trigger in the cache
     *
     * @param trigger the trigger you wish to register
     */
    public void registerTrigger(@NotNull AbilityTrigger<?, ?> trigger) {
        if (this.contains(trigger.getIdentifier()) || !trigger.getIdentifier().matches("[\\w-]+")) {
            return;
        }

        this.triggers.put(trigger.getIdentifier(), trigger);

        if (InnovativeItems.getInstance().isEnabled()) {
            FunctionManager.registerTriggerEvent(trigger);
        }
    }

    /**
     * A method used to register a new trigger in the cache
     *
     * @param depend the name of the plugin this ability trigger depends on
     * @param trigger the trigger you wish to register
     */
    public void registerTrigger(@NotNull String depend, @NotNull AbilityTrigger<?, ?> trigger) {
        if (Bukkit.getPluginManager().getPlugin(depend) == null) {
            //silently fail
            return;
        }

        this.registerTrigger(trigger);
    }

    /**
     * A method used to register new triggers in the cache
     *
     * @param triggers the triggers you wish to register
     */
    public void registerTriggers(@NotNull AbilityTrigger<?, ?>... triggers) {
        for (AbilityTrigger<?, ?> trigger : triggers) {
            this.registerTrigger(trigger);
        }
    }

    /**
     * A method used to register new triggers in the cache
     *
     * @param depend the name of the plugin these ability triggers depend on
     * @param triggers the triggers you wish to register
     */
    public void registerTrigger(@NotNull String depend, @NotNull AbilityTrigger<?, ?>... triggers) {
        if (Bukkit.getPluginManager().getPlugin(depend) == null) {
            //silently fail
            return;
        }

        this.registerTriggers(triggers);
    }

    /**
     * A method used to get a keyword already registered in the cache
     *
     * @param identifier the identifier of the keyword
     * @return the keyword
     */
    @Nullable
    public Keyword getKeyword(@NotNull String identifier) {
        return this.keywords.get(identifier);
    }

    /**
     * A method that returns all of the registered keywords
     *
     * @return all of the registered keywords
     */
    @NotNull
    public ImmutableList<Keyword> getKeywords() {
        return ImmutableList.copyOf(this.keywords.values());
    }

    /**
     * A method used to get a condition already registered in the cache
     *
     * @param identifier the identifier of the condition
     * @return the condition
     */
    @Nullable
    public Condition getCondition(@NotNull String identifier) {
        return this.conditions.get(identifier);
    }

    /**
     * A method that returns all of the registered conditions
     *
     * @return all of the registered conditions
     */
    @NotNull
    public ImmutableList<Condition> getConditions() {
        return ImmutableList.copyOf(this.conditions.values());
    }

    /**
     * A method used to get an ability trigger first via its regular expression, and if it does not exist try via identifier
     *
     * @param identifier the triggers identifier
     * @return an ability trigger
     */
    @Nullable
    public AbilityTrigger<?, ?> getAbilityTrigger(@NotNull String identifier) {
        //via regex
        for (AbilityTrigger<?, ?> trigger : this.triggers.values()) {
            if (trigger.getIdentifier().equals(trigger.getRegex())) { //if regex is null it is set to the identifier so it acts as a literal regex
                continue;
            }

            if (identifier.matches(trigger.getRegex())) {
                return trigger;
            }
        }

        return this.triggers.get(identifier);
    }

    /**
     * A method that returns all of the registered ability triggers
     *
     * @return all of the registered ability triggers
     */
    @NotNull
    public ImmutableList<AbilityTrigger<?, ?>> getAbilityTriggers() {
        return ImmutableList.copyOf(this.triggers.values());
    }

    /**
     * A method used to check whether the cache contains a function with the provided identifier
     *
     * @param identifier the identifier of the function
     * @return a boolean that is true when said identifier is present
     */
    public boolean contains(@NotNull String identifier) {
        return this.keywords.containsKey(identifier) || this.conditions.containsKey(identifier) || this.triggers.containsKey(identifier);
    }

    /**
     * A method that checks whether a string is an invalid identifier to place into the cache
     *
     * @param identifier the identifier you wish to check
     * @return a boolean that is true if the provided identifier is invalid
     */
    public boolean isInvalidIdentifier(@NotNull String identifier) {
        return this.contains(identifier) || !identifier.matches("\\w+");
    }

    /**
     * A method for internal use only that will reregister all event listeners for ability triggers
     */
    public void registerCachedTriggers() {
        for (AbilityTrigger<?, ?> trigger : this.triggers.values()) {
            FunctionManager.registerTriggerEvent(trigger);
        }
    }

    /**
     * A method that checks if a function has var-args in places other than the last element
     *
     * @param function the function to check
     * @return true if the function is valid
     */
    private boolean hasValidArguments(@NotNull InnovativeFunction<?> function) {
        for (int i = 0; i < function.getArguments().size(); i++) {
            if (function.getArguments().get(i) instanceof ExpectedVarArg && i != function.getArguments().size() - 1) {
                return false;
            }
        }

        return true;
    }

    /**
     * A util method used to register the underlying bukkit event inside of an ability trigger (must not be on the onLoad method)
     *
     * @param trigger the ability trigger to register
     */
    private static <T extends Event> void registerTriggerEvent(@NotNull AbilityTrigger<T, ?> trigger) {
        if (trigger.getClass().isAnnotationPresent(ManuallyRegister.class)) {
            return;
        }

        Bukkit.getPluginManager().registerEvent(trigger.getEventClass(), trigger, EventPriority.HIGHEST, ((listener, instance) -> {
            if (!trigger.getEventClass().isInstance(instance)) {
                return;
            }

            T event = (T) instance;

            if (!trigger.getPredicate().test(event)) {
                LogUtil.log(LogUtil.Level.NOISE, "The event " + event.getEventName() + " failed the trigger predicate on the " + trigger.getIdentifier() + " ability trigger!");
                return;
            }

            Player player = trigger.fromEvent(event);

            for (CustomItem item : trigger.getIterator().getItems(event, player)) {
                if (item == null) {
                    continue;
                }

                Ability ability = item.getAbility();

                if (ability == null || ability.getTrigger() != trigger) {
                    continue;
                }

                RuntimeContext context = trigger.trigger(event, item, ability);

                if (context != null) {
                    Bukkit.getScheduler().runTaskAsynchronously(InnovativeItems.getInstance(), () -> ability.execute(context));
                }
            }
        }), InnovativeItems.getInstance());
    }
}
