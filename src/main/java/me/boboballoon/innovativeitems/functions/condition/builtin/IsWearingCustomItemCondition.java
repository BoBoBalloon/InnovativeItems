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
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.RevisedEquipmentSlot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Class that represents a condition in an ability config file that checks if the target is wearing a custom item
 */
public class IsWearingCustomItemCondition extends Condition {
    public IsWearingCustomItemCondition() {
        super("iswearingcustomitem",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING),
                new ExpectedEnum<>(RevisedEquipmentSlot.class, slot -> slot != RevisedEquipmentSlot.HAND && slot != RevisedEquipmentSlot.OFF_HAND, "equipment slot"));
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

        InnovativeCache cache = InnovativeItems.getInstance().getItemCache();

        CustomItem customItem = cache.getItem(itemName);

        if (customItem == null) {
            LogUtil.log(LogUtil.Level.WARNING, "The provided item name on the " + this.getIdentifier() + " condition on the " + context.getAbilityName() + " ability cannot resolve a custom item!");
            return false;
        }

        RevisedEquipmentSlot slot = (RevisedEquipmentSlot) arguments.get(2);

        if (slot == RevisedEquipmentSlot.ANY) {
            return this.isWearingCustomArmor(target, customItem, cache);
        }

        CustomItem item = cache.fromItemStack(target.getInventory().getItem(slot.getSlot()));

        return customItem.equals(item);
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    /**
     * A method used to check if the player is wearing the provided custom item in any slot
     *
     * @param player the player
     * @param customItem the provided custom item
     * @param cache the item cache where all the current custom items are stored in memory
     * @return a boolean that is true when the player is wearing the provided custom item in any slot
     */
    private boolean isWearingCustomArmor(Player player, CustomItem customItem, InnovativeCache cache) {
        for (ItemStack item : player.getInventory().getArmorContents()) {
            CustomItem customItemTwo = cache.fromItemStack(item);

            if (customItemTwo == null) {
                continue;
            }

            if (customItem.equals(customItemTwo)) {
                return true;
            }
        }

        return false;
    }
}
