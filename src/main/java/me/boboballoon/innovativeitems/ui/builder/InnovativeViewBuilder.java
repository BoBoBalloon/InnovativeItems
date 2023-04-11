package me.boboballoon.innovativeitems.ui.builder;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.ui.InnovativeElement;
import me.boboballoon.innovativeitems.ui.InnovativeView;
import org.jetbrains.annotations.NotNull;

/**
 * A view builder designed to build {@link me.boboballoon.innovativeitems.ui.InnovativeView}
 */
public final class InnovativeViewBuilder extends ViewBuilder<InnovativeView> {
    @NotNull
    @Override
    public InnovativeView build() {
        InnovativeElement[] elements = new InnovativeElement[this.getRows() * 9];

        for (int i = 0; i < this.getMaximumIndex(); i++) {
            InnovativeElement element = this.getElements().get(i);
            elements[i] = element != null ? element : InnovativeElement.EMPTY;
        }

        return new InnovativeView(this.getTitle(), ImmutableList.copyOf(elements));
    }
}
