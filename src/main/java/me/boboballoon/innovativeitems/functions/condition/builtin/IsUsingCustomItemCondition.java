package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedEnum;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.items.CustomItem;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.RevisedEquipmentSlot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a condition in an ability config file that checks if the target is using a vanilla item
 */
public class IsUsingCustomItemCondition extends Condition {
    public IsUsingCustomItemCondition() {
        super("isusingcustomitem",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "item amount"),
                new ExpectedEnum<>(RevisedEquipmentSlot.class, "equipment slot"));
    }

    @Override
    protected Boolean call(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        String name = (String) arguments.get(1);
        InnovativeCache cache = InnovativeItems.getInstance().getItemCache();

        CustomItem provided = cache.getItem(name);

        if (provided == null) {
            LogUtil.log(LogUtil.Level.WARNING, "The provided item name on the " + this.getIdentifier() + " condition on the " + context.getAbilityName() + " ability cannot resolve a custom item!");
            return false;
        }

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

        int amount = (int) arguments.get(2);
        RevisedEquipmentSlot slot = (RevisedEquipmentSlot) arguments.get(3);

        for (ItemStack item : slot.getFromPlayer(target)) {
            CustomItem customItem = cache.fromItemStack(item);

            if (customItem != null && provided.getIdentifier().equals(customItem.getIdentifier()) && item.getAmount() >= amount) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
