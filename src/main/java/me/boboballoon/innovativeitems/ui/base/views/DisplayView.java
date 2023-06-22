package me.boboballoon.innovativeitems.ui.base.views;

import me.boboballoon.innovativeitems.ui.base.InnovativeElement;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.ResponseUtil;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A class that represents a view that displays a collection to a user in the form of {@link InnovativeElement}
 */
public class DisplayView<T> extends PagedBorderedView {
    private final Collection<T> collection;
    private final Function<T, ItemStack> map;
    private final BiConsumer<Player, T> clickAction;
    private final BiFunction<T, String, Boolean> filter;
    private String filterInput;

    private static final int ROWS = 5; //two for the border
    private static final List<List<InnovativeElement>> EMPTY = empty();

    public DisplayView(@NotNull String title, @NotNull Collection<T> collection, @NotNull Function<T, ItemStack> map, @NotNull BiConsumer<Player, T> clickAction, @Nullable BiFunction<T, String, Boolean> filter) {
        super(Material.GRAY_STAINED_GLASS_PANE, title, DisplayView.EMPTY);
        this.collection = collection;
        this.map = map;
        this.clickAction = clickAction;
        this.filter = filter;

        this.addOnSetPagesListener(page -> buildView());

        if (this.filter == null) {
            this.setPages(DisplayView.EMPTY);
            return;
        }

        this.setBottomLeft(InnovativeElement.build(Material.HOPPER, (player, clickType) -> {
            if (clickType == ClickType.RIGHT) {
                this.filterInput = null;
                this.setPages(DisplayView.EMPTY);
                return;
            }

            player.closeInventory();

            boolean success = ResponseUtil.input("Please enter the filter you would like to apply to the view! Type &r&ccancel&r&f to end the prompt.", player, response -> {
                if (response == null) {
                    this.open(player);
                    return;
                }

                this.filterInput = response;
                this.setPages(DisplayView.EMPTY);
                this.open(player);
            });

            if (!success) {
                LogUtil.logUnblocked(LogUtil.Level.SEVERE, "An error occurred asking for user input for " + player.getName() +  ". Please contact the developer");
                TextUtil.sendMessage(player, "&r&cAn internal error occurred.");
                this.open(player);
            }
        }, stack -> {
            ItemMeta meta = stack.getItemMeta();

            meta.setDisplayName(TextUtil.format(this.filterInput != null ? "&r&fFilter: " + this.filterInput : "&r&fFilter"));
            meta.setLore(Collections.singletonList(TextUtil.format("&r&fRight click to clear the filter")));

            if (this.filterInput != null) {
                meta.addEnchant(Enchantment.IMPALING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                meta.removeEnchant(Enchantment.IMPALING);
            }

            stack.setItemMeta(meta);
        }));

        this.setPages(DisplayView.EMPTY);
    }

    public DisplayView(@NotNull String title, @NotNull Collection<T> collection, @NotNull Function<T, ItemStack> map, @NotNull BiConsumer<Player, T> clickAction) {
        this(title, collection, map, clickAction, null);
    }

    /**
     * A method used to build multiple pages from a collection
     *
     * @return a list that properly represents the view
     */
    @NotNull
    private List<List<InnovativeElement>> buildView() {
        int pageSize = 7 * (DisplayView.ROWS - 2); //simplified from: DisplayView.ROWS * 9 - (DisplayView.ROWS * 2) - (9 * 2) + 4

        int except = (this.filter != null && this.filterInput != null) ? (int) this.collection.stream().filter(element -> !this.filter.apply(element, this.filterInput)).count() : 0;
        int pageCount = Math.max(1, (int) Math.ceil((double) (this.collection.size() - except) / pageSize)); //round up to nearest integer

        List<List<InnovativeElement>> pages = new ArrayList<>();
        Iterator<T> iterator = (this.filter != null && this.filterInput != null) ? this.collection.stream().filter(element -> this.filter.apply(element, this.filterInput)).iterator() : this.collection.iterator();

        for (int i = 0; i < pageCount; i++) {
            List<InnovativeElement> page = new ArrayList<>();

            for (int j = 0; j < DisplayView.ROWS * 9; j++) {
                int row = j / 9;
                int col = j % 9;

                if (!iterator.hasNext() || row == 0 || row == DisplayView.ROWS - 1 || col == 0 || col == 8) {
                    page.add(InnovativeElement.EMPTY);
                    continue;
                }

                T element = iterator.next();

                page.add(new InnovativeElement(this.map.apply(element), player -> this.clickAction.accept(player, element)));
            }

            pages.add(page);
        }

        return pages;
    }

    @NotNull
    private static List<List<InnovativeElement>> empty() {
        List<List<InnovativeElement>> pages = new ArrayList<>(1);

        List<InnovativeElement> page = new ArrayList<>();

        for (int i = 0; i < DisplayView.ROWS * 9; i++) {
            page.add(InnovativeElement.EMPTY);
        }

        pages.add(page);
        return pages;
    }
}