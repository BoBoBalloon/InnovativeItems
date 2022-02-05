package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedEnum;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.util.TimeOfDay;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a condition in an ability config file that checks the time of day
 */
public class IsTimeCondition extends Condition {
    public IsTimeCondition() {
        super("istime",
                new ExpectedEnum<>(TimeOfDay.class, "time of day"));
    }

    @Override
    protected Boolean call(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        long time = context.getPlayer().getWorld().getTime();

        TimeOfDay expected = (TimeOfDay) arguments.get(0);
        TimeOfDay current = TimeOfDay.getViaTime(time);

        return expected == current;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
