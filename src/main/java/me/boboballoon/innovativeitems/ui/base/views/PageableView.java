package me.boboballoon.innovativeitems.ui.base.views;

import me.boboballoon.innovativeitems.ui.base.InnovativeElement;
import me.boboballoon.innovativeitems.ui.base.InnovativeView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class that represents a pageable inventory view ui
 */
public class PageableView extends InnovativeView {
    private final List<List<InnovativeElement>> pages;
    private int currentPageIndex;

    private final List<Transform> onSetPages;

    /**
     * Creates a new instance of PageableView (it is assumed that the size of all elements of pages are the same size)
     *
     * @param title the title of the inventory
     * @param pages all pages
     * @throws IndexOutOfBoundsException if the pages list has a size of 0 or not all of its elements have the same size
     */
    public PageableView(@NotNull String title, @NotNull List<List<InnovativeElement>> pages) throws IndexOutOfBoundsException {
        super(title, pages.get(0));

        int size = pages.get(0).size();
        if (!pages.stream().allMatch(collection -> collection.size() == size)) {
            throw new IndexOutOfBoundsException("All elements of pages are not the same size!");
        }

        this.pages = pages.stream().map(ArrayList::new).collect(Collectors.toList());
        this.currentPageIndex = 0;
        this.onSetPages = new ArrayList<>();
    }

    /**
     * A method to add a listener to reformat the pages
     *
     * @param listener a listener to reformat the pages
     */
    protected final void addOnSetPagesListener(@NotNull Transform listener) {
        this.onSetPages.add(listener);
    }

    /**
     * A method used to update all of the different pages in this pageable view
     *
     * @param pages all of the NEW different pages in this pageable view
     */
    public final void setPages(@NotNull List<List<InnovativeElement>> pages) {
        int size = pages.get(0).size();
        if (pages.size() < 1 || !pages.stream().allMatch(collection -> collection.size() == size)) {
            return;
        }

        this.pages.clear();

        List<List<InnovativeElement>> transformed = pages;

        for (int i = this.onSetPages.size() - 1; i >= 0; i--) {
            Transform transform = this.onSetPages.get(i);
            transformed = transform.transform(transformed);
        }

        this.pages.addAll(transformed);

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
     * A functional interface used to modify pages when they are set
     */
    @FunctionalInterface
    protected interface Transform {
        /**
         * A function used to transform a pageable view
         *
         * @param pages the page data
         */
        @NotNull
        List<List<InnovativeElement>> transform(@NotNull List<List<InnovativeElement>> pages);
    }
}