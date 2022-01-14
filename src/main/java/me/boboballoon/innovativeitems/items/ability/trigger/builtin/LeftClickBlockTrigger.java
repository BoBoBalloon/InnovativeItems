package me.boboballoon.innovativeitems.items.ability.trigger.builtin;

import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.context.InteractContextBlock;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.InventoryIterator;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents the "left-click-block" ability trigger
 */
public class LeftClickBlockTrigger extends AbilityTrigger<PlayerInteractEvent, InteractContextBlock> {
    public LeftClickBlockTrigger() {
        super("left-click-block", null, PlayerInteractEvent.class, InteractContextBlock.class, InventoryIterator.fromFunctionSingleton((event, inventory) -> event.getItem()), event -> event.hasBlock() && event.getAction() == Action.LEFT_CLICK_BLOCK, FunctionTargeter.BLOCK);
    }

    @Override
    @NotNull
    public Player fromEvent(@NotNull PlayerInteractEvent event) {
        return event.getPlayer();
    }

    @NotNull
    @Override
    public InteractContextBlock trigger(@NotNull PlayerInteractEvent event, @NotNull CustomItem item, @NotNull Ability ability) {
        return new InteractContextBlock(event.getPlayer(), ability, event.getAction(), event.getHand(), event.getClickedBlock());
    }
}
