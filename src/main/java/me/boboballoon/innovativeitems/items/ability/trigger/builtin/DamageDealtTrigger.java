package me.boboballoon.innovativeitems.items.ability.trigger.builtin;

import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.context.DamageContext;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.InventoryIterator;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents the "damage-dealt" ability trigger
 */
public class DamageDealtTrigger extends AbilityTrigger<EntityDamageByEntityEvent, DamageContext> {
    public DamageDealtTrigger() {
        super("damage-dealt", null, EntityDamageByEntityEvent.class, DamageContext.class, InventoryIterator.Constants.armorAndHands(), event -> event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity, FunctionTargeter.ENTITY);
    }

    @Override
    @NotNull
    public Player fromEvent(@NotNull EntityDamageByEntityEvent event) {
        return (Player) event.getDamager();
    }

    @NotNull
    @Override
    public DamageContext trigger(@NotNull EntityDamageByEntityEvent event, @NotNull CustomItem item, @NotNull Ability ability) {
        Player player = (Player) event.getDamager();
        LivingEntity entity = (LivingEntity) event.getEntity();

        return new DamageContext(player, ability, entity, true);
    }
}
