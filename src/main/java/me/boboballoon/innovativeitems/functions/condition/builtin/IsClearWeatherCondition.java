package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a condition in an ability config file that checks if the weather is clear
 */
public class IsClearWeatherCondition extends Condition {
    public IsClearWeatherCondition() {
        super("isclearweather");
    }

    @Override
    protected Boolean call(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        return context.getPlayer().getWorld().isClearWeather();
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
