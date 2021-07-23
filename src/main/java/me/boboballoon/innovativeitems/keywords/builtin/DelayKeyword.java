package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedValues;
import me.boboballoon.innovativeitems.util.LogUtil;

/**
 * Class that represents a keyword in an ability config file that delays the rest of the provided keywords
 */
public class DelayKeyword extends Keyword {
    public DelayKeyword() {
        super("delay",
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.INTEGER, context -> LogUtil.log(LogUtil.Level.WARNING, "There is not a valid delay entered on the " + context.getKeyword().getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!")));
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
