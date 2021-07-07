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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class that represents a keyword in an ability config file that executes another random ability
 */
public class RandomAbilityKeyword extends Keyword {
    public RandomAbilityKeyword() {
        super("randomability", false);
    }

    @Override
    protected void call(List<Object> arguments, RuntimeContext context) {
        List<String> rawAbilities = (List<String>) arguments.get(0);
        List<Ability> abilities = new ArrayList<>();

        for (String rawAbility : rawAbilities) {
            Ability ability = InnovativeItems.getInstance().getItemCache().getAbility(rawAbility);

            if (ability == null) {
                LogUtil.log(LogUtil.Level.WARNING, "There is not a valid ability name entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
                return;
            }

            if (ability.getName().equals(context.getAbilityName())) {
                LogUtil.log(LogUtil.Level.WARNING, "You cannot use the " + this.getIdentifier() + " keyword to recursively call the " + context.getAbilityName() + " ability!");
                return;
            }

            if (context.getAbilityTrigger() != ability.getTrigger()) {
                LogUtil.log(LogUtil.Level.WARNING, "You cannot use the " + this.getIdentifier() + " keyword to execute an ability without the same trigger as the " + context.getAbilityName() + " ability!");
                return;
            }

            abilities.add(ability);
        }

        int index = ThreadLocalRandom.current().nextInt(abilities.size());

        Ability ability = abilities.get(index);

        ability.execute(context);
    }

    @Override
    @Nullable
    public List<Object> load(KeywordContext context) {
        String[] raw = context.getContext();
        List<Object> args = new ArrayList<>();

        String[] rawAbilities = raw[0].split(";");

        args.add(Arrays.asList(rawAbilities));

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
