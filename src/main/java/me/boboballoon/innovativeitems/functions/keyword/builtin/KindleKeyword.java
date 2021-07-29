package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.context.DamageContext;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedValues;
import org.bukkit.entity.LivingEntity;

/**
 * Class that represents a keyword in an ability config file that sets fire to a selected target
 */
public class KindleKeyword extends Keyword {
    public KindleKeyword() {
        super("kindle",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "duration"));
    }

    @Override
    protected void calling(ImmutableList<Object> arguments, RuntimeContext context) {
        LivingEntity target = null;
        FunctionTargeter rawTarget = (FunctionTargeter) arguments.get(0);

        if (rawTarget == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (rawTarget == FunctionTargeter.ENTITY && context instanceof DamageContext) {
            DamageContext damageContext = (DamageContext) context;
            target = damageContext.getEntity();
        }

        int duration = (int) arguments.get(1);

        target.setFireTicks(duration);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
