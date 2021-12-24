package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedEnum;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.BlockContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import org.bukkit.Location;
import org.bukkit.Particle;

/**
 * Class that represents a keyword in an ability config file that fires a particle at a selected target
 */
public class ParticleKeyword extends Keyword {
    public ParticleKeyword() {
        super("particle",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY, FunctionTargeter.BLOCK),
                new ExpectedEnum<>(Particle.class, "particle name"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "particle amount"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.DOUBLE, "x offset"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.DOUBLE, "y offset"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.DOUBLE, "z offset"));
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

        Particle particle = (Particle) arguments.get(1);
        int amount = (int) arguments.get(2);
        double xOffset = (double) arguments.get(3);
        double yOffset = (double) arguments.get(4);
        double zOffset = (double) arguments.get(5);

        location.getWorld().spawnParticle(particle, location, amount, xOffset, yOffset, zOffset);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}