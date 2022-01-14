package me.boboballoon.innovativeitems.items.ability;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.condition.ActiveCondition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.ActiveKeyword;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.util.TextUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A class used to show an ability tied to an item with a cooldown
 */
public class AbilityCooldown extends Ability {
    private final long cooldown;
    private final CooldownMessage message;
    private final Map<UUID, Long> onCooldown;

    public AbilityCooldown(@NotNull String identifier, @NotNull ImmutableList<ActiveKeyword> keywords, @NotNull ImmutableList<ActiveCondition> conditions, @NotNull AbilityTrigger<?, ?> trigger, @NotNull String providedTriggerIdentifier, long cooldown, @Nullable CooldownMessage message) {
        super(identifier, keywords, conditions, trigger, providedTriggerIdentifier);

        if (cooldown <= 0) {
            throw new IllegalArgumentException("The provided cooldown is less than or equal to zero!");
        }

        this.cooldown = cooldown * 50; //convert ticks to milliseconds
        this.message = message;
        this.onCooldown = new HashMap<>();
    }

    /**
     * A method used to return the amount of ticks for the ability's cooldown
     *
     * @return the amount of ticks for the ability's cooldown
     */
    public long getCooldown() {
        return this.cooldown;
    }

    /**
     * A method used to return the message that is sent to the player if they used this ability while it was still on cooldown
     *
     * @return the message that is sent to the player if they used this ability while it was still on cooldown (null if no message should be sent)
     */
    @Nullable
    public CooldownMessage getMessage() {
        return this.message;
    }

    /**
     * A method used to return all the players currently on this abilities cooldown
     *
     * @return all the players currently on this abilities cooldown
     */
    public Map<UUID, Long> getOnCooldown() {
        return this.onCooldown;
    }

    /**
     * A quick method to tell if the provided user still has this ability on cooldown
     *
     * @param player the provided user
     * @return a boolean that is true if the provided user still has this ability on cooldown
     */
    public boolean isOnCooldown(@NotNull Player player) {
        UUID uuid = player.getUniqueId();

        if (!this.onCooldown.containsKey(uuid)) {
            return false;
        }

        return this.onCooldown.get(uuid) + this.cooldown > System.currentTimeMillis();
    }

    /**
     * A method used to return if this ability shows the user if it is on cooldown for them
     *
     * @return a boolean that is true if this ability shows the user if it is on cooldown for them
     */
    @Deprecated
    public boolean shouldShowCooldown() {
        return this.message != null;
    }

    /**
     * A method used to execute an ability with a cooldown (will always be fired async)
     *
     * @param context the context in which the ability was triggered
     * @return a boolean that is true when the ability executed successfully
     */
    @Override
    public boolean execute(@NotNull RuntimeContext context) {
        Player player = context.getPlayer();
        UUID uuid = player.getUniqueId();

        boolean isOnCooldown = this.isOnCooldown(player);

        if (isOnCooldown && this.message != null) {
            this.message.send(player, this.onCooldown.get(uuid), this.cooldown);
            return false;
        } else if (isOnCooldown) {
            return false;
        }

        boolean result = super.execute(context);

        if (result) {
            this.onCooldown.put(uuid, System.currentTimeMillis());
        }

        return result;
    }

    /**
     * A class that represents a message sent to a player when the ability they tried to execute is still on cooldown
     */
    public static class CooldownMessage {
        private final String message;
        private final ChatMessageType type;

        public CooldownMessage(@NotNull String message, @NotNull ChatMessageType type) {
            this.message = TextUtil.format(message);
            this.type = type;
        }

        /**
         * A method used to send an actionbar message saying how much time they have left on cooldown
         *
         * @param player the player to send the actionbar message to
         * @param waitTime the time that they last executed the ability
         * @param cooldown the cooldown time of the ability
         */
        public void send(Player player, long waitTime, long cooldown) {
            long millis = (waitTime + cooldown) - System.currentTimeMillis();

            String cooldownText = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis), TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));

            BaseComponent[] message = TextComponent.fromLegacyText(this.message.replace("{cooldown}", cooldownText));

            player.spigot().sendMessage(this.type, message);
        }
    }
}
