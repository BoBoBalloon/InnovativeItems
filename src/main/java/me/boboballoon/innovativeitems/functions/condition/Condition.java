package me.boboballoon.innovativeitems.functions.condition;

import me.boboballoon.innovativeitems.functions.InnovativeFunction;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedArguments;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a usable condition in an ability config file
 */
public abstract class Condition extends InnovativeFunction<Boolean> {
    public Condition(@NotNull String identifier, ExpectedArguments... arguments) {
        super(identifier, arguments);
    }
}
