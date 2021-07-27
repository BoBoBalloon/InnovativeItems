package me.boboballoon.innovativeitems.functions;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedArguments;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import org.jetbrains.annotations.NotNull;

public abstract class VoidInnovativeFunction extends InnovativeFunction<Void> {
    public VoidInnovativeFunction(@NotNull String identifier, ExpectedArguments... arguments) {
        super(identifier, arguments);
    }

    @Override
    protected final Void call(ImmutableList<Object> arguments, RuntimeContext context) {
        this.calling(arguments, context);
        return null;
    }

    protected abstract void calling(ImmutableList<Object> arguments, RuntimeContext context);
}
