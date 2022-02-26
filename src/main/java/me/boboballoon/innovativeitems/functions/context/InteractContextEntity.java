package me.boboballoon.innovativeitems.functions.context;

import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.items.ability.Ability;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents context where the item was clicked and targeting a block that can assist execution that cannot be cached and must be parsed during runtime separately
 */
public class InteractContextEntity extends InteractContext implements EntityContext {
    private final LivingEntity entity;

    public InteractContextEntity(@NotNull Player player, @NotNull Ability ability, @NotNull Action action, @NotNull EquipmentSlot hand, @NotNull LivingEntity entity) {
        super(player, ability, action, hand);
        this.entity = entity;
    }

    @Override
    @NotNull
    public LivingEntity getEntity() {
        return this.entity;
    }
}
