package me.boboballoon.innovativeitems.functions.keyword;

import me.boboballoon.innovativeitems.functions.ActiveFunction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A class that represents a keyword after being parsed
 */
public class ActiveKeyword extends ActiveFunction<Void> {
    /**
     * A constructor used to build a keyword after being parsed
     *
     * @param base the base keyword being used
     * @param arguments the list of arguments with targeters already parsed
     */
    public ActiveKeyword(@NotNull Keyword base, @NotNull List<Object> arguments) {
        super(base, arguments);
    }

    @Override
    public Keyword getBase() {
        return (Keyword) super.getBase();
    }
}
