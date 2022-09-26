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
 * Class that represents a keyword in an ability config file that removes a scoreboard tag to an entity
 */
public class RemoveScoreboardTagKeyword extends Keyword {
    public RemoveScoreboardTagKeyword() {
        super("removescoreboardtag",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);
        LivingEntity entity = targeter == FunctionTargeter.PLAYER ? context.getPlayer() : ((EntityContext) context).getEntity();
        String tag = (String) arguments.get(1);

        entity.removeScoreboardTag(tag);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
