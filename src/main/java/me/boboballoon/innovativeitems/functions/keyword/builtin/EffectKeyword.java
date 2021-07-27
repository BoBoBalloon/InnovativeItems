package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.context.DamageContext;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedValues;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Class that represents a keyword in an ability config file that gives a potion effect to a selected target
 */
public class EffectKeyword extends Keyword {
    public EffectKeyword() {
        super("effect",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedManual((rawValue, context) -> PotionEffectType.getByName(rawValue.toUpperCase()), "potion effect"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "duration"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "level"));
    }

    @Override
    protected void calling(ImmutableList<Object> arguments, RuntimeContext context) {
        LivingEntity target = null;
        FunctionTargeter rawTarget = (FunctionTargeter) arguments.get(0);

        if (rawTarget == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (rawTarget == FunctionTargeter.ENTITY && context instanceof DamageContext) {
            DamageContext damageContext = (DamageContext) context;
            target = damageContext.getEntity();
        }

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