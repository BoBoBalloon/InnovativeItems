package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.context.DamageContext;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordTargeter;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedValues;
import org.bukkit.entity.Player;

/**
 * Class that represents a keyword in an ability config file that damages a selected target
 */
public class FeedKeyword extends Keyword {
    public FeedKeyword() {
        super("feed",
                new ExpectedTargeters(KeywordTargeter.PLAYER, KeywordTargeter.ENTITY),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "food amount"));
    }

    @Override
    protected void call(ImmutableList<Object> arguments, RuntimeContext context) {
        Player target = null;
        KeywordTargeter rawTarget = (KeywordTargeter) arguments.get(0);

        if (rawTarget == KeywordTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (rawTarget == KeywordTargeter.ENTITY && context instanceof DamageContext) {
            DamageContext damageContext = (DamageContext) context;
            if (damageContext.getEntity() instanceof Player) {
                target = (Player) damageContext.getEntity();
            }
        }

        int rawAmount = (Integer) arguments.get(1);
        int amount = rawAmount + target.getFoodLevel();

        target.setFoodLevel(amount);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
