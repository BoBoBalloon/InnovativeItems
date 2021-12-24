package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedEnum;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
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
                new ExpectedEnum<>(Material.class, "material"),
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
