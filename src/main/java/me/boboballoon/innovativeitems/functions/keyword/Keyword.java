package me.boboballoon.innovativeitems.functions.keyword;

import me.boboballoon.innovativeitems.functions.VoidInnovativeFunction;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedArguments;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a usable keyword in an ability config file
 */
public abstract class Keyword extends VoidInnovativeFunction {
    public Keyword(@NotNull String identifier, @NotNull ExpectedArguments... arguments) {
        super(identifier, arguments);
    }
}
