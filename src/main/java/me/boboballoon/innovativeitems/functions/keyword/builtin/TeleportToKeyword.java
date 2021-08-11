package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.BlockContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Class that represents a keyword in an ability config file that teleports a targeter to another targeter
 */
public class TeleportToKeyword extends Keyword {
    public TeleportToKeyword() {
        super("teleportto",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY, FunctionTargeter.BLOCK));
    }

    @Override
    protected void calling(ImmutableList<Object> arguments, RuntimeContext context) {
        FunctionTargeter entityTargeter = (FunctionTargeter) arguments.get(0);
        FunctionTargeter locationTargeter = (FunctionTargeter) arguments.get(1);

        LivingEntity target = this.getEntity(entityTargeter, context);
        Location location = this.getLocation(locationTargeter, context);

        target.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    private LivingEntity getEntity(FunctionTargeter targeter, RuntimeContext context) {
        if (targeter == FunctionTargeter.PLAYER) {
            return context.getPlayer();
        }

        if (targeter == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;
            return entityContext.getEntity();
        }

        return null;
    }

    private Location getLocation(FunctionTargeter targeter, RuntimeContext context) {
        if (targeter == FunctionTargeter.PLAYER) {
            return context.getPlayer().getLocation();
        }

        if (targeter == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;
            return entityContext.getEntity().getLocation();
        }

        if (targeter == FunctionTargeter.BLOCK && context instanceof BlockContext) {
            BlockContext blockContext = (BlockContext) context;
            return blockContext.getBlock().getLocation();
        }

        return null;
    }
}
