package me.boboballoon.innovativeitems.items.ability.trigger.builtin;

import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.context.DamageContext;
import me.boboballoon.innovativeitems.items.CustomItem;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.InventoryIterator;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents the "damage-taken" ability trigger
 */
public class DamageTakenTrigger extends AbilityTrigger<EntityDamageByEntityEvent, DamageContext> {
    public DamageTakenTrigger() {
        super("damage-taken", null, EntityDamageByEntityEvent.class, DamageContext.class, InventoryIterator.Constants.armorAndHands(), event -> event.getEntity() instanceof Player && event.getDamager() instanceof LivingEntity, FunctionTargeter.ENTITY);
    }

    @Override
    @NotNull
    public Player fromEvent(@NotNull EntityDamageByEntityEvent event) {
        return (Player) event.getEntity();
    }

    @NotNull
    @Override
    public DamageContext trigger(@NotNull EntityDamageByEntityEvent event, @NotNull CustomItem item, @NotNull Ability ability) {
        Player player = (Player) event.getEntity();
        LivingEntity damager = (LivingEntity) event.getDamager();

        return new DamageContext(player, ability, damager, false);
    }
}
