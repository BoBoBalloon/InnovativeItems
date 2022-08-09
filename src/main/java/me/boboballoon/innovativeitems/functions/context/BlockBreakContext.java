package me.boboballoon.innovativeitems.functions.context;

import me.boboballoon.innovativeitems.functions.context.interfaces.ItemContext;
import me.boboballoon.innovativeitems.items.CustomItem;
import me.boboballoon.innovativeitems.items.ability.Ability;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents context where a block was broken and can assist execution that cannot be cached and must be parsed during runtime separately
 */
public class BlockBreakContext extends GenericBlockContext implements ItemContext {
    private final CustomItem item;

    public BlockBreakContext(@NotNull Player player, @NotNull Ability ability, @NotNull Block block, @NotNull CustomItem item) {
        super(player, ability, block);
        this.item = item;
    }

    @Override
    @NotNull
    public CustomItem getItem() {
        return this.item;
    }
}
