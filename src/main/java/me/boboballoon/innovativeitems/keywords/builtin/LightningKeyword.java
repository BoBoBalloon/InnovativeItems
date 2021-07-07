package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.context.DamageContext;
import me.boboballoon.innovativeitems.keywords.context.InteractContextBlock;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordContext;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordTargeter;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class that represents a keyword in an ability config file that spawns lightning at a selected target
 */
public class LightningKeyword extends Keyword {
    public LightningKeyword() {
        super("lightning", true, false);
    }

    @Override
    protected void call(List<Object> arguments, RuntimeContext context) {
        Location location = null;
        KeywordTargeter rawLocation = (KeywordTargeter) arguments.get(0);

        if (rawLocation == KeywordTargeter.PLAYER) {
            location = context.getPlayer().getLocation();
        }

        if (context instanceof DamageContext && rawLocation == KeywordTargeter.ENTITY) {
            DamageContext damageContext = (DamageContext) context;
            location = damageContext.getEntity().getLocation();
        }

        if (context instanceof InteractContextBlock && rawLocation == KeywordTargeter.BLOCK) {
            InteractContextBlock interactContext = (InteractContextBlock) context;
            location = interactContext.getBlock().getLocation();
        }

        if (location == null) {
            LogUtil.log(LogUtil.Level.WARNING, "There is not a location currently present on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability! Are you sure the target and trigger are valid together?");
            return;
        }

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
    @Nullable
    public List<Object> load(KeywordContext context) {
        String[] raw = context.getContext();
        List<Object> args = new ArrayList<>();

        KeywordTargeter location = KeywordTargeter.getFromIdentifier(raw[0]);

        if (location == null) {
            LogUtil.log(LogUtil.Level.WARNING, "There is not a valid target entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        if (location != KeywordTargeter.PLAYER && location != KeywordTargeter.ENTITY && location != KeywordTargeter.BLOCK) {
            LogUtil.log(LogUtil.Level.WARNING, "There is not a valid target entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        args.add(location);

        double damage;
        try {
            damage = Double.parseDouble(raw[1]);
        } catch (NumberFormatException e) {
            LogUtil.log(LogUtil.Level.WARNING, "There is not a valid damage amount entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        args.add(damage);

        return args;
    }

    @Override
    public ImmutableList<String> getValidTargeters() {
        return ImmutableList.of("?player", "?entity", "?block");
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}