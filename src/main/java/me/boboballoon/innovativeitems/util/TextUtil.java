package me.boboballoon.innovativeitems.util;

import me.boboballoon.innovativeitems.InnovativeItems;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class used to store util methods regarding strings
 */
public final class TextUtil {
    private static final Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})");
    private static final String PREFIX = TextUtil.format("&r&e&l[InnovativeItems] > &r");

    /**
     * Constructor to prevent people from using this util class in an object oriented way
     */
    private TextUtil() {}

    /**
     * Method to colorize a string with both default color codes + hex color support
     * Stolen from: https://www.spigotmc.org/threads/hex-chat-class.449300/#post-3865028
     *
     * @param text text you wish to add color codes to
     * @return text with added color codes
     */
    @NotNull
    public static String format(@NotNull String text) {
        if (!InnovativeItems.isPluginPremium()) {
            return ChatColor.translateAlternateColorCodes('&', text); //normal color codes
        }

        //normal color codes + hex color codes

        Matcher matcher = HEX_PATTERN.matcher(ChatColor.translateAlternateColorCodes('&', text));

        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of(matcher.group(1)).toString());
        }

        return matcher.appendTail(buffer).toString();
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
