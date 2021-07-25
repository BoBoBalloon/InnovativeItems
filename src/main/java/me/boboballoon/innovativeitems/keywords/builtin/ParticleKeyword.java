package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.context.DamageContext;
import me.boboballoon.innovativeitems.keywords.context.InteractContextBlock;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordTargeter;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedValues;
import org.bukkit.Location;
import org.bukkit.Particle;

/**
 * Class that represents a keyword in an ability config file that fires a particle at a selected target
 */
public class ParticleKeyword extends Keyword {
    public ParticleKeyword() {
        super("particle",
                new ExpectedTargeters(KeywordTargeter.PLAYER, KeywordTargeter.ENTITY, KeywordTargeter.BLOCK),
                new ExpectedManual((rawValue, context) -> Particle.valueOf(rawValue.toUpperCase()), "particle name"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "particle amount"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.DOUBLE, "x offset"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.DOUBLE, "y offset"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.DOUBLE, "z offset"));
    }

    @Override
    protected void call(ImmutableList<Object> arguments, RuntimeContext context) {
        Location location = null;
        KeywordTargeter rawLocation = (KeywordTargeter) arguments.get(0);

        if (rawLocation == KeywordTargeter.PLAYER) {
            location = context.getPlayer().getLocation();
        }

        if (rawLocation == KeywordTargeter.ENTITY && context instanceof DamageContext) {
            DamageContext damageContext = (DamageContext) context;
            location = damageContext.getEntity().getLocation();
        }

        if (rawLocation == KeywordTargeter.BLOCK && context instanceof InteractContextBlock) {
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