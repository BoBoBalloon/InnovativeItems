package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedValues;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.DamageContext;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import org.bukkit.entity.LivingEntity;

/**
 * Class that represents a condition in an ability config file that checks if the target has a certain amount of health
 */
public class IsHeathAtCondition extends Condition {
    public IsHeathAtCondition() {
        super("ishealthat",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "health amount"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.STRING, "operation", object -> {
                    String value = (String) object;

                    return value.equals(">") || value.equals("<") || value.equals("=");
                }));
    }

    @Override
    protected Boolean call(ImmutableList<Object> arguments, RuntimeContext context) {
        LivingEntity target = null;
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);

        if (targeter == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (targeter == FunctionTargeter.ENTITY && context instanceof DamageContext) {
            DamageContext damageContext = (DamageContext) context;
            target = damageContext.getEntity();
        }

        int amount = (int) arguments.get(1); //check if works, if not cast to Integer wrapper class
        int activeAmount = (int) target.getHealth();
        String operation = (String) arguments.get(2);


        if (operation.equals(">")) {
            return amount > activeAmount;
        }

        if (operation.equals("<")) {
            return amount < activeAmount;
        }

        if (operation.equals("=")) {
            return amount == activeAmount;
        }

        //this will never fire due to the condition in place for the string in the constructor
        return null;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
