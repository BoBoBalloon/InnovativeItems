package me.boboballoon.innovativeitems.functions;

import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that is responsible for holding all functions in memory during runtime
 */
public final class FunctionManager {
    private final Map<String, Keyword> keywords;
    private final Map<String, Condition> conditions;

    public FunctionManager() {
        this.keywords = new HashMap<>();
        this.conditions = new HashMap<>();
        LogUtil.logUnblocked(LogUtil.Level.INFO, "Function manager initialized!");
        //unblocked because debug level is null
    }

    /**
     * A method used to register a new keyword in the cache
     *
     * @param keyword the keyword you wish to register
     */
    public void registerKeyword(@NotNull Keyword keyword) {
        String identifier = keyword.getIdentifier();

        if (!this.isValidIdentifier(identifier)) {
            //unblocked because debug level may be null at this point
            LogUtil.logUnblocked(LogUtil.Level.DEV, "Keyword with the identifier of " + identifier + " is not valid! Skipping...");
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

        if (!this.isValidIdentifier(identifier)) {
            //unblocked because debug level may be null at this point
            LogUtil.logUnblocked(LogUtil.Level.DEV, "Condition with the identifier of " + identifier + " is not valid! Skipping...");
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
     * A method used to check whether the cache contains a function with the provided identifier
     *
     * @param identifier the identifier of the function
     * @return a boolean that is true when said identifier is present
     */
    public boolean contains(String identifier) {
        return (this.keywords.containsKey(identifier) || this.conditions.containsKey(identifier));
    }

    /**
     * A method that checks whether a string is a valid identifier to place into the cache
     *
     * @param identifier the identifier you wish to check
     * @return a boolean that is true if the provided identifier is valid
     */
    public boolean isValidIdentifier(String identifier) {
        return (!this.contains(identifier) && identifier.matches("\\w+"));
    }
}
