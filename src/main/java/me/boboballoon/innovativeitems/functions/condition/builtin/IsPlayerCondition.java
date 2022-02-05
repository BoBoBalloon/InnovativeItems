package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a condition in an ability config file that checks if the target is a player
 */
public class IsPlayerCondition extends Condition {
    public IsPlayerCondition() {
        super("isplayer",
                new ExpectedTargeters(FunctionTargeter.ENTITY));
    }

    @Override
    protected Boolean call(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        EntityContext entityContext = (EntityContext) context;
        return entityContext.getEntity() instanceof Player;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
