package me.boboballoon.innovativeitems.ui.builder;

import me.boboballoon.innovativeitems.ui.InnovativeElement;
import me.boboballoon.innovativeitems.ui.views.PagedView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A view builder designed to build {@link PagedView}
 */
public final class PagedViewBuilder extends ViewBuilder<PagedView> {
    private int pages;

    public PagedViewBuilder() {
        super();
        this.pages = 1;
    }

    @Override
    protected int getMaximumIndex() {
        return super.getMaximumIndex() * this.pages;
    }

    /**
     * A method used to set the amount of pages this view should have
     *
     * @param pages the amount of pages this view should have
     * @return this builder
     */
    @NotNull
    public PagedViewBuilder setPages(int pages) {
        this.pages = Math.max(pages, 1); //can't be less than one

        this.getElements().entrySet().removeIf(element -> element.getKey() > this.getMaximumIndex());

        return this;
    }

    @NotNull
    @Override
    public PagedView build() {
        List<List<InnovativeElement>> pagesData = new ArrayList<>(this.pages);

        for (int page = 0; page < this.pages; page++) {
            List<InnovativeElement> elements = new ArrayList<>(this.getRows() * 9);
            for (int i = 0; i < this.getRows() * 9; i++) {
                InnovativeElement element = this.getElements().get(i + (this.getRows() * 9 * page));
                elements.add(element != null ? element : InnovativeElement.EMPTY);
            }
            pagesData.add(elements);
        }

        return new PagedView(this.getTitle(), pagesData);
    }
}
