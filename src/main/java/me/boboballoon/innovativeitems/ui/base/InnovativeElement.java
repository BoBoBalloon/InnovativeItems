package me.boboballoon.innovativeitems.ui.base;

import me.boboballoon.innovativeitems.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A class that represents an item in an inventory view ui
 */
public class InnovativeElement {
    private final ItemStack stack;
    private final BiConsumer<Player, ClickType> clickAction;
    private final Runnable loadAction;

    /**
     * A static field that represents an empty element with no functionality
     */
    public static final InnovativeElement EMPTY = new InnovativeElement(new ItemStack(Material.AIR));

    public InnovativeElement(@NotNull ItemStack stack, @Nullable BiConsumer<Player, ClickType> clickAction, @Nullable Runnable loadAction) {
        this.stack = stack;
        this.clickAction = clickAction != null ? clickAction : (player, click) -> {};
        this.loadAction = loadAction != null ? loadAction : () -> {};
    }

    public InnovativeElement(@NotNull ItemStack stack, @Nullable Consumer<Player> clickAction, @Nullable Runnable loadAction) {
        this(stack, clickAction != null ? (player, click) -> clickAction.accept(player) : null, loadAction);
    }

    public InnovativeElement(@NotNull ItemStack stack, @Nullable Consumer<Player> clickAction) {
        this(stack, clickAction, null);
    }

    public InnovativeElement(@NotNull ItemStack stack, @Nullable Runnable loadAction) {
        this(stack, (BiConsumer<Player, ClickType>) null, loadAction);
    }

