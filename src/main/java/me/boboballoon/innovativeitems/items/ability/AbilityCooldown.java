package me.boboballoon.innovativeitems.items.ability;

import me.boboballoon.innovativeitems.functions.condition.ActiveCondition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.ActiveKeyword;
import me.boboballoon.innovativeitems.util.TextUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A class used to show an ability tied to an item with a cooldown
 */
public class AbilityCooldown extends Ability {
    private final long cooldown;
    private final boolean showCooldown;
    private final Map<UUID, Long> onCooldown;

    public AbilityCooldown(@NotNull String identifier, @NotNull List<ActiveKeyword> keywords, @NotNull List<ActiveCondition> conditions, @NotNull AbilityTrigger trigger, long cooldown, boolean showCooldown) {
        super(identifier, keywords, conditions, trigger);

        if (cooldown <= 0) {
            throw new IllegalArgumentException("The provided cooldown is less than or equal to zero!");
        }

        this.cooldown = cooldown * 50; //convert ticks to milliseconds
        this.showCooldown = showCooldown;
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
     * A method used to return if this ability shows the user if it is on cooldown for them
     *
     * @return a boolean that is true if this ability shows the user if it is on cooldown for them
     */
    public boolean showCooldown() {
        return this.showCooldown;
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
     * A method used to execute an ability with a cooldown (will always be fired async)
     *
     * @param context the context in which the ability was triggered
     * @return a boolean that is true when the ability executed successfully
     */
    @Override
    public boolean execute(RuntimeContext context) {
        UUID uuid = context.getPlayer().getUniqueId();

        if (this.onCooldown.containsKey(uuid) && this.onCooldown.get(uuid) + this.cooldown > System.currentTimeMillis()) {
            this.message(context.getPlayer());
            return false;
        }

        boolean result = super.execute(context);

        if (result) {
            this.onCooldown.put(uuid, System.currentTimeMillis());
        }

        return result;
    }

    /**
     * A method used to send an actionbar message saying how much time they have left on cooldown
     *
     * @param player the player to send the actionbar message to
     */
    private void message(Player player) {
        if (!this.showCooldown) {
            return;
        }

        long millis = (this.onCooldown.get(player.getUniqueId()) + this.cooldown) - System.currentTimeMillis();

        String text = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis), TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));

        TextComponent message = new TextComponent(TextUtil.format("&r&cYou have " + text + " time left until you can use " + this.getIdentifier() + " again!"));

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
    }
}
