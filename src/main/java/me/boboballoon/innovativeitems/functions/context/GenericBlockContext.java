package me.boboballoon.innovativeitems.functions.context;

import me.boboballoon.innovativeitems.functions.context.interfaces.BlockContext;
import me.boboballoon.innovativeitems.items.ability.Ability;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents context where a block was involved
 */
public class GenericBlockContext extends RuntimeContext implements BlockContext {
    private final Block block;

    public GenericBlockContext(@NotNull Player player, @NotNull Ability ability, @NotNull Block block) {
        super(player, ability);
        this.block = block;
    }

    @Override
    @NotNull
    public Block getBlock() {
        return this.block;
    }
}
