package me.boboballoon.innovativeitems.ui;

import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a pageable inventory view ui
 */
public class PageableView extends InnovativeView {
    private ImmutableList<ImmutableList<InnovativeElement>> pages;
    private int currentPageIndex;

    /**
     * Creates a new instance of PageableView (it is assumed that the size of all elements of pages are the same size)
     *
     * @param title the title of the inventory
     * @param pages all pages
     * @throws IndexOutOfBoundsException if the pages list has a size of 0 or not all of its elements have the same size
     */
    public PageableView(@NotNull String title, @NotNull ImmutableList<List<InnovativeElement>> pages) throws IndexOutOfBoundsException {
        super(title, pages.get(0));

        int size = pages.get(0).size();
        if (!pages.stream().allMatch(collection -> collection.size() == size)) {
            throw new IndexOutOfBoundsException("All elements of pages are not the same size!");
        }

        this.pages = this.page(pages);
        this.currentPageIndex = 0;
        this.setElements(this.pages.get(this.currentPageIndex)); //reloads
    }

    public PageableView(@NotNull String title, @NotNull List<List<InnovativeElement>> pages) throws IndexOutOfBoundsException {
        this(title, ImmutableList.copyOf(pages));
    }

    public PageableView(@NotNull String title, @NotNull List<InnovativeElement>[] pages) throws IndexOutOfBoundsException {
        this(title, ImmutableList.copyOf(pages));
    }

    /**
     * A method used to return all of the different pages in this pageable view
     *
     * @return all of the different pages in this pageable view
     */
    @NotNull
    public final ImmutableList<ImmutableList<InnovativeElement>> getPages() {
        return this.pages;
    }

    /**
     * A method used to update all of the different pages in this pageable view
     *
     * @param pages all of the NEW different pages in this pageable view
     */
    public final void setPages(@NotNull ImmutableList<List<InnovativeElement>> pages) {
        if (pages.size() < 1) {
            return;
        }

        this.pages = this.page(pages);
        this.currentPageIndex = 0;
        this.setElements(this.pages.get(this.currentPageIndex)); //calls reload
    }

    /**
     * A method used to get what page this view is currently on
     *
     * @return what page this view is currently on
     */
    public final int getCurrentPageIndex() {
        return this.currentPageIndex;
    }

    /**
     * A method used to set the current page that this pageable view should be on
     *
     * @param currentPageIndex the current page that this pageable view should be on
     */
    public final void setCurrentPageIndex(int currentPageIndex) {
        int bounded = Math.max(Math.min(this.pages.size() - 1, currentPageIndex), 0);

        if (bounded == this.currentPageIndex) {
            return; //don't bother reloading if it is the same
        }

        this.currentPageIndex = bounded;
        this.setElements(this.pages.get(this.currentPageIndex)); //calls reload
    }

    /**
     * Converts types and adds in arrow elements in top row
     *
     * @param pages the pages of the view
     * @return the completed page collection
     */
    @NotNull
    private ImmutableList<ImmutableList<InnovativeElement>> page(@NotNull List<List<InnovativeElement>> pages) {
        List<ImmutableList<InnovativeElement>> elements = new ArrayList<>(pages.size());
        BlankElement previous = new BlankElement(new ItemStack(Material.ARROW), player -> this.setCurrentPageIndex(this.getCurrentPageIndex() - 1));
        BlankElement next = new BlankElement(new ItemStack(Material.ARROW), player -> this.setCurrentPageIndex(this.getCurrentPageIndex() + 1));

        for (List<InnovativeElement> page : pages) {
            page.set(0, previous);
            page.set(8, next);
            elements.add(ImmutableList.copyOf(page));
        }

        return ImmutableList.copyOf(elements);
    }
}