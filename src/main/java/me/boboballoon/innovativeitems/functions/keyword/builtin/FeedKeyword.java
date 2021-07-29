package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.context.DamageContext;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedValues;
import org.bukkit.entity.Player;

/**
 * Class that represents a keyword in an ability config file that damages a selected target
 */
public class FeedKeyword extends Keyword {
    public FeedKeyword() {
        super("feed",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "food amount"));
    }

    @Override
    protected void calling(ImmutableList<Object> arguments, RuntimeContext context) {
        Player target = null;
        FunctionTargeter rawTarget = (FunctionTargeter) arguments.get(0);

        if (rawTarget == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (rawTarget == FunctionTargeter.ENTITY && context instanceof DamageContext) {
            DamageContext damageContext = (DamageContext) context;

            if (!(damageContext.getEntity() instanceof Player)) {
                return;
            }

            target = (Player) damageContext.getEntity();
        }

        int rawAmount = (int) arguments.get(1);
        int amount = rawAmount + target.getFoodLevel();

        target.setFoodLevel(amount);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
