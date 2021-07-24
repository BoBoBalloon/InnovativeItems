package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.keywords.context.DamageContext;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordTargeter;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedValues;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Class that represents a keyword in an ability config file that gives a vanilla minecraft item
 */
public class GiveCustomItemKeyword extends Keyword {
    public GiveCustomItemKeyword() {
        super("givecustomitem",
                new ExpectedTargeters(KeywordTargeter.PLAYER, KeywordTargeter.ENTITY),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.STRING, "innovative item name"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "item amount"));
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

        String itemName = (String) arguments.get(1);

        CustomItem customItem = InnovativeItems.getInstance().getItemCache().getItem(itemName);

        if (customItem == null) {
            LogUtil.log(LogUtil.Level.WARNING, "The provided item name on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability cannot resolve a custom item!");
            return;
        }

        int amount = (Integer) arguments.get(2);

        ItemStack item = customItem.getItemStack().clone();
        item.setAmount(amount);

        target.getInventory().addItem(item);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
