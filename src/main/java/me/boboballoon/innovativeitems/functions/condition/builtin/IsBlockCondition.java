package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedEnum;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.BlockContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a condition in an ability config file that checks if the block at the target location is the specified type
 */
public class IsBlockCondition extends Condition {
    public IsBlockCondition() {
        super("isblock",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY, FunctionTargeter.BLOCK),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "x offset"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "y offset"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "z offset"),
                new ExpectedEnum<>(Material.class, "material type"));
    }

    @Override
    protected Boolean call(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);
        Location location = null;

        if (targeter == FunctionTargeter.PLAYER) {
            location = context.getPlayer().getLocation();
        }

        if (targeter == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;
            location = entityContext.getEntity().getLocation();
        }

        if (targeter == FunctionTargeter.BLOCK && context instanceof BlockContext) {
            BlockContext blockContext = (BlockContext) context;
            location = blockContext.getBlock().getLocation();
        }

        int xOffset = (int) arguments.get(1);
        int yOffset = (int) arguments.get(2);
        int zOffset = (int) arguments.get(3);

        Block block = location.clone().add(xOffset, yOffset, zOffset).getBlock();

        Material material = (Material) arguments.get(4);

        return block.getType() == material;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
