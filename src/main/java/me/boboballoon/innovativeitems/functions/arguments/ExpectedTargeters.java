package me.boboballoon.innovativeitems.functions.arguments;

import com.google.common.collect.ImmutableSet;
import me.boboballoon.innovativeitems.functions.FunctionContext;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

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

    /**
     * A method used to check if a keyword targeter is expected in this argument
     *
     * @param targeter the targeter to check
     * @return a boolean that is true when the provided targeter is an expected argument
     */
    public boolean contains(FunctionTargeter targeter) {
        return this.targeters.contains(targeter);
    }

    @Override
    @Nullable
    public Object getValue(String rawValue, FunctionContext context) {
        throw new UnsupportedOperationException("The ExpectedTargeters class does not support the use of the getValue method!");
    }

    @Override
    public boolean shouldGetValue() {
        return false;
    }
}
