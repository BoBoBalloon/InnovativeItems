package me.boboballoon.innovativeitems.util;

import me.boboballoon.innovativeitems.InnovativeItems;

import java.util.logging.Level;

/**
 * A class used to store util methods regarding logging
 */
public class LogUtil {
    /**
     * Method wrapper of log method with debug level checks
     *
     * @param level the level of the log
     * @param text  text you wish to log
     */
    public static void log(Level level, String text) {
        int debugLevel = InnovativeItems.getInstance().getConfigManager().getDebugLevel();

        if (level == Level.INFO && debugLevel < 3) {
            return;
        }

        if (level == Level.WARNING && debugLevel < 2) {
            return;
        }

        if (level == Level.SEVERE && debugLevel < 1) {
            return;
        }

        InnovativeItems.getInstance().getLogger().log(level, text);
    }

    /**
     * Method wrapper of log method without restrictions
     *
     * @param level the level of the log
     * @param text  text you wish to log
     */
    public static void logUnblocked(Level level, String text) {
        InnovativeItems.getInstance().getLogger().log(level, text);
    }
}
