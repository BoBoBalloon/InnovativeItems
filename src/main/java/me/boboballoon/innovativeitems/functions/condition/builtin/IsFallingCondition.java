package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a condition in an ability config file that checks if the specified entity is falling
 */
public class IsFallingCondition extends Condition {
    public IsFallingCondition() {
        super("isfalling",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY));
    }

    @Override
    protected Boolean call(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);
        LivingEntity target = null;

        if (targeter == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        } else if (targeter == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;
            target = entityContext.getEntity();
        }

        return target.getVelocity().getY() < 0 && !target.isOnGround();
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
