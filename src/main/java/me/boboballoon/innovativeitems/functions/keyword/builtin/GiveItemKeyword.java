package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.context.DamageContext;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedValues;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Class that represents a keyword in an ability config file that gives a vanilla minecraft item
 */
public class GiveItemKeyword extends Keyword {
    public GiveItemKeyword() {
        super("giveitem",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedManual((rawValue, context) -> Material.valueOf(rawValue.toUpperCase()), "material"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "item amount"));
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

        Material material = (Material) arguments.get(1);
        int amount = (int) arguments.get(2);

        ItemStack item = new ItemStack(material, amount);

        target.getInventory().addItem(item);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
