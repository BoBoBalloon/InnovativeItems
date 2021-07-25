package me.boboballoon.innovativeitems.keywords.keyword.arguments;

import com.google.common.collect.ImmutableSet;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordContext;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordTargeter;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * A class used to contain all possible keyword targeters used in a keyword argument
 */
public class ExpectedTargeters implements ExpectedArguments {
    private final ImmutableSet<KeywordTargeter> targeters;

    public ExpectedTargeters(ImmutableSet<KeywordTargeter> targeters) {
        this.targeters = targeters;
    }

    public ExpectedTargeters(Set<KeywordTargeter> targeters) {
        this(ImmutableSet.copyOf(targeters));
    }

    public ExpectedTargeters(KeywordTargeter... targeters) {
        this(ImmutableSet.copyOf(targeters));
    }

    /**
     * A method used to get all allowed targeters for a given argument in a keyword
     *
     * @return all allowed targeters for a given argument in a keyword
     */
    public ImmutableSet<KeywordTargeter> getTargeters() {
        return this.targeters;
    }

    /**
     * A method used to check if a keyword targeter is expected in this argument
     *
     * @param targeter the targeter to check
     * @return a boolean that is true when the provided targeter is an expected argument
     */
    public boolean contains(KeywordTargeter targeter) {
        return this.targeters.contains(targeter);
    }

    @Override
    @Nullable
    public Object getValue(String rawValue, KeywordContext context) {
        throw new UnsupportedOperationException("The ExpectedTargeters class does not support the use of the getValue method!");
    }

    @Override
    public boolean shouldGetValue() {
        return false;
    }
}
