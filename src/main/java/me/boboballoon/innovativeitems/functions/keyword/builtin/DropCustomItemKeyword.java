package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedValues;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.BlockContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 * Class that represents a keyword in an ability config file that drops a custom minecraft item on the ground
 */
public class DropCustomItemKeyword extends Keyword {
    public DropCustomItemKeyword() {
        super("dropcustomitem",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY, FunctionTargeter.BLOCK),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.STRING),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "item amount", object -> {
                    int integer = (int) object;
                    return integer > 0;
                }));
    }

    @Override
    protected void calling(ImmutableList<Object> arguments, RuntimeContext context) {
        Location target = null;
        FunctionTargeter rawTarget = (FunctionTargeter) arguments.get(0);

        if (rawTarget == FunctionTargeter.PLAYER) {
            target = context.getPlayer().getLocation();
        }

        if (rawTarget == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;
            target = entityContext.getEntity().getLocation();
        }

        if (rawTarget == FunctionTargeter.BLOCK && context instanceof BlockContext) {
            BlockContext blockContext = (BlockContext) context;
            target = blockContext.getBlock().getLocation();
        }

        String itemName = (String) arguments.get(1);

        CustomItem customItem = InnovativeItems.getInstance().getItemCache().getItem(itemName);

        if (customItem == null) {
            LogUtil.log(LogUtil.Level.WARNING, "The provided item name on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability cannot resolve a custom item!");
            return;
        }

        int amount = (int) arguments.get(2);

        ItemStack item = customItem.getItemStack().clone();
        item.setAmount(amount);

        target.getWorld().dropItemNaturally(target, item);
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
