package me.boboballoon.innovativeitems.items.ability.trigger.builtin;

import me.boboballoon.innovativeitems.functions.context.InteractContext;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.InventoryIterator;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents the "right-click" ability trigger
 */
public class RightClickTrigger extends AbilityTrigger<PlayerInteractEvent, InteractContext> {
    public RightClickTrigger() {
        super("right-click", null, PlayerInteractEvent.class, InteractContext.class, InventoryIterator.fromFunctionSingleton((event, inventory) -> event.getItem()), event -> event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK);
    }

    @Override
    @NotNull
    public Player fromEvent(@NotNull PlayerInteractEvent event) {
        return event.getPlayer();
    }

    @NotNull
    @Override
    public InteractContext trigger(@NotNull PlayerInteractEvent event, @NotNull CustomItem item, @NotNull Ability ability) {
        return new InteractContext(event.getPlayer(), ability, event.getAction(), event.getHand());
    }
}
