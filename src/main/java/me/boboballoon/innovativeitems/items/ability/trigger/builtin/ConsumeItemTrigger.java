package me.boboballoon.innovativeitems.items.ability.trigger.builtin;

import me.boboballoon.innovativeitems.functions.context.ConsumeContext;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.InventoryIterator;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents the "item-consume" ability trigger
 */
public class ConsumeItemTrigger extends AbilityTrigger<PlayerItemConsumeEvent, ConsumeContext> {
    public ConsumeItemTrigger() {
        super("item-consume", null, PlayerItemConsumeEvent.class, ConsumeContext.class, InventoryIterator.fromFunctionSingleton((event, inventory) -> event.getItem()), null);
    }

    @Override
    @NotNull
    public Player fromEvent(@NotNull PlayerItemConsumeEvent event) {
        return event.getPlayer();
    }

    @NotNull
    @Override
    public ConsumeContext trigger(@NotNull PlayerItemConsumeEvent event, @NotNull CustomItem item, @NotNull Ability ability) {
        return new ConsumeContext(event.getPlayer(), ability, item);
    }
}
