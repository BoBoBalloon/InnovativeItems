package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.context.DamageContext;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordContext;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordTargeter;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a keyword in an ability config file that sets the health of a selected target
 */
public class SetHealthKeyword extends Keyword {
    public SetHealthKeyword() {
        super("sethealth", true, false);
    }

    @Override
    protected void call(List<Object> arguments, RuntimeContext context) {
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
            LogUtil.log(LogUtil.Level.WARNING, "There is not a valid living entity currently present on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability! Are you sure the target and trigger are valid together?");
            return;
        }

        double amount = (Double) arguments.get(1);

        if (amount > target.getMaxHealth()) {
            amount = target.getMaxHealth();
        } else if (amount < 0) {
            amount = 0;
        }

        target.setHealth(amount);
    }

    @Override
    @Nullable
    public List<Object> load(KeywordContext context) {
        String[] raw = context.getContext();
        List<Object> args = new ArrayList<>();

        KeywordTargeter rawTarget = KeywordTargeter.getFromIdentifier(raw[0]);

        if (rawTarget == null) {
            LogUtil.log(LogUtil.Level.WARNING, "There is not a valid target entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        if (rawTarget != KeywordTargeter.PLAYER && rawTarget != KeywordTargeter.ENTITY) {
            LogUtil.log(LogUtil.Level.WARNING, "There is not a valid target entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        args.add(rawTarget);

        double amount;
        try {
            amount = Double.parseDouble(raw[1]);
        } catch (NumberFormatException e) {
            LogUtil.log(LogUtil.Level.WARNING, "There is not a valid health value entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        args.add(amount);

        return args;
    }

    @Override
    public ImmutableList<String> getValidTargeters() {
        return ImmutableList.of("?player", "?entity");
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
