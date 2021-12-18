package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import org.bukkit.entity.LivingEntity;

/**
 * Class that represents a condition in an ability config file that checks if the target has a certain amount of health
 */
public class IsHeathAtCondition extends Condition {
    public IsHeathAtCondition() {
        super("ishealthat",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "health amount"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.CHAR, "operation", object -> {
                    char value = (char) object;
                    return value == '>' || value == '<' || value == '=';
                }));
    }

    @Override
    protected Boolean call(ImmutableList<Object> arguments, RuntimeContext context) {
        LivingEntity target = null;
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);

        if (targeter == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (targeter == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;
            target = entityContext.getEntity();
        }

        int amount = (int) arguments.get(1);
        int activeAmount = (int) target.getHealth();
        char operation = (char) arguments.get(2);


        if (operation == '>') {
            return activeAmount > amount;
        }

        if (operation == '<') {
            return activeAmount < amount;
        }

        if (operation == '=') {
            return activeAmount == amount;
        }

        //this will never fire due to the condition in place for the string in the constructor
        return null;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
