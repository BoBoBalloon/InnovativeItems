package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a keyword in an ability config file that gives a potion effect to a selected target
 */
public class EffectKeyword extends Keyword {
    public EffectKeyword() {
        super("effect",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedManual((rawValue, context) -> PotionEffectType.getByName(rawValue.toUpperCase()), "potion effect"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "duration"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "level"));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        LivingEntity target = null;
        FunctionTargeter rawTarget = (FunctionTargeter) arguments.get(0);

        if (rawTarget == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (rawTarget == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;
            target = entityContext.getEntity();
        }

        PotionEffectType type = (PotionEffectType) arguments.get(1);
        int duration = (int) arguments.get(2);
        int level = (int) arguments.get(3);

        PotionEffect effect = new PotionEffect(type, duration, level);

        target.addPotionEffect(effect);
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}