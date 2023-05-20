package me.boboballoon.innovativeitems.ui.base.views;

import me.boboballoon.innovativeitems.ui.base.InnovativeElement;
import me.boboballoon.innovativeitems.ui.base.InnovativeView;
import me.boboballoon.innovativeitems.ui.base.elements.BlankElement;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents an inventory view ui with a material border
 */
public class BorderedView extends InnovativeView {
    private final Material border;
    private InnovativeElement topLeft;
    private InnovativeElement topRight;
    private InnovativeElement bottomLeft;
    private InnovativeElement bottomRight;

    public BorderedView(@NotNull Material border, @NotNull String title, @NotNull List<InnovativeElement> elements, @Nullable InnovativeElement topLeft, @Nullable InnovativeElement topRight, @Nullable InnovativeElement bottomLeft, @Nullable InnovativeElement bottomRight) {
        super(title, operation(elements, border, topLeft, topRight, bottomLeft, bottomRight));
        this.border = border;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
        this.addSetElementsListener(page -> operation(page, this.border, this.topLeft, this.topRight, this.bottomLeft, this.bottomRight));
    }

    public BorderedView(@NotNull Material border, @NotNull String title, @NotNull List<InnovativeElement> elements) {
        this(border, title, elements, null, null, null, null);
    }

    /**
     * Set the element to be set in the top left of this view
     *
     * @param topLeft the element
     */
    public final void setTopLeft(@Nullable InnovativeElement topLeft) {
        this.topLeft = topLeft;
        this.setElements(new ArrayList<>(this.getElements()));
    }

    /**
     * Set the element to be set in the top right of this view
     *
     * @param topRight the element
     */
    public final void setTopRight(@Nullable InnovativeElement topRight) {
        this.topRight = topRight;
        this.setElements(new ArrayList<>(this.getElements()));
    }

    /**
     * Set the element to be set in the bottom left of this view
     *
     * @param bottomLeft the element
     */
    public final void setBottomLeft(@Nullable InnovativeElement bottomLeft) {
        this.bottomLeft = bottomLeft;
        this.setElements(new ArrayList<>(this.getElements()));
    }

    /**
     * Set the element to be set in the bottom right of this view
     *
     * @param bottomRight the element
     */
    public final void setBottomRight(@Nullable InnovativeElement bottomRight) {
        this.bottomRight = bottomRight;
        this.setElements(new ArrayList<>(this.getElements()));
    }

    @NotNull
    private static List<InnovativeElement> operation(@NotNull List<InnovativeElement> elements, @NotNull Material border, @Nullable InnovativeElement topLeft, @Nullable InnovativeElement topRight, @Nullable InnovativeElement bottomLeft, @Nullable InnovativeElement bottomRight) {
        BlankElement element = new BlankElement(border);

        for (int i = 0; i < elements.size(); i++) {
            int row = i / 9;
            int col = i % 9;

            if (row == 0 && col == 0 && topLeft != null) {
                elements.set(i, topLeft);
                continue;
            }

            if (row == 0 && col == 8 && topRight != null) {
                elements.set(i, topRight);
                continue;
            }

            if (row == (elements.size()) / 9 - 1 && col == 0 && bottomLeft != null) {
                elements.set(i, bottomLeft);
                continue;
            }

            if (row == (elements.size()) / 9 - 1 && col == 8 && bottomRight != null) {
                elements.set(i, bottomRight);
                continue;
            }

            if (row == 0 || row == elements.size() / 9 - 1 || col == 0 || col == 8) {
                elements.set(i, element);
            }
        }

        return elements;
    }
}