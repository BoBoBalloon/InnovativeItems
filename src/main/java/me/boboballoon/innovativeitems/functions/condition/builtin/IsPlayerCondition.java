package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import org.bukkit.entity.Player;

/**
 * Class that represents a condition in an ability config file that checks if the target is a player
 */
public class IsPlayerCondition extends Condition {
    public IsPlayerCondition() {
        super("isplayer",
                new ExpectedTargeters(FunctionTargeter.ENTITY));
    }

    @Override
    protected Boolean call(ImmutableList<Object> arguments, RuntimeContext context) {
        EntityContext entityContext = (EntityContext) context;
        return entityContext.getEntity() instanceof Player;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
