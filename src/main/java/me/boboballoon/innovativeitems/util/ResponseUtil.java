package me.boboballoon.innovativeitems.util;

import me.boboballoon.innovativeitems.InnovativeItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A class used to get user input from players without having to use NMS/ProtocolLib (chat > sign U anvil)
 */
public final class ResponseUtil {
    private static final Map<UUID, UserResponse> RESPONDING = new HashMap<>();
    private static boolean enabled = false;

    /**
     * Constructor to prevent people from using this util class in an object oriented way
     */
    private ResponseUtil() {}

    /**
     * A method used to register the listeners used for the util
     */
    public static void enable() {
        if (!enabled) {
            Bukkit.getPluginManager().registerEvents(new ChatListener(), InnovativeItems.getInstance());
            ResponseUtil.enabled = true;
        }
    }

    /**
     * A method used to get a response from a player
     *
     * @param prompt what message to send to the user (if null is provided a default message will be sent and if an empty string is provided no message will be sent at all)
     * @param to the player to send it to
     * @param response the action to be taken when a response is provided
     * @return true if added the player to the "waiting list" was successful, will fail if the util is still waiting for another prompt
     */
    public static boolean input(@Nullable String prompt, @NotNull Player to, @NotNull UserResponse response) {
        if (ResponseUtil.RESPONDING.containsKey(to.getUniqueId())) {
            return false;
        }

        if (prompt != null && !prompt.isEmpty()) {
            TextUtil.sendMessage(to, prompt);
        } else if (prompt == null) {
            TextUtil.sendMessage(to, "&rPlease type your response in chat. Type &r&ccancel&r to end the prompt.");
        } //do nothing is the prompt was empty (only remaining case)

        ResponseUtil.RESPONDING.put(to.getUniqueId(), response);
        return true;
    }

    /**
     * A method used to get a response from a player
     *
     * @param to the player to send it to
     * @param response the action to be taken when a response is provided
     * @return true if added the player to the "waiting list" was successful, will fail if the util is still waiting for another prompt
     */
    public static boolean input(@NotNull Player to, @NotNull UserResponse response) {
        return ResponseUtil.input(null, to, response);
    }

    /**
     * An interface used to receive user responses
     */
    @FunctionalInterface
    public interface UserResponse {
        /**
         * A method used to use a provided response
         *
         * @param response what the user responded with (null if player cancelled response or left the game)
         */
        void response(@Nullable String response);
    }

    /**
     * An internal class for the sole purpose of listening for user responses
     */
    private static class ChatListener implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        private void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
            if (!ResponseUtil.RESPONDING.containsKey(event.getPlayer().getUniqueId())) {
                return;
            }

            UserResponse response = ResponseUtil.RESPONDING.remove(event.getPlayer().getUniqueId());
            String message = event.getMessage();

            Bukkit.getScheduler().runTask(InnovativeItems.getInstance(), () -> response.response(message.equalsIgnoreCase("cancel") ? null : message));
            event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        private void onPlayerQuit(PlayerQuitEvent event) {
            UserResponse response = ResponseUtil.RESPONDING.remove(event.getPlayer().getUniqueId());

            if (response != null) {
                response.response(null);
            }
        }
    }
}
