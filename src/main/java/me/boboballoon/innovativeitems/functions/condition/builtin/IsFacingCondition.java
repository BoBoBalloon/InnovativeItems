package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.util.Direction;
import org.bukkit.entity.LivingEntity;

/**
 * Class that represents a condition in an ability config file that checks if the target is facing the specified direction
 */
public class IsFacingCondition extends Condition {
    public IsFacingCondition() {
        super("isfacing",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedManual((rawValue, context) -> Direction.valueOf(rawValue.toUpperCase()), "direction"));
    }

    @Override
    protected Boolean call(ImmutableList<Object> arguments, RuntimeContext context) {
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);
        LivingEntity target = null;

        if (targeter == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (targeter == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;
            target = entityContext.getEntity();
        }

        Direction direction = (Direction) arguments.get(1);
        Direction facing = Direction.getCardinalDirection(target);

        return direction == facing;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
