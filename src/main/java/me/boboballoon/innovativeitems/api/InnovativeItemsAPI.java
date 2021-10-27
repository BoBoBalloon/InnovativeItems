package me.boboballoon.innovativeitems.api;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.config.ConfigManager;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.items.GarbageCollector;
import me.boboballoon.innovativeitems.functions.FunctionManager;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A class used to easily retrieve internal information about the plugin
 */
public final class InnovativeItemsAPI {
    /**
     * Constructor to prevent people from using this util class in an object oriented way
     */
    private InnovativeItemsAPI() {}

    /**
     * A method used to return the active instance of the keyword manager
     *
     * @return the active instance of the keyword manager
     */
    public static FunctionManager getFunctionManager() {
        return InnovativeItems.getInstance().getFunctionManager();
    }

    /**
     * A method used to return the active instance of the config manager
     * (Should not be used until the plugin is enabled, will return null if used when the plugin is loaded)
     *
     * @return the active instance of the config manager
     */
    public static ConfigManager getConfigManager() {
        return InnovativeItems.getInstance().getConfigManager();
    }

    /**
     * A method used to return the active instance of the garbage collector
     * (Should not be used until the plugin is enabled, will return null if used when the plugin is loaded)
     *
     * @return the active instance of the garbage collector
     */
    public static GarbageCollector getGarbageCollector() {
        return InnovativeItems.getInstance().getGarbageCollector();
    }

    /**
     * A method used to log using the debug level if chosen using the innovative items plugin instance
     * (Should not be used until the plugin is enabled, will throw a null pointer in that case if ignoreDebugLevel is false)
     *
     * @param level the level of the debug
     * @param text the text displayed in the debug
     * @param ignoreDebugLevel whether or not this log should ignore the current debug level
     */
    public static void log(@NotNull LogUtil.Level level, @NotNull String text, boolean ignoreDebugLevel) {
        if (ignoreDebugLevel) {
            LogUtil.logUnblocked(level, text);
            return;
        }

        InnovativeItemsAPI.log(level, text);
    }

    /**
     * A method used to log using the debug level if chosen using the innovative items plugin instance
     * (Should not be used until the plugin is enabled, will throw a null pointer)
     *
     * @param level the level of the debug
     * @param text the text displayed in the debug
     */
    public static void log(@NotNull LogUtil.Level level, @NotNull String text) {
        LogUtil.log(level, text);
    }

    /**
     * A method used to execute an ability based on its identifier
     *
     * @param identifier the provided identifier
     * @param player the player executing the ability
     * @throws IllegalArgumentException when the provided identifier does not correlate to a currently loaded ability
     * @return a boolean that is true when the ability was successfully executed
     */
    public static boolean executeAbility(@NotNull String identifier, @NotNull Player player) throws IllegalArgumentException {
        Ability ability = InnovativeItems.getInstance().getItemCache().getAbility(identifier);

        if (ability == null) {
            throw new IllegalArgumentException("The provided identifier: " + identifier + ", does not belong to any currently loaded ability!");
        }

        return ability.execute(player);
    }

    /**
     * A method used to get an custom item object from the cache
     *
     * @param identifier the name of the custom item
     * @return an optional custom item
     */
    public static Optional<CustomItem> getItem(@NotNull String identifier) {
        CustomItem item = InnovativeItems.getInstance().getItemCache().getItem(identifier);
        return Optional.ofNullable(item);
    }

    /**
     * A method used to get an ability object from the cache
     *
     * @param identifier the name of the ability
     * @return an optional ability
     */
    public static Optional<Ability> getCustomAbility(@NotNull String identifier) {
        Ability ability = InnovativeItems.getInstance().getItemCache().getAbility(identifier);
        return Optional.ofNullable(ability);
    }

    /**
     * A method used to get a custom item object from the cache
     *
     * @param identifier the name of the custom item
     * @return the object that represents the custom item (null if it does not exist)
     */
    @Nullable
    @Deprecated
    public static CustomItem getCustomItem(@NotNull String identifier) {
        return InnovativeItems.getInstance().getItemCache().getItem(identifier);
    }

    /**
     * A method used to get an ability object from the cache
     *
     * @param identifier the name of the ability
     * @return the object that represents the ability (null if it does not exist)
     */
    @Nullable
    @Deprecated
    public static Ability getAbility(@NotNull String identifier) {
        return InnovativeItems.getInstance().getItemCache().getAbility(identifier);
    }
}
