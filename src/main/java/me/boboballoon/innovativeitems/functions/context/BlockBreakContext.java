package me.boboballoon.innovativeitems.functions.context;

import me.boboballoon.innovativeitems.functions.context.interfaces.BlockContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.ItemContext;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * A class that represents context where a block was broken and can assist execution that cannot be cached and must be parsed during runtime separately
 */
public class BlockBreakContext extends RuntimeContext implements BlockContext, ItemContext {
    private final Block block;
    private final CustomItem item;

    public BlockBreakContext(Player player, Ability ability, Block block, CustomItem item) {
        super(player, ability);
        this.block = block;
        this.item = item;
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    @Override
    public CustomItem getItem() {
        return this.item;
    }
}
