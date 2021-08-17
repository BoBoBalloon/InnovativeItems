package me.boboballoon.innovativeitems.functions.arguments;

import com.google.common.collect.ImmutableSet;
import me.boboballoon.innovativeitems.functions.FunctionContext;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;

/**
 * A class used to contain all possible keyword targeters used in a keyword argument
 */
public class ExpectedTargeters implements ExpectedArguments {
    private final ImmutableSet<FunctionTargeter> targeters;

    public ExpectedTargeters(ImmutableSet<FunctionTargeter> targeters) {
        this.targeters = targeters;
    }

    public ExpectedTargeters(Set<FunctionTargeter> targeters) {
        this(ImmutableSet.copyOf(targeters));
    }

    public ExpectedTargeters(FunctionTargeter... targeters) {
        this(ImmutableSet.copyOf(targeters));
    }

    /**
     * A method used to get all allowed targeters for a given argument in a keyword
     *
     * @return all allowed targeters for a given argument in a keyword
     */
    public ImmutableSet<FunctionTargeter> getTargeters() {
        return this.targeters;
    }

    @Override
    @Nullable
    public FunctionTargeter getValue(@NotNull String rawValue, @NotNull FunctionContext context) {
        String section = (context.getFunction() instanceof Keyword) ? " on keywords" : " on conditions";

        if (!rawValue.startsWith("?")) {
            LogUtil.log(LogUtil.Level.WARNING, "Line " + context.getLineNumber() + section + " on ability " + context.getAbilityName() + " was expected a targeter but did not receive one!");
            return null;
        }

        FunctionTargeter targeter = FunctionTargeter.getFromIdentifier(rawValue);

        if (targeter == null) {
            LogUtil.log(LogUtil.Level.WARNING, "Line " + context.getLineNumber() + section +  " on ability " + context.getAbilityName() + " is an invalid targeter because it does not exist!");
            return null;
        }

        if (!context.getAbilityTrigger().getAllowedTargeters().contains(rawValue)) {
            LogUtil.log(LogUtil.Level.WARNING, "Line " + context.getLineNumber() + section +  " on ability " + context.getAbilityName() + " has an invalid targeter for the trigger of " + context.getAbilityTrigger().getIdentifier() + "!");
            return null;
        }

        if (!this.targeters.contains(targeter)) {
            LogUtil.log(LogUtil.Level.WARNING, "Line " + context.getLineNumber() + section +  " on ability " + context.getAbilityName() + " is an invalid targeter for the keyword of " + context.getFunction().getIdentifier() + "!");
            return null;
        }

        return targeter;
    }

    @Override
    public Consumer<FunctionContext> getOnError() {
        return context -> {};
    }
}
