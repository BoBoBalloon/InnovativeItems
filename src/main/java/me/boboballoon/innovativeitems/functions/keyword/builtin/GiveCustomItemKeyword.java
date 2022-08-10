package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.InventoryUtil;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a keyword in an ability config file that gives a vanilla minecraft item
 */
public class GiveCustomItemKeyword extends Keyword {
    public GiveCustomItemKeyword() {
        super("givecustomitem",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING, "innovative item name"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "item amount", object -> {
                    int integer = (int) object;
                    return integer > 0 && integer < 1000;
                }));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        Player target = null;
        FunctionTargeter rawTarget = (FunctionTargeter) arguments.get(0);

        if (rawTarget == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (rawTarget == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;

            if (!(entityContext.getEntity() instanceof Player)) {
                return;
            }

            target = (Player) entityContext.getEntity();
        }

        String itemName = (String) arguments.get(1);

        CustomItem customItem = InnovativeItems.getInstance().getItemCache().getItem(itemName);

        if (customItem == null) {
            LogUtil.log(LogUtil.Level.WARNING, "The provided item name on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability cannot resolve a custom item!");
            return;
        }

        int amount = (int) arguments.get(2);

        InventoryUtil.giveItem(target, customItem.getItemStack(), amount);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
