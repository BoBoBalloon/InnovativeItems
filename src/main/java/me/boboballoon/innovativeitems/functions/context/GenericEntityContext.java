package me.boboballoon.innovativeitems.functions.context;

import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.items.ability.Ability;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents context where an entity was involved
 */
public class GenericEntityContext extends RuntimeContext implements EntityContext {
    private final LivingEntity entity;

    public GenericEntityContext(@NotNull Player player, @NotNull Ability ability, @NotNull LivingEntity entity) {
        super(player, ability);
        this.entity = entity;
    }

    @Override
    @NotNull
    public LivingEntity getEntity() {
        return this.entity;
    }
}
