package me.boboballoon.innovativeitems.functions.context;

import me.boboballoon.innovativeitems.functions.context.interfaces.BlockContext;
import me.boboballoon.innovativeitems.items.ability.Ability;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents context where the item was clicked and targeting a block that can assist execution that cannot be cached and must be parsed during runtime separately
 */
public class InteractBlockContext extends InteractContext implements BlockContext {
    private final Block block;

    public InteractBlockContext(@NotNull Player player, @NotNull Ability ability, @NotNull Action action, @NotNull EquipmentSlot hand, @NotNull Block block) {
        super(player, ability, action, hand);
        this.block = block;
    }

    @Override
    @NotNull
    public Block getBlock() {
        return this.block;
    }
}
