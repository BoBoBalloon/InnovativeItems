package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a condition in an ability config file that checks if the target is in the given world
 */
public class IsInWorldCondition extends Condition {
    public IsInWorldCondition() {
        super("isinworld",
                new ExpectedManual((rawValue, context) -> Bukkit.getWorld(rawValue), "world name"));
    }

    @Override
    protected Boolean call(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        World world = (World) arguments.get(0);
        return context.getPlayer().getWorld() == world;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
