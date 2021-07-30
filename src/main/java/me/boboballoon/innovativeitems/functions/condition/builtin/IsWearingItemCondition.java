package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.DamageContext;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.util.RevisedEquipmentSlot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Class that represents a condition in an ability config file that checks if the target is wearing a vanilla item
 */
public class IsWearingItemCondition extends Condition {
    public IsWearingItemCondition() {
        super("iswearingitem",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedManual((rawValue, context) -> Material.valueOf(rawValue.toUpperCase()), "material"),
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

        if (targeter == FunctionTargeter.ENTITY && context instanceof DamageContext) {
            DamageContext damageContext = (DamageContext) context;

            if (!(damageContext.getEntity() instanceof Player)) {
                return false;
            }

            target = (Player) damageContext.getEntity();
        }

        Material material = (Material) arguments.get(1);
        RevisedEquipmentSlot slot = (RevisedEquipmentSlot) arguments.get(2);

        PlayerInventory inventory = target.getInventory();

        if (slot != RevisedEquipmentSlot.ANY) {
            ItemStack item = inventory.getItem(slot.getSlot());
            return item != null && item.getType() == material;
        }

        boolean value = false;
        for (ItemStack item : inventory.getArmorContents()) {
            if (item == null) {
                continue;
            }

            if (item.getType() == material) {
                value = true;
                break;
            }
        }

        return value;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
