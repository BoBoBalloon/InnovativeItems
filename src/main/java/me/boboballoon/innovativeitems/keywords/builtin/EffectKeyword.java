package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordContext;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordTargeter;
import me.boboballoon.innovativeitems.keywords.keyword.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.context.DamageContext;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Class that represents a keyword in an ability config file that gives a potion effect to a selected target
 */
public class EffectKeyword extends Keyword {
    public EffectKeyword() {
        super("effect", true, false, false, false);
    }

    @Override
    public void execute(List<Object> arguments, RuntimeContext context) {
        LivingEntity target = null;
        KeywordTargeter rawTarget = (KeywordTargeter) arguments.get(0);

        if (rawTarget == KeywordTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (context instanceof DamageContext && rawTarget == KeywordTargeter.ENTITY) {
            DamageContext damageContext = (DamageContext) context;
            target = damageContext.getEntity();
        }

        if (target == null) {
            LogUtil.log(Level.WARNING, "There is not a valid living entity currently present on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability! Are you sure the target and trigger are valid together?");
            return;
        }

        PotionEffect effect = (PotionEffect) arguments.get(1);

        target.addPotionEffect(effect);
    }

    @Override
    @Nullable
    public List<Object> load(KeywordContext context) {
        String[] raw = context.getContext();
        List<Object> args = new ArrayList<>();

        KeywordTargeter target = KeywordTargeter.getFromIdentifier(raw[0]);

        if (target == null) {
            LogUtil.log(Level.WARNING, "There is not a valid target entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        if (target != KeywordTargeter.PLAYER && target != KeywordTargeter.ENTITY) {
            LogUtil.log(Level.WARNING, "There is not a valid target entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        args.add(target);

        PotionEffectType effectType = PotionEffectType.getByName(raw[1].toUpperCase());

        if (effectType == null) {
            LogUtil.log(Level.WARNING, "There is not a valid potion effect entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        int duration;
        try {
            duration = Integer.parseInt(raw[2]);
        } catch (NumberFormatException e) {
            LogUtil.log(Level.WARNING, "There is not a valid duration entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        int level;
        try {
            level = Integer.parseInt(raw[3]);
        } catch (NumberFormatException e) {
            LogUtil.log(Level.WARNING, "There is not a valid level entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        args.add(new PotionEffect(effectType, duration, level));

        return args;
    }

    @Override
    public ImmutableList<String> getValidTargeters() {
        return ImmutableList.of("?player", "?entity");
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}