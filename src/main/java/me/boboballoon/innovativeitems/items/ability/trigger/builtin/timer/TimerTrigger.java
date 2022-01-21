package me.boboballoon.innovativeitems.items.ability.trigger.builtin.timer;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.InventoryIterator;
import me.boboballoon.innovativeitems.items.ability.trigger.ManuallyRegister;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents the "timer" ability trigger
 */
@ManuallyRegister
public class TimerTrigger extends AbilityTrigger<Event, RuntimeContext> {
    public TimerTrigger() {
        super("timer", "timer:\\d+", Event.class, RuntimeContext.class, InventoryIterator.Constants.armorAndHands(), null);
    }

    @Override
    public void init(@NotNull Ability ability) {
        long timer;
        try {
            timer = Long.parseLong(ability.getProvidedTriggerIdentifier().split(":")[1]);
        } catch (NumberFormatException ignored) {
            LogUtil.log(LogUtil.Level.DEV, "There was an error trying to parse the trigger delay for the " + ability.getIdentifier() + " ability!");
            throw new IllegalArgumentException("The provided trigger identifier cannot reasonably match the provided ability due to the delay not matching the long data type!");
        }

        AbilityTimerManager manager = InnovativeItems.getInstance().getAbilityTimerManager();

        manager.registerTimer(ability, timer);
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
