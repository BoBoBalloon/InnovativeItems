package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a condition in an ability config file that checks if the target is sneaking
 */
public class HasLineOfSightCondition extends Condition {
    public HasLineOfSightCondition() {
        super("haslineofsight",
                new ExpectedTargeters(FunctionTargeter.ENTITY),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "max distance", object -> ((int) object) > 0));
    }

    @Override
    protected Boolean call(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        Player player = context.getPlayer();
        LivingEntity target = ((EntityContext) context).getEntity(); //allowed to assume content instance of EntityContext given targeter
        int maxDistance = (int) arguments.get(1);

        return player.getLocation().distance(target.getLocation()) <= maxDistance && player.hasLineOfSight(target);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
