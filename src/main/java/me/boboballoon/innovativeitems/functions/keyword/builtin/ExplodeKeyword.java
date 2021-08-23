package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedValues;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.BlockContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import org.bukkit.Location;

/**
 * Class that represents a keyword in an ability config file that spawns an explosion at a selected target
 */
public class ExplodeKeyword extends Keyword {
    public ExplodeKeyword() {
        super("explode",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY, FunctionTargeter.BLOCK),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.FLOAT, "explosion power"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.BOOLEAN, "should break blocks"));
    }

    @Override
    protected void calling(ImmutableList<Object> arguments, RuntimeContext context) {
        Location location = null;
        FunctionTargeter rawLocation = (FunctionTargeter) arguments.get(0);

        if (rawLocation == FunctionTargeter.PLAYER) {
            location = context.getPlayer().getLocation();
        }

        if (rawLocation == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;
            location = entityContext.getEntity().getLocation();
        }

        if (rawLocation == FunctionTargeter.BLOCK && context instanceof BlockContext) {
            BlockContext blockContext = (BlockContext) context;
            location = blockContext.getBlock().getLocation();
        }

        float power = (float) arguments.get(1);
        boolean breakBlocks = (boolean) arguments.get(2);

        location.getWorld().createExplosion(location, power, false, breakBlocks);
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}