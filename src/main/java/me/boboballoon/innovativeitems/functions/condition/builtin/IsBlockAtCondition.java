package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedValues;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Class that represents a condition in an ability config file that checks if the block at the target location is the specified type
 */
public class IsBlockAtCondition extends Condition {
    public IsBlockAtCondition() {
        super("isblockat",
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "x"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "y"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "z"),
                new ExpectedManual((rawValue, context) -> Bukkit.getWorld(rawValue), "world name"),
                new ExpectedManual((rawValue, context) -> Material.valueOf(rawValue.toUpperCase()), "material type"));
    }

    @Override
    protected Boolean call(ImmutableList<Object> arguments, RuntimeContext context) {
        int x = (int) arguments.get(0);
        int y = (int) arguments.get(1);
        int z = (int) arguments.get(2);
        World world = (World) arguments.get(3);

        Block block = world.getBlockAt(x, y, z);

        Material material = (Material) arguments.get(4);

        return block.getType() == material;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
