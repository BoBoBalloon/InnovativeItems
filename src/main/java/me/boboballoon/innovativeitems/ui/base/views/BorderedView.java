package me.boboballoon.innovativeitems.ui.base.views;

import com.google.common.collect.ImmutableList;
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

    public BorderedView(@NotNull Material border, @NotNull String title, @NotNull ImmutableList<InnovativeElement> elements, @Nullable InnovativeElement topLeft, @Nullable InnovativeElement topRight, @Nullable InnovativeElement bottomLeft, @Nullable InnovativeElement bottomRight) {
        super(title, operation(elements, border, topLeft, topRight, bottomLeft, bottomRight));
        this.border = border;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    public BorderedView(@NotNull Material border, @NotNull String title, @NotNull ImmutableList<InnovativeElement> elements) {
        this(border, title, elements, null, null, null, null);
    }

    @Override
    public void setElements(@NotNull ImmutableList<InnovativeElement> elements) throws IllegalArgumentException {
        super.setElements(operation(elements, this.border, this.topLeft, this.topRight, this.bottomLeft, this.bottomRight));
    }

    /**
     * Set the element to be set in the top left of this view
     *
     * @param topLeft the element
     */
    public final void setTopLeft(@Nullable InnovativeElement topLeft) {
        this.topLeft = topLeft;
        this.setElements(this.getElements());
    }

    /**
     * Set the element to be set in the top right of this view
     *
     * @param topRight the element
     */
    public final void setTopRight(@Nullable InnovativeElement topRight) {
        this.topRight = topRight;
        this.setElements(this.getElements());
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

    @NotNull
    private static ImmutableList<InnovativeElement> operation(@NotNull ImmutableList<InnovativeElement> elements, @NotNull Material border, @Nullable InnovativeElement topLeft, @Nullable InnovativeElement topRight, @Nullable InnovativeElement bottomLeft, @Nullable InnovativeElement bottomRight) {
        List<InnovativeElement> data = new ArrayList<>(elements);
        BlankElement element = new BlankElement(border);

        for (int i = 0; i < data.size(); i++) {
            int row = i / 9;
            int col = i % 9;

            if (row == 0 && col == 0 && topLeft != null) {
                data.set(i, topLeft);
                continue;
            }

            if (row == 0 && col == 8 && topRight != null) {
                data.set(i, topRight);
                continue;
            }

            if (row == (data.size()) / 9 - 1 && col == 0 && bottomLeft != null) {
                data.set(i, bottomLeft);
                continue;
            }

            if (row == (data.size()) / 9 - 1 && col == 8 && bottomRight != null) {
                data.set(i, bottomLeft);
                continue;
            }

            if (row == 0 || row == (data.size()) / 9 - 1 || col == 0 || col == 8) {
                data.set(i, element);
            }
        }

        return ImmutableList.copyOf(data);
    }
}