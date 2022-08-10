package me.boboballoon.innovativeitems.items.ability.trigger.builtin.projectile;

import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.context.GenericBlockContext;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * A class that represents the "arrow-hit-block" ability trigger
 */
public class ArrowHitBlockTrigger extends AbilityTrigger<ProjectileHitEvent, GenericBlockContext> {
    public ArrowHitBlockTrigger() {
        super("arrow-hit-block", null, ProjectileHitEvent.class, GenericBlockContext.class, (event, player) -> Collections.singleton(ArrowFireListener.get(event.getEntity())), event -> event.getHitBlock() != null && ArrowFireListener.contains(event.getEntity()), FunctionTargeter.BLOCK);
    }

    @Override
    @NotNull
    public Player fromEvent(@NotNull ProjectileHitEvent event) {
        return (Player) event.getEntity().getShooter();
    }

    @NotNull
    @Override
    public GenericBlockContext trigger(@NotNull ProjectileHitEvent event, @NotNull CustomItem item, @NotNull Ability ability) {
        return new GenericBlockContext(this.fromEvent(event), ability, event.getHitBlock());
    }
}
