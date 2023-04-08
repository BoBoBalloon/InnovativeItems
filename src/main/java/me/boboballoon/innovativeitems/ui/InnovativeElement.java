package me.boboballoon.innovativeitems.ui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A class that represents an item in an inventory view ui
 */
public class InnovativeElement {
    private final ItemStack stack;
    private final Consumer<Player> clickAction;
    private final Runnable loadAction;

    /**
     * A static field that represents an empty element with no functionality
     */
    public static final InnovativeElement EMPTY = new InnovativeElement(new ItemStack(Material.AIR));

    public InnovativeElement(@NotNull ItemStack stack, @Nullable Consumer<Player> clickAction, @Nullable Runnable loadAction) {
        this.stack = stack;
        this.clickAction = clickAction != null ? clickAction : player -> {};
        this.loadAction = loadAction != null ? loadAction : () -> {};
    }

    public InnovativeElement(@NotNull ItemStack stack, @Nullable Consumer<Player> clickAction) {
        this(stack, clickAction, null);
    }

    public InnovativeElement(@NotNull ItemStack stack, @Nullable Runnable loadAction) {
        this(stack, null, loadAction);
    }

    public InnovativeElement(@NotNull ItemStack stack) {
        this(stack, null, null);
    }

    /**
     * A method that returns the underlying itemstack
     *
     * @return the underlying itemstack
     */
    @NotNull
    public final ItemStack getStack() {
        return this.stack;
    }

    /**
     * A method that returns the click action of the element
     *
     * @return the click action of the element
     */
    @NotNull
    public final Consumer<Player> getClickAction() {
        return this.clickAction;
    }

    /**
     * A method that returns the load action of the element
     *
     * @return the load action of the element
     */
    @NotNull
    public final Runnable getLoadAction() {
        return this.loadAction;
    }
}
