package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedValues;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Class that represents a keyword in an ability config file that teleports an entity to a location
 */
public class TeleportKeyword extends Keyword {
    public TeleportKeyword() {
        super("teleport",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.DOUBLE, "x coordinate"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.DOUBLE, "y coordinate"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.DOUBLE, "z coordinate"),
                new ExpectedManual((rawValue, context) -> Bukkit.getWorld(rawValue), "world name"));
    }

    @Override
    protected void calling(ImmutableList<Object> arguments, RuntimeContext context) {
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);
        LivingEntity target = null;

        if (targeter == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (targeter == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;
            target = entityContext.getEntity();
        }

        double x = (double) arguments.get(1);
        double y = (double) arguments.get(2);
        double z = (double) arguments.get(3);
        World world = (World) arguments.get(4);

        Location current = target.getLocation();
        Location location = new Location(world, x, y, z, current.getYaw(), current.getPitch());

        target.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
