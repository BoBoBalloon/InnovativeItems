package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.context.DamageContext;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordTargeter;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedValues;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Class that represents a keyword in an ability config file that removes the item in the targets hand
 */
public class RemoveHeldItemKeyword extends Keyword {
    public RemoveHeldItemKeyword() {
        super("removehelditem",
                new ExpectedTargeters(KeywordTargeter.PLAYER, KeywordTargeter.ENTITY),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "amount"));
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

            if (!(damageContext.getEntity() instanceof Player)) {
                return;
            }

            target = (Player) damageContext.getEntity();
        }

        int amountToRemove = (Integer) arguments.get(1);

        ItemStack item = target.getInventory().getItemInMainHand();

        int amount = item.getAmount() - amountToRemove;

        item.setAmount(amount);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
