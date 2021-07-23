package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.context.DamageContext;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordTargeter;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedValues;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.entity.LivingEntity;

/**
 * Class that represents a keyword in an ability config file that sets the health of a selected target
 */
public class SetHealthKeyword extends Keyword {
    public SetHealthKeyword() {
        super("sethealth",
                new ExpectedTargeters(KeywordTargeter.PLAYER, KeywordTargeter.ENTITY),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.DOUBLE, context -> LogUtil.log(LogUtil.Level.WARNING, "There is not a valid health value entered on the " + context.getKeyword().getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!")));
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

        double amount = (Double) arguments.get(1);

        if (amount > target.getMaxHealth()) {
            amount = target.getMaxHealth();
        } else if (amount < 0) {
            amount = 0;
        }

        target.setHealth(amount);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
