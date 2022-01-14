package me.boboballoon.innovativeitems.items.ability.trigger.builtin;

import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.context.BlockBreakContext;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.InventoryIterator;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents the "block-break" ability trigger
 */
public class BlockBreakTrigger extends AbilityTrigger<BlockBreakEvent, BlockBreakContext> {
    public BlockBreakTrigger() {
        super("block-break", null, BlockBreakEvent.class, BlockBreakContext.class, InventoryIterator.Constants.bothHands(), null, FunctionTargeter.BLOCK);
    }

    @Override
    @NotNull
    public Player fromEvent(@NotNull BlockBreakEvent event) {
        return event.getPlayer();
    }

    @NotNull
    @Override
    public BlockBreakContext trigger(@NotNull BlockBreakEvent event, @NotNull CustomItem item, @NotNull Ability ability) {
        return new BlockBreakContext(event.getPlayer(), ability, event.getBlock(), item);
    }
}
