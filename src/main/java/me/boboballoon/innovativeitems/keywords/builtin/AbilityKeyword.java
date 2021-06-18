package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordContext;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Class that represents a keyword in an ability config file that executes another ability
 */
public class AbilityKeyword extends Keyword {
    public AbilityKeyword() {
        super("ability", false);
    }

    @Override
    public void call(List<Object> arguments, RuntimeContext context) {
        String rawAbility = (String) arguments.get(0);

        Ability ability = InnovativeItems.getInstance().getItemCache().getAbility(rawAbility);

        if (ability == null) {
            LogUtil.log(Level.WARNING, "There is not a valid ability name entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return;
        }

        if (ability.getName().equals(context.getAbilityName())) {
            LogUtil.log(Level.WARNING, "You cannot use the " + this.getIdentifier() + " keyword to recursively call the " + context.getAbilityName() + " ability!");
            return;
        }

        if (context.getAbilityTrigger() != ability.getTrigger()) {
            LogUtil.log(Level.WARNING, "You cannot use the " + this.getIdentifier() + " keyword to execute an ability without the same trigger as the " + context.getAbilityName() + " ability!");
            return;
        }

        ability.execute(context);
    }

    @Override
    @Nullable
    public List<Object> load(KeywordContext context) {
        String[] raw = context.getContext();
        List<Object> args = new ArrayList<>();

        String rawAbility = raw[0];

        args.add(rawAbility);

        return args;
    }

    @Override
    public ImmutableList<String> getValidTargeters() {
        return ImmutableList.of();
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
