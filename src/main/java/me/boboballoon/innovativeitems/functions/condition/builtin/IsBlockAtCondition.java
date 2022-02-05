package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedEnum;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a condition in an ability config file that checks if the block at the target location is the specified type
 */
public class IsBlockAtCondition extends Condition {
    public IsBlockAtCondition() {
        super("isblockat",
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "x"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "y"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "z"),
                new ExpectedManual((rawValue, context) -> Bukkit.getWorld(rawValue), "world name"),
                new ExpectedEnum<>(Material.class, "material type"));
    }

    @Override
    protected Boolean call(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
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
