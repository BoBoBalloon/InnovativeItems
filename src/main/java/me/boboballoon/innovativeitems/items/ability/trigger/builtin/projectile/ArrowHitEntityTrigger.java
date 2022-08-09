package me.boboballoon.innovativeitems.items.ability.trigger.builtin.projectile;

import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.context.DamageContext;
import me.boboballoon.innovativeitems.items.CustomItem;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * A class that represents the "arrow-hit-entity" ability trigger
 */
public class ArrowHitEntityTrigger extends AbilityTrigger<ProjectileHitEvent, DamageContext> {
    public ArrowHitEntityTrigger() {
        super("arrow-hit-entity", null, ProjectileHitEvent.class, DamageContext.class, (event, player) -> Collections.singleton(ArrowFireListener.get(event.getEntity())), event -> event.getHitEntity() instanceof LivingEntity && ArrowFireListener.contains(event.getEntity()), FunctionTargeter.ENTITY);
    }

    @Override
    @NotNull
    public Player fromEvent(@NotNull ProjectileHitEvent event) {
        return (Player) event.getEntity().getShooter();
    }

    @NotNull
    @Override
    public DamageContext trigger(@NotNull ProjectileHitEvent event, @NotNull CustomItem item, @NotNull Ability ability) {
        return new DamageContext(this.fromEvent(event), ability, (LivingEntity) event.getHitEntity(), true);
    }
}
