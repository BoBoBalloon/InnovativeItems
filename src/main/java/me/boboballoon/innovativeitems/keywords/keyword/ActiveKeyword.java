package me.boboballoon.innovativeitems.keywords.keyword;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;

import java.util.List;

/**
 * A class that represents a keyword after being parsed
 */
public class ActiveKeyword {
    private final Keyword base;
    private final ImmutableList<Object> arguments;

    /**
     * A constructor used to build a keyword after being parsed
     *
     * @param base the base keyword being used
     * @param arguments the list of arguments with targeters already parsed
     */
    public ActiveKeyword(Keyword base, List<Object> arguments) {
        this.base = base;
        this.arguments = ImmutableList.copyOf(arguments);
    }

    /**
     * A method that executes the base keyword given the provided context (will always be fired async)
     *
     * @param context context that can assist execution that cannot be cached and must be parsed during runtime separately
     */
    public void execute(RuntimeContext context) {
        if (this.arguments == null) {
            return;
        }

        this.base.execute(this.arguments, context);
    }
}
