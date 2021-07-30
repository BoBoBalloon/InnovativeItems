package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedValues;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Class that represents a condition in an ability config file that checks if the target is holding a vanilla item
 */
public class IsHoldingItemCondition extends Condition {
    public IsHoldingItemCondition() {
        super("isholdingitem",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedManual((rawValue, context) -> Material.valueOf(rawValue.toUpperCase()), "material"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "item amount"));
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

        Material material = (Material) arguments.get(1);
        int amount = (int) arguments.get(2);

        ItemStack item = target.getInventory().getItemInMainHand();

        return item.getType() == material && item.getAmount() >= amount;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
