package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a keyword in an ability config file that executes another ability
 */
public class AbilityKeyword extends Keyword {
    public AbilityKeyword() {
        super("ability",
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING));
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

        if (InnovativeItems.getInstance().getConfigManager().isStrict() && !context.getAbilityTrigger().isCompatible(ability.getTrigger())) {
            LogUtil.log(LogUtil.Level.WARNING, "You cannot use the " + this.getIdentifier() + " keyword to execute an ability without the same targeters as the " + context.getAbilityName() + " ability!");
            return;
        }

        Ability currentAbility = context.getAbility();

        context.setAbility(ability);
        ability.execute(context);
        context.setAbility(currentAbility);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
