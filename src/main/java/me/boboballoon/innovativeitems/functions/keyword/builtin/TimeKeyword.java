package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.util.TimeOfDay;
import org.bukkit.World;

/**
 * Class that represents a keyword in an ability config file that sets the time of day in the given world
 */
public class TimeKeyword extends Keyword {
    public TimeKeyword() {
        super("time",
                new ExpectedManual((rawValue, context) -> TimeOfDay.valueOf(rawValue.toUpperCase()), "time of day"));
    }

    @Override
    protected void calling(ImmutableList<Object> arguments, RuntimeContext context) {
        TimeOfDay time = (TimeOfDay) arguments.get(0);

        World world = context.getPlayer().getWorld();

        world.setTime(time.getTime());
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
