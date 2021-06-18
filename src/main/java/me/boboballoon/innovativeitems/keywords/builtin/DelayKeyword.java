package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordContext;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Class that represents a keyword in an ability config file that delays the rest of the provided keywords
 */
public class DelayKeyword extends Keyword {
    public DelayKeyword() {
        super("delay", false);
    }

    @Override
    public void execute(List<Object> arguments, RuntimeContext context) {
        int delay = (Integer) arguments.get(0);

        try {
            Thread.sleep(delay);
        } catch (InterruptedException ignore) {}
    }

    @Override
    @Nullable
    public List<Object> load(KeywordContext context) {
        String[] raw = context.getContext();
        List<Object> args = new ArrayList<>();

        int delay;
        try {
            delay = Integer.parseInt(raw[0]);
        } catch (NumberFormatException e) {
            LogUtil.log(Level.WARNING, "There is not a valid delay entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        args.add(delay * 50); //delay in ticks, 1000 milliseconds in second and 20 ticks in second 1000 / 20 = 50

        return args;
    }

    @Override
    public ImmutableList<String> getValidTargeters() {
        return ImmutableList.of();
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
