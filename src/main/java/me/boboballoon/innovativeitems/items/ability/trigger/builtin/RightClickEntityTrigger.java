package me.boboballoon.innovativeitems.items.ability.trigger.builtin;

import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.context.InteractContextEntity;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.InventoryIterator;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents the "right-click-entity" ability trigger
 */
public class RightClickEntityTrigger extends AbilityTrigger<PlayerInteractEntityEvent, InteractContextEntity> {
    public RightClickEntityTrigger() {
        super("right-click-entity", null, PlayerInteractEntityEvent.class, InteractContextEntity.class, InventoryIterator.Constants.armorAndHands(), event -> event.getHand() == EquipmentSlot.HAND && event.getRightClicked() instanceof LivingEntity, FunctionTargeter.ENTITY);
    }

    @Override
    @NotNull
    public Player fromEvent(@NotNull PlayerInteractEntityEvent event) {
        return event.getPlayer();
    }

    @NotNull
    @Override
    public InteractContextEntity trigger(@NotNull PlayerInteractEntityEvent event, @NotNull CustomItem item, @NotNull Ability ability) {
        return new InteractContextEntity(event.getPlayer(), ability, Action.RIGHT_CLICK_AIR, event.getHand(), (LivingEntity) event.getRightClicked());
    }
}
