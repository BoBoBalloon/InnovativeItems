package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedVarArg;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class that represents a keyword in an ability config file that executes another random ability
 */
public class RandomAbilityKeyword extends Keyword {
    public RandomAbilityKeyword() {
        super("randomability",
                new ExpectedVarArg(new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING)));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        List<String> rawAbilities = (List<String>) arguments.get(0);
        List<Ability> abilities = new ArrayList<>();

        for (String rawAbility : rawAbilities) {
            Ability ability = InnovativeItems.getInstance().getItemCache().getAbility(rawAbility);

            if (ability == null) {
                LogUtil.log(LogUtil.Level.WARNING, "There is not a valid ability name entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
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

            abilities.add(ability);
        }

        if (abilities.isEmpty()) {
            return;
        }

        int index = ThreadLocalRandom.current().nextInt(abilities.size());

        Ability currentAbility = context.getAbility();
        Ability ability = abilities.get(index);

        context.setAbility(ability);
        ability.execute(context);
        context.setAbility(currentAbility);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
