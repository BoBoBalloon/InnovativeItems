package me.boboballoon.innovativeitems.items.ability.trigger.builtin;

import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.InventoryIterator;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents the "crouch" ability trigger
 */
public class CrouchTrigger extends AbilityTrigger<PlayerToggleSneakEvent, RuntimeContext> {
    public CrouchTrigger() {
        super("crouch", null, PlayerToggleSneakEvent.class, RuntimeContext.class, InventoryIterator.Constants.armorAndHands(), PlayerToggleSneakEvent::isSneaking);
    }

    @Override
    @NotNull
    public Player fromEvent(@NotNull PlayerToggleSneakEvent event) {
        return event.getPlayer();
    }

    @NotNull
    @Override
    public RuntimeContext trigger(@NotNull PlayerToggleSneakEvent event, @NotNull CustomItem item, @NotNull Ability ability) {
        return new RuntimeContext(event.getPlayer(), ability);
    }
}
