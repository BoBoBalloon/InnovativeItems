package me.boboballoon.innovativeitems.items.ability.trigger.builtin;

import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.context.InteractEntityContext;
import me.boboballoon.innovativeitems.items.CustomItem;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.InventoryIterator;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents the "right-click-entity" ability trigger
 */
public class RightClickEntityTrigger extends AbilityTrigger<PlayerInteractEntityEvent, InteractEntityContext> {
    public RightClickEntityTrigger() {
        super("right-click-entity", null, PlayerInteractEntityEvent.class, InteractEntityContext.class, InventoryIterator.Constants.armorAndHands(), event -> event.getHand() == EquipmentSlot.HAND && event.getRightClicked() instanceof LivingEntity, FunctionTargeter.ENTITY);
    }

    @Override
    @NotNull
    public Player fromEvent(@NotNull PlayerInteractEntityEvent event) {
        return event.getPlayer();
    }

    @NotNull
    @Override
    public InteractEntityContext trigger(@NotNull PlayerInteractEntityEvent event, @NotNull CustomItem item, @NotNull Ability ability) {
        return new InteractEntityContext(event.getPlayer(), ability, Action.RIGHT_CLICK_AIR, event.getHand(), (LivingEntity) event.getRightClicked());
    }
}
