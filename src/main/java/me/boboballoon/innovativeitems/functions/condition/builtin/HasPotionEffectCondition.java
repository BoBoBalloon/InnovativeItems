package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a condition in an ability config file that checks if the target is sneaking
 */
public class HasPotionEffectCondition extends Condition {
    public HasPotionEffectCondition() {
        super("haspotioneffect",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedManual((rawValue, context) -> PotionEffectType.getByName(rawValue), "potion effect"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "level requirement", object -> {
                    int requirement = (int) object;
                    return requirement >= 0;
                }),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.CHAR, "operation", object -> {
                    char operation = (char) object;
                    return operation == '>' || operation == '<' || operation == '=';
                }));
    }

    @Override
    protected Boolean call(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        LivingEntity target = null;
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);

        if (targeter == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (targeter == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;

            target = entityContext.getEntity();
        }

        PotionEffectType potionEffectType = (PotionEffectType) arguments.get(1);
        PotionEffect effect = target.getPotionEffect(potionEffectType);

        if (effect == null) {
            return false;
        }

        int requirement = (int) arguments.get(2);
        char operation = (char) arguments.get(3);
        int level = effect.getAmplifier() + 1; //level minus one is the amplifier so the effect strength two would have an amplifier of one

        return (operation == '=' && level == requirement) || (operation == '>' && level > requirement) || (operation == '<' && level < requirement);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
