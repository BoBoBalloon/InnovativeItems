package me.boboballoon.innovativeitems.util;

import me.boboballoon.innovativeitems.InnovativeItems;

import java.util.logging.Level;

/**
 * A class used to store util methods regarding logging
 */
public class LogUtil {
    private static final String PREFIX = "[InnovativeItems] >";

    /**
     * Method wrapper so I have access to a shortcut to a log method
     *
     * @param level the level of the log
     * @param text  text you wish to log
     */
    public static void log(Level level, String text) {
        InnovativeItems.getInstance().getLogger().log(level, PREFIX + " " + text);
    }
}
