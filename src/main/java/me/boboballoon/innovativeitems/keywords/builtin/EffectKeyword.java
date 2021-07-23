package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.context.DamageContext;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordTargeter;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedManualSophisticated;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedValues;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Class that represents a keyword in an ability config file that gives a potion effect to a selected target
 */
public class EffectKeyword extends Keyword {
    public EffectKeyword() {
        super("effect",
                new ExpectedTargeters(KeywordTargeter.PLAYER, KeywordTargeter.ENTITY),
                new ExpectedManualSophisticated((rawValue, context) -> PotionEffectType.getByName(rawValue.toUpperCase()), context -> LogUtil.log(LogUtil.Level.WARNING, "There is not a valid potion effect entered on the " + context.getKeyword().getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!")),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, context -> LogUtil.log(LogUtil.Level.WARNING, "There is not a valid duration entered on the " + context.getKeyword().getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!")),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, context -> LogUtil.log(LogUtil.Level.WARNING, "There is not a valid level entered on the " + context.getKeyword().getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!")));
    }

    @Override
    protected void call(ImmutableList<Object> arguments, RuntimeContext context) {
        LivingEntity target = null;
        KeywordTargeter rawTarget = (KeywordTargeter) arguments.get(0);

        if (rawTarget == KeywordTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (rawTarget == KeywordTargeter.ENTITY && context instanceof DamageContext) {
            DamageContext damageContext = (DamageContext) context;
            target = damageContext.getEntity();
        }

        /*
        if (target == null) {
            LogUtil.log(LogUtil.Level.WARNING, "There is not a valid living entity currently present on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability! Are you sure the target and trigger are valid together?");
            return;
        }
        */

        PotionEffectType type = (PotionEffectType) arguments.get(1);
        int duration = (Integer) arguments.get(2);
        int level = (Integer) arguments.get(3);

        PotionEffect effect = new PotionEffect(type, duration, level);

        target.addPotionEffect(effect);
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}