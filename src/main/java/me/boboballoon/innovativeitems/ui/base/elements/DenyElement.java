package me.boboballoon.innovativeitems.ui.base.elements;

import me.boboballoon.innovativeitems.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A class that represents an item in an inventory view ui that plays a low note and has a click action
 */
public class DenyElement extends SoundElement {
    private static final ItemStack BOTH = build(true, true);
    private static final ItemStack BOLD = build(true, false);
    private static final ItemStack ITALICS = build(false, true);
    private static final ItemStack NONE = build(false, false);

    public DenyElement(boolean bold, boolean italics, @Nullable Consumer<Player> clickAction) {
        super(bold && italics ? BOTH : bold ? BOLD : italics ? ITALICS : NONE, Sound.BLOCK_NOTE_BLOCK_BASS, clickAction, null);
    }

    public DenyElement(@Nullable Consumer<Player> clickAction) {
        this(false, false, clickAction);
    }

    public DenyElement() {
        this(null);
    }

    /**
     * A method used to build an instance of the confirm element itemstack
     *
     * @param bold if the confirm button display name should be bold
     * @param italics if the confirm button should be italicized
     * @return an instance of the item
     */
    @NotNull
    private static ItemStack build(boolean bold, boolean italics) {
        ItemStack stack = new ItemStack(Material.RED_TERRACOTTA);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(TextUtil.format("&r&c" + (bold ? "&l" : "") + (italics ? "&o" : "") + "Deny"));

        stack.setItemMeta(meta);
        return stack;
    }
}
