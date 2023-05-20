package me.boboballoon.innovativeitems.ui.base.views;

import me.boboballoon.innovativeitems.ui.base.InnovativeElement;
import me.boboballoon.innovativeitems.ui.base.elements.BlankElement;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A class that represents a paged inventory view ui
 */
public class PagedView extends PageableView {
    /**
     * Creates a new instance of PagedView (it is assumed that the size of all elements of pages are the same size)
     *
     * @param title the title of the inventory
     * @param pages all pages
     * @throws IndexOutOfBoundsException if the pages list has a size of 0 or not all of its elements have the same size
     */
    public PagedView(@NotNull String title, @NotNull List<List<InnovativeElement>> pages) throws IndexOutOfBoundsException {
        super(title, pages);

        BlankElement previous = new BlankElement(new ItemStack(Material.ARROW), player -> this.setCurrentPageIndex(this.getCurrentPageIndex() - 1));
        BlankElement next = new BlankElement(new ItemStack(Material.ARROW), player -> this.setCurrentPageIndex(this.getCurrentPageIndex() + 1));

        this.addOnSetPagesListener(elements -> {
            for (int i = 0; i < elements.size(); i++) {
                List<InnovativeElement> page = elements.get(i);

                if (i > 0) {
                    page.set(0, previous); //first slot in first row
                }

                if (i < pages.size() - 1) {
                    page.set(8, next); //last slot in first row
                }
            }

            return elements;
        });

        this.setPages(pages);
    }
}