package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedValues;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.BlockContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;

import java.util.Collection;

/**
 * Class that represents a keyword in an ability config file that spawns lightning at a selected target
 */
public class LightningKeyword extends Keyword {
    public LightningKeyword() {
        super("lightning",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY, FunctionTargeter.BLOCK),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.DOUBLE, "damage amount"));
    }

    @Override
    protected void calling(ImmutableList<Object> arguments, RuntimeContext context) {
        Location location = null;
        FunctionTargeter rawLocation = (FunctionTargeter) arguments.get(0);

        if (rawLocation == FunctionTargeter.PLAYER) {
            location = context.getPlayer().getLocation();
        }

        if (rawLocation == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;
            location = entityContext.getEntity().getLocation();
        }

        if (rawLocation == FunctionTargeter.BLOCK && context instanceof BlockContext) {
            BlockContext blockContext = (BlockContext) context;
            location = blockContext.getBlock().getLocation();
        }

        location.getWorld().strikeLightningEffect(location);

        double damage = (double) arguments.get(1);

        if (damage <= 0) {
            return;
        }

        Collection<Entity> entities = location.getWorld().getNearbyEntities(location, 5, 5, 5, entity -> entity instanceof Damageable);

        for (Entity entity : entities) {
            Damageable damageable = (Damageable) entity;
            damageable.damage(damage);
        }
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}