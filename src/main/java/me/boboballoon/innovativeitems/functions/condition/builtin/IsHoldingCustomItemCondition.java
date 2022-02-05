package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a condition in an ability config file that checks if the target is holding a custom item
 */
public class IsHoldingCustomItemCondition extends Condition {
    public IsHoldingCustomItemCondition() {
        super("isholdingcustomitem",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "item amount"));
    }

    @Override
    protected Boolean call(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        Player target = null;
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);

        if (targeter == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (targeter == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;

            if (!(entityContext.getEntity() instanceof Player)) {
                return false;
            }

            target = (Player) entityContext.getEntity();
        }

        String itemName = (String) arguments.get(1);

        InnovativeCache cache = InnovativeItems.getInstance().getItemCache();

        CustomItem customItem = cache.getItem(itemName);

        if (customItem == null) {
            LogUtil.log(LogUtil.Level.WARNING, "The provided item name on the " + this.getIdentifier() + " condition on the " + context.getAbilityName() + " ability cannot resolve a custom item!");
            return false;
        }

        ItemStack itemStack = target.getInventory().getItemInMainHand();
        CustomItem item = cache.fromItemStack(itemStack);

        if (item == null) {
            return false;
        }

        int amount = (int) arguments.get(2);

        return customItem.equals(item) && itemStack.getAmount() >= amount;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
