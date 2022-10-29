package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a keyword in an ability config file that sets the amount of ticks the target is invulnerable
 */
public class SetInvulnerableKeyword extends Keyword {
    public SetInvulnerableKeyword() {
        super("setinvulnerable",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "invulnerability ticks"));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        LivingEntity target;
        FunctionTargeter rawTarget = (FunctionTargeter) arguments.get(0);

        if (rawTarget == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        } else {
            EntityContext entityContext = (EntityContext) context;
            target = entityContext.getEntity();
        }

        int ticks = (int) arguments.get(1);

        target.setNoDamageTicks(ticks);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
