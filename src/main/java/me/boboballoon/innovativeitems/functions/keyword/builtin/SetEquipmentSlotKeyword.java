package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedEnum;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.RevisedEquipmentSlot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class that represents a keyword in an ability config file that gives a vanilla minecraft item
 */
public class SetEquipmentSlotKeyword extends Keyword {
    public SetEquipmentSlotKeyword() {
        super("setequipmentslot",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING, "item name"),
                new ExpectedEnum<>(RevisedEquipmentSlot.class, slot -> slot != RevisedEquipmentSlot.ANY, "equipment slot"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.BOOLEAN, "is custom item"));
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
        RevisedEquipmentSlot equipmentSlot = (RevisedEquipmentSlot) arguments.get(2);
        boolean isCustomItem = (boolean) arguments.get(3);

        ItemStack item = this.getItem(itemName, isCustomItem, context);

        if (item == null) {
            //silently fail
            return;
        }

        target.getInventory().setItem(equipmentSlot.getSlot(), item);
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    /**
     * Method used to get a custom itemstack or generate a new one
     */
    @Nullable
    private ItemStack getItem(String itemName, boolean isCustomItem, RuntimeContext context) {
        if (isCustomItem) {
            CustomItem customItem = InnovativeItems.getInstance().getItemCache().getItem(itemName);

            if (customItem == null) {
                LogUtil.log(LogUtil.Level.WARNING, "The provided item name on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability cannot resolve a custom item!");
                return null;
            }

            return customItem.getItemStack().clone();
        }

        Material material;
        try {
            material = Material.valueOf(itemName.toUpperCase());
        } catch (IllegalArgumentException e) {
            LogUtil.log(LogUtil.Level.WARNING, "The provided material on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability cannot resolve a valid material!");
            return null;
        }

        return new ItemStack(material);
    }
}
