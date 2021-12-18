package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import de.tr7zw.nbtapi.NBTItem;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
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
                new ExpectedManual((rawValue, context) -> {
                    RevisedEquipmentSlot slot = RevisedEquipmentSlot.valueOf(rawValue.toUpperCase());
                    if (slot != RevisedEquipmentSlot.HAND && slot != RevisedEquipmentSlot.OFF_HAND) {
                        return slot;
                    } else {
                        return null;
                    }
                }, "equipment slot"));
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

        RevisedEquipmentSlot slot = (RevisedEquipmentSlot) arguments.get(2);

        if (slot == RevisedEquipmentSlot.ANY) {
            return this.isWearingCustomArmor(target, customItem);
        }

        ItemStack item = target.getInventory().getItem(slot.getSlot());

        if (item == null) {
            return false;
        }

        NBTItem nbtItem = new NBTItem(item);

        if (!nbtItem.hasKey("innovativeplugin-customitem")) {
            return false;
        }

        String key = nbtItem.getString("innovativeplugin-customitem-id");

        return customItem.getIdentifier().equals(key);
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
     * @return a boolean that is true when the player is wearing the provided custom item in any slot
     */
    private boolean isWearingCustomArmor(Player player, CustomItem customItem) {
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item == null) {
                continue;
            }

            NBTItem nbtItem = new NBTItem(item);

            if (!nbtItem.hasKey("innovativeplugin-customitem")) {
                continue;
            }

            String key = nbtItem.getString("innovativeplugin-customitem-id");

            if (customItem.getIdentifier().equals(key)) {
                return true;
            }
        }

        return false;
    }
}
