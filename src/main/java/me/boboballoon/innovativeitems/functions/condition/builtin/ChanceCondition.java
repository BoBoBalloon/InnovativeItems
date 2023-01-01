package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a condition in an ability config file that generates a random number and check if it is greater than a specified rational number
 */
public class ChanceCondition extends Condition {
    public ChanceCondition() {
        super("chance",
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.DOUBLE, "percent chance", raw -> {
                    double value = (double) raw;
                    return value > 0 && value < 100;
                }));
    }

    @Override
    protected Boolean call(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        double percent = (double) arguments.get(0);

        return Math.random() <= percent / 100;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
