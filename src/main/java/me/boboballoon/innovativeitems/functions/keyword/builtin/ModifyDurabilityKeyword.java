package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedEnum;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.util.RevisedEquipmentSlot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a keyword in an ability config file that adds durability to an item inside an equipment slot
 */
public class ModifyDurabilityKeyword extends Keyword {
    public ModifyDurabilityKeyword() {
        super("modifydurability",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "durability amount"),
                new ExpectedEnum<>(RevisedEquipmentSlot.class, "equipment slot"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.BOOLEAN, "set durability"));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        Player target = null;
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);

        if (targeter == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (targeter == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;

            if (!(entityContext.getEntity() instanceof Player)) {
                return;
            }

            target = (Player) entityContext.getEntity();
        }

        int amount = (int) arguments.get(1);
        RevisedEquipmentSlot slot = (RevisedEquipmentSlot) arguments.get(2);
        boolean set = (boolean) arguments.get(3);

        for (ItemStack item : slot.getFromPlayer(target)) {
            if (item.getItemMeta() instanceof Damageable) {
                Damageable damageable = (Damageable) item.getItemMeta();
                damageable.setDamage(set ? item.getType().getMaxDurability() - Math.max(Math.min(item.getType().getMaxDurability(), amount), 0) : item.getType().getMaxDurability() - Math.max(Math.min(item.getType().getMaxDurability(), (item.getType().getMaxDurability() - damageable.getDamage()) + amount), 0));
                item.setItemMeta((ItemMeta) damageable);
            }
        }
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
