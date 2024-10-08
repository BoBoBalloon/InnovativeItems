package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedEnum;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.util.TimeOfDay;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a keyword in an ability config file that sets the time of day in the given world
 */
public class TimeKeyword extends Keyword {
    public TimeKeyword() {
        super("time",
                new ExpectedEnum<>(TimeOfDay.class, "time of day"));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        TimeOfDay time = (TimeOfDay) arguments.get(0);

        World world = context.getPlayer().getWorld();

        world.setTime(time.getTime());
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
