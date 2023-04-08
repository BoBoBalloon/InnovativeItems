package me.boboballoon.innovativeitems.ui;

import me.boboballoon.innovativeitems.util.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.function.Consumer;

/**
 * A class that represents an item in an inventory view ui that has no lore or display name
 */
public class BlankElement extends InnovativeElement {
    public BlankElement(@NotNull ItemStack stack, @Nullable Consumer<Player> clickAction, @Nullable Runnable loadAction) {
        super(blank(stack), clickAction, loadAction);
    }

    public BlankElement(@NotNull ItemStack stack, @Nullable Consumer<Player> clickAction) {
        this(stack, clickAction, null);
    }

    public BlankElement(@NotNull ItemStack stack, @Nullable Runnable loadAction) {
        this(stack, null, loadAction);
    }

    public BlankElement(@NotNull ItemStack stack) {
        this(stack, null, null);
    }

    /**
     * A method that returns a copy of the itemstack after all item flags are added and the lore and display name is removed
     *
     * @param stack the itemstack to make a copy of
     * @return copy of the itemstack after all item flags are added and the lore and display name is removed
     */
    @NotNull
    private static ItemStack blank(@NotNull ItemStack stack) {
        ItemStack copy = stack.clone();
        ItemMeta meta = copy.getItemMeta();

        meta.setDisplayName(TextUtil.format("&r"));
        meta.setLore(Collections.emptyList());
        meta.addItemFlags(ItemFlag.values());

        copy.setItemMeta(meta);
        return copy;
    }
}
