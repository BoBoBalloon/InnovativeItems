package me.boboballoon.innovativeitems.items.ability.trigger.builtin;

import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.context.InteractBlockContext;
import me.boboballoon.innovativeitems.items.CustomItem;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.InventoryIterator;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents the "right-click-block" ability trigger
 */
public class RightClickBlockTrigger extends AbilityTrigger<PlayerInteractEvent, InteractBlockContext> {
    public RightClickBlockTrigger() {
        super("right-click-block", null, PlayerInteractEvent.class, InteractBlockContext.class, InventoryIterator.fromFunctionSingleton((event, inventory) -> event.getItem()), event -> event.hasBlock() && event.getAction() == Action.RIGHT_CLICK_BLOCK, FunctionTargeter.BLOCK);
    }

    @Override
    @NotNull
    public Player fromEvent(@NotNull PlayerInteractEvent event) {
        return event.getPlayer();
    }

    @NotNull
    @Override
    public InteractBlockContext trigger(@NotNull PlayerInteractEvent event, @NotNull CustomItem item, @NotNull Ability ability) {
        return new InteractBlockContext(event.getPlayer(), ability, event.getAction(), event.getHand(), event.getClickedBlock());
    }
}
