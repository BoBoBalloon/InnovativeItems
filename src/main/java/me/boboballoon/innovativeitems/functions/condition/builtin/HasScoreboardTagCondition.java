package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a condition in an ability config file that checks if the target entity is blocking with a shield
 */
public class HasScoreboardTagCondition extends Condition {
    public HasScoreboardTagCondition() {
        super("hasscoreboardtag",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING));
    }

    @Override
    protected Boolean call(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);
        LivingEntity entity = targeter == FunctionTargeter.PLAYER ? context.getPlayer() : ((EntityContext) context).getEntity();
        String tag = (String) arguments.get(1);
        return entity.getScoreboardTags().contains(tag);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
