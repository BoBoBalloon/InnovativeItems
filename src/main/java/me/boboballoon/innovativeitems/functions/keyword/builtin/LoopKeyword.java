package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a keyword in an ability config file that executes another ability
 */
public class LoopKeyword extends Keyword {
    public LoopKeyword() {
        super("loop",
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "loops", raw -> (int) raw > 0));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        String rawAbility = (String) arguments.get(0);

        Ability ability = InnovativeItems.getInstance().getItemCache().getAbility(rawAbility);

        if (ability == null) {
            LogUtil.logFunctionError(LogUtil.Level.WARNING, "ability name", this.getIdentifier(), "keyword", context.getAbilityName());
            return;
        }

        if (ability.getIdentifier().equals(context.getAbilityName())) {
            LogUtil.log(LogUtil.Level.WARNING, "You cannot use the " + this.getIdentifier() + " keyword to recursively call the " + context.getAbilityName() + " ability!");
            return;
        }

        if (InnovativeItems.getInstance().getConfigManager().isStrict() && !AbilityTrigger.isCompatible(context.getAbilityTrigger(), ability.getTrigger())) {
            LogUtil.log(LogUtil.Level.WARNING, "You cannot use the " + this.getIdentifier() + " keyword to execute an ability without the same targeters as the " + context.getAbilityName() + " ability!");
            return;
        }

        int loops = (int) arguments.get(1);

        Ability currentAbility = context.getAbility();

        context.setAbility(ability);
        for (int i = 0; i < loops; i++) {
            ability.execute(context);
        }
        context.setAbility(currentAbility);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
