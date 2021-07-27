package me.boboballoon.innovativeitems.util;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.FunctionContext;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * A class used to store util methods regarding logging
 */
public final class LogUtil {
    /**
     * Constructor to prevent people from using this util class in an object oriented way
     */
    private LogUtil() {
    }

    /**
     * Method wrapper of log method with debug level checks
     * Debug level of 5 allows everything
     * Debug level of 4 allows warnings and severes and info and dev
     * Debug level of 3 allows warnings and severes and info
     * Debug level of 2 allows warnings and severes
     * Debug level of 1 allows severes
     * Debug level of 0 allows nothing
     *
     * @param level the level of the log
     * @param text  text you wish to log
     */
    public static void log(@NotNull Level level, @NotNull String text) {
        int debugLevel = InnovativeItems.getInstance().getConfigManager().getDebugLevel();

        if (level == Level.NOISE && debugLevel < Level.NOISE.getDebugLevel()) {
            return;
        }

        if (level == Level.DEV && debugLevel < Level.DEV.getDebugLevel()) {
            return;
        }

        if (level == Level.INFO && debugLevel < Level.INFO.getDebugLevel()) {
            return;
        }

        if (level == Level.WARNING && debugLevel < Level.WARNING.getDebugLevel()) {
            return;
        }

        if (level == Level.SEVERE && debugLevel < Level.SEVERE.getDebugLevel()) {
            return;
        }

        LogUtil.logUnblocked(level, text);
    }

    /**
     * Method wrapper of log method without restrictions
     *
     * @param level the level of the log
     * @param text  text you wish to log
     */
    public static void logUnblocked(@NotNull Level level, @NotNull String text) {
        InnovativeItems.getInstance().getLogger().log(level.getLogLevel(), text);
    }

    /**
     * A method wrapper to log a keyword exception to prevent repetitive code
     *
     * @param level       the log level
     * @param fieldName   the name of field to be initialized
     * @param keywordName the name of the keyword
     * @param abilityName the name of the ability the keyword is being initialized on
     */
    public static void logKeywordError(@NotNull Level level, @NotNull String fieldName, @NotNull String keywordName, @NotNull String abilityName) {
        LogUtil.log(level, "There is not a valid " + fieldName + " entered on the " + keywordName + " keyword on the " + abilityName + " ability!");
    }

    /**
     * A method wrapper to log a keyword exception to prevent repetitive code
     *
     * @param level     the log level
     * @param context   the context in which the keyword was parsed
     * @param fieldName the name of field to be initialized
     */
    public static void logKeywordError(@NotNull Level level, @NotNull FunctionContext context, @NotNull String fieldName) {
        LogUtil.logKeywordError(level, fieldName, context.getFunction().getIdentifier(), context.getAbilityName());
    }

    /**
     * A method wrapper to log a keyword exception to prevent repetitive code
     *
     * @param context   the context in which the keyword was parsed
     * @param fieldName the name of field to be initialized
     */
    public static void logKeywordError(@NotNull FunctionContext context, @NotNull String fieldName) {
        LogUtil.logKeywordError(Level.WARNING, context, fieldName);
    }

    /**
     * An internal wrapper class used to ensure all debugs follow the proper debug levels
     */
    public enum Level {
        NOTHING(null, 0),
        SEVERE(java.util.logging.Level.SEVERE, 1),
        WARNING(java.util.logging.Level.WARNING, 2),
        INFO(java.util.logging.Level.INFO, 3),
        DEV(java.util.logging.Level.INFO, 4),
        NOISE(java.util.logging.Level.INFO, 5);

        private final java.util.logging.Level logLevel;
        private final int debugLevel;

        Level(java.util.logging.Level logLevel, int debugLevel) {
            this.logLevel = logLevel;
            this.debugLevel = debugLevel;
        }

        /**
         * A method that returns the current internal debug level to be used
         *
         * @return the current internal debug level to be used
         */
        public java.util.logging.Level getLogLevel() {
            return this.logLevel;
        }

        /**
         * A method that returns the innovative items debug level
         *
         * @return the innovative items debug level
         */
        public int getDebugLevel() {
            return this.debugLevel;
        }
    }
}
