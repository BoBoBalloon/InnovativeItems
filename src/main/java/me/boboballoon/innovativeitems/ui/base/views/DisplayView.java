package me.boboballoon.innovativeitems.ui.base.views;

import me.boboballoon.innovativeitems.ui.base.InnovativeElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A class that represents an paged view ui with a material border
 */
public class DisplayView<T> extends PagedBorderedView {
    private static final int ROWS = 5; //two for the border

    public DisplayView(@NotNull String title, @NotNull Collection<T> collection, @NotNull Function<T, ItemStack> map, @NotNull BiConsumer<Player, T> clickAction) {
        super(Material.GRAY_STAINED_GLASS_PANE, title, buildView(collection, map, clickAction));
        this.addOnSetPagesListener(page -> buildView(collection, map, clickAction));
    }

    /**
     * A method used to build multiple pages from a collection
     *
     * @param collection the collection to build a view from
     * @param map a function to map type T to {@link ItemStack}
     * @param clickAction what action should occur when the item is clicked
     * @param <T> the type to build the view from
     * @return a list that properly represents the view
     */
    @NotNull
    private static <T> List<List<InnovativeElement>> buildView(@NotNull Collection<T> collection, @NotNull Function<T, ItemStack> map, @NotNull BiConsumer<Player, T> clickAction) {
        int pageSize = 7 * (DisplayView.ROWS - 2); //simplified from: DisplayView.ROWS * 9 - (DisplayView.ROWS * 2) - (9 * 2) + 4

        int pageCount = (int) Math.ceil((double) collection.size() / pageSize); //round up to nearest integer

        List<List<InnovativeElement>> pages = new ArrayList<>();
        Iterator<T> iterator = collection.iterator();

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

                page.add(new InnovativeElement(map.apply(element), player -> clickAction.accept(player, element)));
            }

            pages.add(page);
        }

        return pages;
    }
}