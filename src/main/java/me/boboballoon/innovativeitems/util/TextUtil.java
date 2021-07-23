package me.boboballoon.innovativeitems.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * A class used to store util methods regarding strings
 */
public final class TextUtil {
    private static final String PREFIX = TextUtil.format("&r&e&l[InnovativeItems] > &r");

    /**
     * Constructor to prevent people from using this util class in an object oriented way
     */
    private TextUtil() {}

    /**
     * Method wrapper so I have access to a shortcut to this translate method
     *
     * @param text text you wish to add color codes to
     * @return text with added color codes
     */
    public static String format(@NotNull String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * A method used to send a message to a player with color codes and prefix
     *
     * @param sender the entity who the message must be sent to
     * @param message the message you wish to send
     */
    public static void sendMessage(@NotNull CommandSender sender, @NotNull String message) {
        sender.sendMessage(PREFIX + TextUtil.format(message));
    }
}
