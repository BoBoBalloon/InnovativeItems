package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import de.tr7zw.nbtapi.NBTItem;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
    protected Boolean call(ImmutableList<Object> arguments, RuntimeContext context) {
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

        CustomItem customItem = InnovativeItems.getInstance().getItemCache().getItem(itemName);

        if (customItem == null) {
            LogUtil.log(LogUtil.Level.WARNING, "The provided item name on the " + this.getIdentifier() + " condition on the " + context.getAbilityName() + " ability cannot resolve a custom item!");
            return false;
        }

        ItemStack item = target.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            return false;
        }

        NBTItem nbtItem = new NBTItem(item);

        if (!nbtItem.hasKey("innovativeplugin-customitem")) {
            return false;
        }

        String key = nbtItem.getString("innovativeplugin-customitem-id");
        int amount = (int) arguments.get(2);

        return customItem.getIdentifier().equals(key) && item.getAmount() >= amount;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
