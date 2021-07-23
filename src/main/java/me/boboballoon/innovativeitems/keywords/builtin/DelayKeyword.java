package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedValues;

/**
 * Class that represents a keyword in an ability config file that delays the rest of the provided keywords
 */
public class DelayKeyword extends Keyword {
    public DelayKeyword() {
        super("delay",
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "delay"));
    }

    @Override
    protected void call(ImmutableList<Object> arguments, RuntimeContext context) {
        int delay = (Integer) arguments.get(0) * 50; //convert to milliseconds (from ticks)

        try {
            Thread.sleep(delay);
        } catch (InterruptedException ignore) {}
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
