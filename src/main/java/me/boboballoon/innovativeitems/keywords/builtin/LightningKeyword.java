package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.context.DamageContext;
import me.boboballoon.innovativeitems.keywords.context.InteractContextBlock;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordTargeter;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedValues;
import me.boboballoon.innovativeitems.util.LogUtil;
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
                new ExpectedTargeters(KeywordTargeter.PLAYER, KeywordTargeter.ENTITY, KeywordTargeter.BLOCK),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.DOUBLE, context -> LogUtil.log(LogUtil.Level.WARNING, "There is not a valid damage amount entered on the " + context.getKeyword().getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!")));
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

        location.getWorld().strikeLightningEffect(location);

        double damage = (Double) arguments.get(1);

        if (damage <= 0) {
            return;
        }

        Collection<Entity> entities = location.getWorld().getNearbyEntities(location, 5, 5, 5, entity -> entity instanceof Damageable);

        if (entities.isEmpty()) {
            return;
        }

        entities.forEach(entity -> {
            Damageable damageable = (Damageable) entity;
            damageable.damage(damage);
        });
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}