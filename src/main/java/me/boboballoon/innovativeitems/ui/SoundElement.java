package me.boboballoon.innovativeitems.ui;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A class that represents an item in an inventory view ui that has no lore or display name
 */
public class SoundElement extends InnovativeElement {
    public SoundElement(@NotNull ItemStack stack, @NotNull Sound sound, @Nullable Consumer<Player> clickAction, @Nullable Runnable loadAction) {
        super(stack, player -> {
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);

            if (clickAction != null) {
                clickAction.accept(player);
            }
        }, loadAction);
    }

    public SoundElement(@NotNull ItemStack stack, @NotNull Sound sound, @Nullable Consumer<Player> clickAction) {
        this(stack, sound, clickAction, null);
    }

    public SoundElement(@NotNull ItemStack stack, @NotNull Sound sound, @Nullable Runnable loadAction) {
        this(stack, sound, null, loadAction);
    }

    public SoundElement(@NotNull ItemStack stack, @NotNull Sound sound) {
        this(stack, sound, null, null);
    }
}
