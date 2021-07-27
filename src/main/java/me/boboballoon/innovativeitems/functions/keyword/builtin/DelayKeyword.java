package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedValues;

/**
 * Class that represents a keyword in an ability config file that delays the rest of the provided keywords
 */
public class DelayKeyword extends Keyword {
    public DelayKeyword() {
        super("delay",
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, "delay"));
    }

    @Override
    protected void calling(ImmutableList<Object> arguments, RuntimeContext context) {
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
