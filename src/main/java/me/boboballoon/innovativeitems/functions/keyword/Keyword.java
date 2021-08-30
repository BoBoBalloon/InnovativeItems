package me.boboballoon.innovativeitems.functions.keyword;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.InnovativeFunction;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedArguments;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a usable keyword in an ability config file
 */
public abstract class Keyword extends InnovativeFunction<Void> {
    public Keyword(@NotNull String identifier, @NotNull ExpectedArguments... arguments) {
        super(identifier, arguments);
    }

    @Override
    protected final Void call(ImmutableList<Object> arguments, RuntimeContext context) {
        this.calling(arguments, context);
        return null;
    }

    protected abstract void calling(ImmutableList<Object> arguments, RuntimeContext context);
}
