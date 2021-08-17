package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedValues;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.BlockContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Class that represents a keyword in an ability config file that drops a vanilla minecraft item on the ground
 */
public class DropItemKeyword extends Keyword {
    public DropItemKeyword() {
        super("dropitem",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY, FunctionTargeter.BLOCK),
                new ExpectedManual((rawValue, context) -> Material.valueOf(rawValue.toUpperCase()), "material"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "item amount", object -> {
                    int integer = (int) object;
                    return integer > 0;
                }));
    }

    @Override
    protected void calling(ImmutableList<Object> arguments, RuntimeContext context) {
        Location target = null;
        FunctionTargeter rawTarget = (FunctionTargeter) arguments.get(0);

        if (rawTarget == FunctionTargeter.PLAYER) {
            target = context.getPlayer().getLocation();
        }

        if (rawTarget == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;
            target = entityContext.getEntity().getLocation();
        }

        if (rawTarget == FunctionTargeter.BLOCK && context instanceof BlockContext) {
            BlockContext blockContext = (BlockContext) context;
            target = blockContext.getBlock().getLocation();
        }

        Material material = (Material) arguments.get(1);
        int amount = (int) arguments.get(2);

        ItemStack item = new ItemStack(material, amount);

        target.getWorld().dropItemNaturally(target, item);
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
