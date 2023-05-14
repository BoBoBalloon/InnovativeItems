package me.boboballoon.innovativeitems.ui.base.views;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.ui.base.InnovativeElement;
import me.boboballoon.innovativeitems.ui.base.elements.BlankElement;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class that represents an paged view ui with a material border
 */
public class PagedBorderedView extends PagedView {
    private final Material border;
    private InnovativeElement bottomLeft;
    private InnovativeElement bottomRight;

    public PagedBorderedView(@NotNull Material border, @NotNull String title, @NotNull List<ImmutableList<InnovativeElement>> elements, @Nullable InnovativeElement bottomLeft, @Nullable InnovativeElement bottomRight) {
        super(title, empty(elements.size(), elements.get(0).size()));
        this.border = border;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
        this.setPages(elements);
    }

    public PagedBorderedView(@NotNull Material border, @NotNull String title, @NotNull List<ImmutableList<InnovativeElement>> elements) {
        this(border, title, elements, null, null);
    }

    @Override
    public void setPages(@NotNull List<ImmutableList<InnovativeElement>> pages) {
        super.setPages(this.reformat(pages.stream().map(ArrayList::new).collect(Collectors.toList())));
    }

    /**
     * Set the element to be set in the bottom left of this view
     *
     * @param bottomLeft the element
     */
    public final void setBottomLeft(@Nullable InnovativeElement bottomLeft) {
        this.bottomLeft = bottomLeft;
        this.setElements(this.getElements());
    }

    /**
     * Set the element to be set in the bottom right of this view
     *
     * @param bottomRight the element
     */
    public final void setBottomRight(@Nullable InnovativeElement bottomRight) {
        this.bottomRight = bottomRight;
        this.setElements(this.getElements());
    }

    /**
     * Converts types and adds in arrow elements in top row
     *
     * @param pages the pages of the view
     * @return the completed page collection
     */
    @NotNull
    private List<ImmutableList<InnovativeElement>> reformat(@NotNull List<List<InnovativeElement>> pages) {
        List<ImmutableList<InnovativeElement>> elements = new ArrayList<>(pages.size());

        BlankElement previous = new BlankElement(new ItemStack(Material.ARROW), player -> this.setCurrentPageIndex(this.getCurrentPageIndex() - 1));
        BlankElement next = new BlankElement(new ItemStack(Material.ARROW), player -> this.setCurrentPageIndex(this.getCurrentPageIndex() + 1));

        BlankElement element = new BlankElement(this.border);

        for (int i = 0; i < pages.size(); i++) {
            List<InnovativeElement> page = pages.get(i);

            if (i > 0) {
                page.set(0, previous); //first slot in first row
            }

            if (i < pages.size() - 1) {
                page.set(8, next); //last slot in first row
            }

            for (int j = 0; j < page.size(); j++) {
                int row = j / 9;
                int col = j % 9;

                if ((row == 0 && col == 0) || (row == 0 && col == 8)) {
                    continue;
                }

                if (row == (page.size()) / 9 - 1 && col == 0 && this.bottomLeft != null) {
                    page.set(j, this.bottomLeft);
                    continue;
                }

                if (row == (page.size()) / 9 - 1 && col == 8 && this.bottomRight != null) {
                    page.set(j, this.bottomRight);
                    continue;
                }

                if (row == 0 || row == page.size() / 9 - 1 || col == 0 || col == 8) {
                    page.set(j, element);
                }
            }

            elements.add(ImmutableList.copyOf(page));
        }

        return elements;
    }

    @NotNull
    private static List<List<InnovativeElement>> empty(int pages, int size) {
        List<InnovativeElement> full = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            full.add(InnovativeElement.EMPTY);
        }

        List<List<InnovativeElement>> list = new ArrayList<>();

        for (int i = 0; i < pages; i++) {
            list.add(full);
        }

        return list;
    }
}