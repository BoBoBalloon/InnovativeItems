package me.boboballoon.innovativeitems.items.ability.trigger.builtin;

import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.InventoryIterator;
import me.boboballoon.innovativeitems.items.ability.trigger.ManuallyRegister;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents the "none" ability trigger
 */
@ManuallyRegister
public class NoneTrigger extends AbilityTrigger<Event, RuntimeContext> {
    public NoneTrigger() {
        super("none", null, Event.class, RuntimeContext.class, InventoryIterator.Constants.armorAndHands(), null);
    }

    @Override
    @NotNull
    public Player fromEvent(@NotNull Event event) {
        throw new UnsupportedOperationException("Congrats, you managed to execute an event that wasn't even registered! Yikes...");
    }

    @NotNull
    @Override
    public RuntimeContext trigger(@NotNull Event event, @NotNull CustomItem item, @NotNull Ability ability) {
        throw new UnsupportedOperationException("Congrats, you managed to execute an event that wasn't even registered! Yikes...");
    }
}
