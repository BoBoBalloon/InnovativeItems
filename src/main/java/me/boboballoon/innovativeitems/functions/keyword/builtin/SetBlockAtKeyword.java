package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedEnum;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.context.interfaces.BlockContext;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a keyword in an ability config file that sets the material of the block at the currect position
 */
public class SetBlockAtKeyword extends Keyword {
    public SetBlockAtKeyword() {
        super("setblockat",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY, FunctionTargeter.BLOCK),
                new ExpectedEnum<>(Material.class, "material"));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        Block block = null;
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);

        if (targeter == FunctionTargeter.PLAYER) {
            block = context.getPlayer().getLocation().getBlock();
        }

        if (targeter == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;
            block = entityContext.getEntity().getLocation().getBlock();
        }

        if (targeter == FunctionTargeter.BLOCK && context instanceof BlockContext) {
            BlockContext blockContext = (BlockContext) context;
            block = blockContext.getBlock();
        }

        Material material = (Material) arguments.get(1);
        block.setType(material);
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
