package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a keyword in an ability config file that sets the pitch selected target
 */
public class SetPitchKeyword extends Keyword {
    public SetPitchKeyword() {
        super("setpitch",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.FLOAT, "pitch value"));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);
        LivingEntity target = targeter == FunctionTargeter.PLAYER ? context.getPlayer() : ((EntityContext) context).getEntity();
        float pitch = (float) arguments.get(1);

        Location location = target.getLocation();
        location.setPitch(pitch);
        target.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}