package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Class that represents a keyword in an ability config file that swaps the position of two entities
 */
public class SwitcherooKeyword extends Keyword {
    public SwitcherooKeyword() {
        super("switcheroo",
                new ExpectedTargeters(FunctionTargeter.ENTITY));
    }

    @Override
    protected void calling(ImmutableList<Object> arguments, RuntimeContext context) {
        LivingEntity entityOne = context.getPlayer();
        LivingEntity entityTwo;

        if (context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;
            entityTwo = entityContext.getEntity();
        } else {
            return;
        }

        Location locationOne = entityOne.getLocation();
        Location locationTwo = entityTwo.getLocation();

        entityOne.teleport(locationTwo, PlayerTeleportEvent.TeleportCause.PLUGIN);
        entityTwo.teleport(locationOne, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
