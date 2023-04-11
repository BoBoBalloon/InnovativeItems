package me.boboballoon.innovativeitems.ui.builder;

import me.boboballoon.innovativeitems.ui.InnovativeElement;
import me.boboballoon.innovativeitems.ui.InnovativeView;
import me.boboballoon.innovativeitems.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A class used to easily create a new rectangular view
 */
public abstract class ViewBuilder<T extends InnovativeView> {
    private int rows;
    private String title;
    private final Map<Integer, InnovativeElement> elements;

    public ViewBuilder() {
        this.rows = 1;
        this.title = TextUtil.format("&r");
        this.elements = new HashMap<>();
    }

    /**
     * A method used to set the rows to be created by this builder
     *
     * @param rows the about of rows this view builder should create
     * @return the builder
     */
    @NotNull
    public final ViewBuilder<T> setRows(int rows) {
        this.rows = Math.max(Math.min(6, rows), 1); //min is 1, max is 6

        this.elements.entrySet().removeIf(element -> element.getKey() > this.getMaximumIndex()); //remove all elements in map if index is greater than new max index

        return this;
    }

    /**
     * A method used to the title of the view
     *
     * @param title the non color coded title of the view
     * @return this builder
     */
    @NotNull
    public final ViewBuilder<T> setTitle(@NotNull String title) {
        this.title = TextUtil.format(title);
        return this;
    }

    /**
     * A method used to add new items to the elements of the view
     *
     * @param index the index of the inventory that the element should be added
     * @param element the element to be added
     * @return this builder
     */
    @NotNull
    public final ViewBuilder<T> addElement(int index, @NotNull InnovativeElement element) {
        if (index < 0 || index > this.getMaximumIndex()) {
            return this; //if index is invalid skip
        }

        this.elements.put(index, element);
        return this;
    }

    /**
     * A method used to add new items to the elements of the view
     *
     * @param index the index of the inventory that the element should be added
     * @param item the item to be added
     * @return this builder
     */
    @NotNull
    public final ViewBuilder<T> addElement(int index, @NotNull ItemStack item) {
        if (index < 0 || index > this.getMaximumIndex()) {
            return this; //if index is invalid skip
        }

        return this.addElement(index, item.getType() != Material.AIR ? new InnovativeElement(item) : InnovativeElement.EMPTY);
    }

    /**
     * A method used to remove an element from the view
     *
     * @param index the index of the inventory that the element should be removed
     * @return this builder
     */
    @NotNull
    public final ViewBuilder<T> removeElement(int index) {
        this.elements.remove(index);
        return this;
    }

    /**
     * A method which returns how many rows this view should have
     *
     * @return the highest index an item can have in this view
     */
    protected final int getRows() {
        return this.rows;
    }

    /**
     * A method which returns the title of the view
     *
     * @return the title of the view
     */
    @NotNull
    protected final String getTitle() {
        return this.title;
    }

    /**
     * A method which returns what elements are in the builder
     *
     * @return the elements in the builder
     */
    @NotNull
    protected final Map<Integer, InnovativeElement> getElements() {
        return this.elements;
    }

    /**
     * A method which returns the highest index an item can have in this view
     *
     * @return the highest index an item can have in this view
     */
    protected int getMaximumIndex() {
        return this.rows * 9 - 1;
    }

    /**
     * A method used to finally create the view based on the information previously given/assumed by the builder
     *
     * @return the expected view
     */
    @NotNull
    public abstract T build();
}