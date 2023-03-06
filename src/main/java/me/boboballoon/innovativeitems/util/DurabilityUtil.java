package me.boboballoon.innovativeitems.util;

import de.tr7zw.nbtapi.NBTItem;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * A util class used to organize durability based functions
 */
public final class DurabilityUtil {
    /**
     * Constructor to prevent people from using this util class in an object oriented way
     */
    private DurabilityUtil() {
    }

    /**
     * A method used to set the durability of an item
     *
     * @param stack  the itemstack whose durability you want to modify
     * @param amount the durability to set the item to
     */
    public static void setDurability(@NotNull ItemStack stack, int amount) {
        if (!(stack.getItemMeta() instanceof Damageable)) {
            return;
        }

        if (amount <= 0) {
            stack.setAmount(0);
            return;
        }

        Damageable damageable = (Damageable) stack.getItemMeta();
        CustomItem item = InnovativeItems.getInstance().getItemCache().fromItemStack(stack);

        int damage;
        if (item != null) {
            double ratio = (double) Math.max(Math.min(item.getMaxDurability(), amount), 1) / item.getMaxDurability();
            int newDurability = (int) (stack.getType().getMaxDurability() * ratio); //truncate double
            damage = stack.getType().getMaxDurability() - newDurability;
        } else {
            damage = stack.getType().getMaxDurability() - Math.max(Math.min(stack.getType().getMaxDurability(), amount), 0);
        }

        damageable.setDamage(damage);
        stack.setItemMeta((ItemMeta) damageable);

        if (item != null) {
            NBTItem nbt = new NBTItem(stack, true);
            nbt.setInteger("innovativeplugin-customitem-durability", Math.min(amount, item.getMaxDurability()));
        }
    }

    /**
     * A method used to get the durability of an item
     *
     * @param stack the itemstack to get the durability from
     * @return the amount of durability the item has, -1 if the item is not damageable
     */
    public static int getDurability(@NotNull ItemStack stack) {
        if (!(stack.getItemMeta() instanceof Damageable)) {
            return -1;
        }

        Damageable damageable = (Damageable) stack.getItemMeta();
        NBTItem nbt = new NBTItem(stack);
        CustomItem item = InnovativeItems.getInstance().getItemCache().fromNBTItem(nbt);
        int nativeDurability = item != null ? nbt.getInteger("innovativeplugin-customitem-durability") : stack.getType().getMaxDurability() - damageable.getDamage();

        if (item != null &&
                (stack.getType().getMaxDurability() - damageable.getDamage()) / stack.getType().getMaxDurability() != nativeDurability / item.getMaxDurability()) {
            double ratio = (double) (stack.getType().getMaxDurability() - damageable.getDamage()) / stack.getType().getMaxDurability();
            int real = (int) (item.getMaxDurability() * ratio); //truncate double
            DurabilityUtil.setDurability(stack, real);
            return real;
        }

        return nativeDurability;
    }
}
