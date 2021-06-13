package me.boboballoon.innovativeitems.keywords.builtin;

import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordContext;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordTargeter;
import me.boboballoon.innovativeitems.keywords.keyword.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.context.DamageContext;
import me.boboballoon.innovativeitems.keywords.keyword.context.InteractContextBlock;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Class that represents a keyword in an ability config file that fires a particle at a selected target
 */
public class ParticleKeyword extends Keyword {
    public ParticleKeyword() {
        super("particle", true, false, false, false, false, false);
    }

    @Override
    public void execute(List<Object> arguments, RuntimeContext context) {
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
            LogUtil.log(Level.WARNING, "There is not a location currently present on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability! Are you sure the target and trigger are valid together?");
            return;
        }

        Particle particle = (Particle) arguments.get(1);
        int amount = (Integer) arguments.get(2);
        double xOffset = (Double) arguments.get(3);
        double yOffset = (Double) arguments.get(4);
        double zOffset = (Double) arguments.get(5);

        location.getWorld().spawnParticle(particle, location, amount, xOffset, yOffset, zOffset);
    }

    @Override
    @Nullable
    public List<Object> load(KeywordContext context) {
        String[] raw = context.getContext();
        List<Object> args = new ArrayList<>();

        KeywordTargeter location = KeywordTargeter.getFromIdentifier(raw[0]);

        if (location == null) {
            LogUtil.log(Level.WARNING, "There is not a valid target entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        if (location != KeywordTargeter.PLAYER && location != KeywordTargeter.ENTITY && location != KeywordTargeter.BLOCK) {
            LogUtil.log(Level.WARNING, "There is not a valid target entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        args.add(location);

        Particle particle;
        try {
            particle = Particle.valueOf(raw[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            LogUtil.log(Level.WARNING, "There is not a valid particle name entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        args.add(particle);

        int amount;
        try {
            amount = Integer.parseInt(raw[2]);
        } catch (NumberFormatException e) {
            LogUtil.log(Level.WARNING, "There is not a valid particle amount entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        args.add(amount);

        double xOffset;
        try {
            xOffset = Double.parseDouble(raw[3]);
        } catch (NumberFormatException e) {
            LogUtil.log(Level.WARNING, "There is not a valid x offset entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        args.add(xOffset);

        double yOffset;
        try {
            yOffset = Double.parseDouble(raw[4]);
        } catch (NumberFormatException e) {
            LogUtil.log(Level.WARNING, "There is not a valid y offset entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        args.add(yOffset);

        double zOffset;
        try {
            zOffset = Double.parseDouble(raw[5]);
        } catch (NumberFormatException e) {
            LogUtil.log(Level.WARNING, "There is not a valid z offset entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        args.add(zOffset);

        return args;
    }
}