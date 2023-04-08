package me.boboballoon.innovativeitems.ui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A class that represents an inventory view ui
 */
public class InnovativeView implements InventoryHolder {
    private final Inventory inventory;
    private ImmutableList<InnovativeElement> elements;

    /**
     * Creates a new instance of InnovativeView (size of elements must be a multiple of 9)
     *
     * @param title title of inventory
     * @param elements the items and effects
     */
    public InnovativeView(@NotNull String title, @NotNull ImmutableList<InnovativeElement> elements) {
        this.inventory = Bukkit.createInventory(this, elements.size(), title);
        this.elements = elements;
        this.reload();
    }

    public InnovativeView(@NotNull String title, @NotNull List<InnovativeElement> elements) {
        this(title, populate(elements));
    }

    public InnovativeView(@NotNull String title, @NotNull InnovativeElement[] elements) {
        this(title, populate(Lists.newArrayList(elements)));
    }

    /**
     * A method that returns this views inventory
     *
     * @return this views inventory
     */
    @Override
    @NotNull
    public final Inventory getInventory() {
        return this.inventory;
    }

    /**
     * A method that returns this views items, all inventory slots must have corresponding element
     *
     * @return this views items, all inventory slots must have corresponding element
     */
    @NotNull
    public final ImmutableList<InnovativeElement> getElements() {
        return this.elements;
    }

    /**
     * A method that sets the elements of this view
     *
     * @param elements the elements of this view
     * @throws IllegalArgumentException if the provided elements do not reflect the size of the inventory
     */
    public final void setElements(@NotNull ImmutableList<InnovativeElement> elements) throws IllegalArgumentException {
        if (this.getSize() != elements.size()) {
            throw new IllegalArgumentException("Invalid elements provided!");
        }

        this.elements = elements;
        this.reload();
    }

    /**
     * A method that sets the elements of this view
     *
     * @param elements the elements of this view
     * @throws IllegalArgumentException if the provided elements do not reflect the size of the inventory
     */
    public final void setElements(@NotNull List<InnovativeElement> elements) throws IllegalArgumentException {
        if (this.getSize() != elements.size()) {
            throw new IllegalArgumentException("Invalid elements provided!");
        }

        this.setElements(populate(elements));
    }

    /**
     * A method that returns the existing element at the provided index
     *
     * @param index the provided index
     * @return the existing element at the provided index
     */
    @NotNull
    public final InnovativeElement getElement(int index) {
        return this.elements.get(index);
    }

    /**
     * A method that returns the size of this view
     *
     * @return the size of this view
     */
    public final int getSize() {
        return this.inventory.getSize();
    }

    /**
     * A method that sets all of the items in the to reflect the elements
     *
     * @throws IllegalArgumentException if the provided elements do not reflect the size of the inventory
     */
    public final void reload() throws IllegalArgumentException {
        if (this.getSize() != this.elements.size()) {
            throw new IllegalArgumentException("Invalid elements provided!");
        }

        for (int i = 0; i < this.getSize(); i++) {
            InnovativeElement element = this.getElement(i);
            this.inventory.setItem(i, element.getStack());
            element.getLoadAction().run();
        }

        this.inventory.getViewers().stream().map(human -> (Player) human).forEach(Player::updateInventory); //safe to assume human is player
    }

    /**
     * A method used to easily open the view for a player
     *
     * @param player the player to show the view to
     */
    public final void open(@NotNull Player player) {
        player.openInventory(this.inventory);
    }

    /**
     * A private method used to populate an array of elements so that all null elements are set to empty elements
     *
     * @param elements the list of elements that must be populated so that no null elements are present in the collection
     */
    @NotNull
    private static ImmutableList<InnovativeElement> populate(@NotNull List<InnovativeElement> elements) {
        for (int i = 0; i < elements.size(); i++) {
            InnovativeElement element = elements.get(i);

            if (element == null) {
                elements.set(i, InnovativeElement.EMPTY);
            }
        }

        return ImmutableList.copyOf(elements);
    }
}