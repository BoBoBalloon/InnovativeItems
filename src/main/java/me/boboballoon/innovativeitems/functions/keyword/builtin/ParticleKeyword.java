package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.context.DamageContext;
import me.boboballoon.innovativeitems.functions.context.InteractContextBlock;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedValues;
import org.bukkit.Location;
import org.bukkit.Particle;

/**
 * Class that represents a keyword in an ability config file that fires a particle at a selected target
 */
public class ParticleKeyword extends Keyword {
    public ParticleKeyword() {
        super("particle",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY, FunctionTargeter.BLOCK),
                new ExpectedManual((rawValue, context) -> Particle.valueOf(rawValue.toUpperCase()), "particle name"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "particle amount"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.DOUBLE, "x offset"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.DOUBLE, "y offset"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.DOUBLE, "z offset"));
    }

    @Override
    protected void calling(ImmutableList<Object> arguments, RuntimeContext context) {
        Location location = null;
        FunctionTargeter rawLocation = (FunctionTargeter) arguments.get(0);

        if (rawLocation == FunctionTargeter.PLAYER) {
            location = context.getPlayer().getLocation();
        }

        if (rawLocation == FunctionTargeter.ENTITY && context instanceof DamageContext) {
            DamageContext damageContext = (DamageContext) context;
            location = damageContext.getEntity().getLocation();
        }

        if (rawLocation == FunctionTargeter.BLOCK && context instanceof InteractContextBlock) {
            InteractContextBlock interactContext = (InteractContextBlock) context;
            location = interactContext.getBlock().getLocation();
        }

        Particle particle = (Particle) arguments.get(1);
        int amount = (Integer) arguments.get(2);
        double xOffset = (Double) arguments.get(3);
        double yOffset = (Double) arguments.get(4);
        double zOffset = (Double) arguments.get(5);

        location.getWorld().spawnParticle(particle, location, amount, xOffset, yOffset, zOffset);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}