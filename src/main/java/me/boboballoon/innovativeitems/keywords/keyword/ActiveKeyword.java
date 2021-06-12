package me.boboballoon.innovativeitems.keywords.keyword;

import me.boboballoon.innovativeitems.keywords.keyword.context.KeywordContext;
import me.boboballoon.innovativeitems.keywords.keyword.context.RuntimeContext;

import java.util.List;

/**
 * A class that represents a keyword after being parsed
 */
public class ActiveKeyword {
    private final Keyword base;
    private final List<Object> arguments;

    /**
     * A constructor used to build a keyword after being parsed
     *
     * @param base the base keyword being used
     * @param context the context in which the base keyword was used in
     */
    public ActiveKeyword(Keyword base, KeywordContext context) {
        this.base = base;
        this.arguments = base.load(context);
    }

    /**
     * A method that executes the base keyword given the provided context
     *
     * @param context context that can assist execution that cannot be cached and must be parsed during runtime separately
     */
    public void execute(RuntimeContext context) {
        this.base.execute(this.arguments, context);
    }
}
