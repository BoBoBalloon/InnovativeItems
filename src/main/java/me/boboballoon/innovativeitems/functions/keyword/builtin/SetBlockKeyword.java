package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedEnum;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a keyword in an ability config file that sets the material of the block at the provided position
 */
public class SetBlockKeyword extends Keyword {
    public SetBlockKeyword() {
        super("setblock",
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "x coordinate"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "y coordinate"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "z coordinate"),
                new ExpectedManual((rawValue, context) -> Bukkit.getWorld(rawValue), "world name"),
                new ExpectedEnum<>(Material.class, "material"));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        int x = (int) arguments.get(0);
        int y = (int) arguments.get(1);
        int z = (int) arguments.get(2);
        World world = (World) arguments.get(3);
        Material material = (Material) arguments.get(4);

        world.getBlockAt(x, y, z).setType(material);
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
