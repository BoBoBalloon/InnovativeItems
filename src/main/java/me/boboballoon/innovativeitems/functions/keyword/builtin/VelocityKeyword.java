package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a keyword in an ability config file that sets the velocity of the selected target
 */
public class VelocityKeyword extends Keyword {
    public VelocityKeyword() {
        super("velocity",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.DOUBLE, "x increase"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.DOUBLE, "y increase"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.DOUBLE, "z increase"));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        LivingEntity target = null;
        FunctionTargeter rawTarget = (FunctionTargeter) arguments.get(0);

        if (rawTarget == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (rawTarget == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;
            target = entityContext.getEntity();
        }

        Vector velocity = target.getVelocity();

        double x = (double) arguments.get(1) + velocity.getX();
        double y = (double) arguments.get(2) + velocity.getY();
        double z = (double) arguments.get(3) + velocity.getZ();

        target.setVelocity(new Vector(x, y, z));
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
