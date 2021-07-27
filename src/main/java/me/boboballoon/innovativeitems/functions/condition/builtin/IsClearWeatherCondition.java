package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;

/**
 * Class that represents a condition in an ability config file that checks if the weather is clear
 */
public class IsClearWeatherCondition extends Condition {
    public IsClearWeatherCondition() {
        super("isclearweather");
    }

    @Override
    protected Boolean call(ImmutableList<Object> arguments, RuntimeContext context) {
        return context.getPlayer().getWorld().isClearWeather();
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
