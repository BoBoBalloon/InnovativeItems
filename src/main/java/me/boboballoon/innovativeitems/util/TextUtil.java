package me.boboballoon.innovativeitems.util;

import org.bukkit.ChatColor;

/**
 * A class used to store util methods regarding strings
 */
public class TextUtil {

    /**
     * Method wrapper so I have access to a shortcut to this translate method
     *
     * @param text text you wish to add color codes to
     * @return text with added color codes
     */
    public static String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
