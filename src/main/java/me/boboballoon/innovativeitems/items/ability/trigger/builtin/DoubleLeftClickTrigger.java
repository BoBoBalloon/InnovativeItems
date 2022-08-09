package me.boboballoon.innovativeitems.items.ability.trigger.builtin;

import me.boboballoon.innovativeitems.functions.context.InteractContext;
import me.boboballoon.innovativeitems.items.CustomItem;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.InventoryIterator;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A class that represents the "double-left-click" ability trigger
 */
public class DoubleLeftClickTrigger extends AbilityTrigger<PlayerInteractEvent, InteractContext> {
    private final Map<UUID, Long> queue;

    private static final int DELAY = 500; //in milliseconds

    public DoubleLeftClickTrigger() {
        super("double-left-click", null, PlayerInteractEvent.class, InteractContext.class, InventoryIterator.fromFunctionSingleton((event, inventory) -> event.getItem()), event -> event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK);
        this.queue = new HashMap<>();
    }

    @Override
    @NotNull
    public Player fromEvent(@NotNull PlayerInteractEvent event) {
        return event.getPlayer();
    }

    @Nullable
    @Override
    public InteractContext trigger(@NotNull PlayerInteractEvent event, @NotNull CustomItem item, @NotNull Ability ability) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!this.queue.containsKey(uuid) || System.currentTimeMillis() - this.queue.get(uuid) > DELAY) {
            this.queue.put(uuid, System.currentTimeMillis());
            return null;
        }

        this.queue.remove(uuid);
        return new InteractContext(player, ability, event.getAction(), event.getHand());
    }
}
