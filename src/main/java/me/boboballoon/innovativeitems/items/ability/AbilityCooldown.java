package me.boboballoon.innovativeitems.items.ability;

import me.boboballoon.innovativeitems.functions.condition.ActiveCondition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.ActiveKeyword;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A class used to show an ability tied to an item with a cooldown
 */
public class AbilityCooldown extends Ability {
    private final long cooldown;
    private final Map<UUID, Long> onCooldown;

    public AbilityCooldown(@NotNull String identifier, @NotNull List<ActiveKeyword> keywords, @NotNull List<ActiveCondition> conditions, @NotNull AbilityTrigger trigger, long cooldown) {
        super(identifier, keywords, conditions, trigger);

        if (cooldown <= 0) {
            throw new IllegalArgumentException("The provided cooldown is less than or equal to zero!");
        }

        this.cooldown = cooldown * 50; //convert ticks to milliseconds
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
            return false;
        }

        boolean result = super.execute(context);

        if (result) {
            this.onCooldown.put(uuid, System.currentTimeMillis());
        }

        return result;
    }
}
