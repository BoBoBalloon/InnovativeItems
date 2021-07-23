package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.context.DamageContext;
import me.boboballoon.innovativeitems.keywords.context.InteractContextBlock;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordTargeter;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedManualSophisticated;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedValues;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Location;
import org.bukkit.Particle;

/**
 * Class that represents a keyword in an ability config file that fires a particle at a selected target
 */
public class ParticleKeyword extends Keyword {
    public ParticleKeyword() {
        super("particle",
                new ExpectedTargeters(KeywordTargeter.PLAYER, KeywordTargeter.ENTITY, KeywordTargeter.BLOCK),
                new ExpectedManualSophisticated((rawValue, context) -> {
                    try {
                        return Particle.valueOf(rawValue.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        LogUtil.log(LogUtil.Level.WARNING, "There is not a valid particle name entered on the " + context.getKeyword().getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
                        return null;
                    }
                }, context -> LogUtil.log(LogUtil.Level.WARNING, "There is not a valid particle name entered on the " + context.getKeyword().getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!")),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, context -> LogUtil.log(LogUtil.Level.WARNING, "There is not a valid particle amount entered on the " + context.getKeyword().getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!")),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.DOUBLE, context -> LogUtil.log(LogUtil.Level.WARNING, "There is not a valid x offset entered on the " + context.getKeyword().getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!")),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.DOUBLE, context -> LogUtil.log(LogUtil.Level.WARNING, "There is not a valid y offset entered on the " + context.getKeyword().getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!")),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.DOUBLE, context -> LogUtil.log(LogUtil.Level.WARNING, "There is not a valid z offset entered on the " + context.getKeyword().getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!")));
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

        /*
        if (location == null) {
            LogUtil.log(LogUtil.Level.WARNING, "There is not a location currently present on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability! Are you sure the target and trigger are valid together?");
            return;
        }
         */

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