    public InnovativeElement(@NotNull ItemStack stack) {
        this(stack, (BiConsumer<Player, ClickType>) null, null);
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
    public final BiConsumer<Player, ClickType> getClickAction() {
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

    /**
     A util method used to easily build elements for views
     *
     * @param type the material type
     * @param display the display name (nullable)
     * @param lore the item lore (nullable)
     * @param glowing if the item should have the enchantment glowing effect
     * @param clickAction the code that should be executed when the code is ran (nullable)
     * @param loadAction the code that should be executed when the item is loaded on the view (nullable)
     * @return an element that reflects the input
     */
    @NotNull
    public static InnovativeElement build(@NotNull Material type, @Nullable String display, @Nullable List<String> lore, boolean glowing, @Nullable BiConsumer<Player, ClickType> clickAction, @Nullable Consumer<ItemStack> loadAction) {
        ItemStack stack = new ItemStack(type);
        ItemMeta meta = stack.getItemMeta();

        if (display != null) {
            meta.setDisplayName(TextUtil.format(display));
        }

        if (lore != null && !lore.isEmpty()) {
            meta.setLore(lore.stream().map(TextUtil::format).collect(Collectors.toList()));
        }

        if (glowing) {
            meta.addEnchant(Enchantment.IMPALING, 1, true);
            meta.addItemFlags(ItemFlag.values());
        }

        stack.setItemMeta(meta);

        return new InnovativeElement(stack, clickAction, loadAction != null ? () -> loadAction.accept(stack) : null);
    }

    /**
     A util method used to easily build elements for views
     *
     * @param type the material type
     * @param display the display name (nullable)
     * @param lore the item lore (nullable)
     * @param glowing if the item should have the enchantment glowing effect
     * @param clickAction the code that should be executed when the code is ran (nullable)
     * @return an element that reflects the input
     */
    @NotNull
    public static InnovativeElement build(@NotNull Material type, @Nullable String display, @Nullable List<String> lore, boolean glowing, @Nullable BiConsumer<Player, ClickType> clickAction) {
        return InnovativeElement.build(type, display, lore, glowing, clickAction, null);
    }

    /**
     A util method used to easily build elements for views
     *
     * @param type the material type
     * @param display the display name (nullable)
     * @param lore the item lore (nullable)
     * @param glowing if the item should have the enchantment glowing effect
     * @param loadAction the code that should be executed when the item is loaded on the view (nullable)
     * @return an element that reflects the input
     */
    @NotNull
    public static InnovativeElement build(@NotNull Material type, @Nullable String display, @Nullable List<String> lore, boolean glowing, @Nullable Consumer<ItemStack> loadAction) {
        return InnovativeElement.build(type, display, lore, glowing, null, loadAction);
    }

    /**
     A util method used to easily build elements for views
     *
     * @param type the material type
     * @param display the display name (nullable)
     * @param lore the item lore (nullable)
     * @param glowing if the item should have the enchantment glowing effect
     * @return an element that reflects the input
     */
    @NotNull
    public static InnovativeElement build(@NotNull Material type, @Nullable String display, @Nullable List<String> lore, boolean glowing) {
        return InnovativeElement.build(type, display, lore, glowing, null, null);
    }

    /**
     A util method used to easily build elements for views
     *
     * @param type the material type
     * @param display the display name (nullable)
     * @param lore the item lore (nullable)
     * @return an element that reflects the input
     */
    @NotNull
    public static InnovativeElement build(@NotNull Material type, @Nullable String display, @Nullable List<String> lore) {
        return InnovativeElement.build(type, display, lore, false, null, null);
    }

    /**
     A util method used to easily build elements for views
     *
     * @param type the material type
     * @param display the display name (nullable)
     * @param lore the item lore (nullable)
     * @param clickAction the code that should be executed when the code is ran (nullable)
     * @param loadAction the code that should be executed when the item is loaded on the view (nullable)
     * @return an element that reflects the input
     */
    @NotNull
    public static InnovativeElement build(@NotNull Material type, @Nullable String display, @Nullable List<String> lore, @Nullable BiConsumer<Player, ClickType> clickAction, @Nullable Consumer<ItemStack> loadAction) {
        return InnovativeElement.build(type, display, lore, false, clickAction, loadAction);
    }

    /**
     A util method used to easily build elements for views
     *
     * @param type the material type
     * @param display the display name (nullable)
     * @return an element that reflects the input
     */
    @NotNull
    public static InnovativeElement build(@NotNull Material type, @Nullable String display) {
        return InnovativeElement.build(type, display, null, false, null, null);
    }

    /**
     A util method used to easily build elements for views
     *
     * @param type the material type
     * @param display the display name (nullable)
     * @param clickAction the code that should be executed when the code is ran (nullable)
     * @param loadAction the code that should be executed when the item is loaded on the view (nullable)
     * @return an element that reflects the input
     */
    @NotNull
    public static InnovativeElement build(@NotNull Material type, @Nullable String display, @Nullable BiConsumer<Player, ClickType> clickAction, @Nullable Consumer<ItemStack> loadAction) {
        return InnovativeElement.build(type, display, null, false, clickAction, loadAction);
    }

    /**
     A util method used to easily build elements for views
     *
     * @param type the material type
     * @param clickAction the code that should be executed when the code is ran (nullable)
     * @param loadAction the code that should be executed when the item is loaded on the view (nullable)
     * @return an element that reflects the input
     */
    @NotNull
    public static InnovativeElement build(@NotNull Material type, @Nullable BiConsumer<Player, ClickType> clickAction, @Nullable Consumer<ItemStack> loadAction) {
        return InnovativeElement.build(type, null, null, false, clickAction, loadAction);
    }

    /**
     A util method used to easily build elements for views
     *
     * @param type the material type
     * @param display the display name (nullable)
     * @param clickAction the code that should be executed when the code is ran (nullable)
     * @return an element that reflects the input
     */
    @NotNull
    public static InnovativeElement build(@NotNull Material type, @Nullable String display, @Nullable BiConsumer<Player, ClickType> clickAction) {
        return InnovativeElement.build(type, display, null, false, clickAction, null);
    }

    /**
     A util method used to easily build elements for views
     *
     * @param type the material type
     * @param display the display name (nullable)
     * @param loadAction the code that should be executed when the item is loaded on the view (nullable)
     * @return an element that reflects the input
     */
    @NotNull
    public static InnovativeElement build(@NotNull Material type, @Nullable String display, @Nullable Consumer<ItemStack> loadAction) {
        return InnovativeElement.build(type, display, null, false, null, loadAction);
    }
}