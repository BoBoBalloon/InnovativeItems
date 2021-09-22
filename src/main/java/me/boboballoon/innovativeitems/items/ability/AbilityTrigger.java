package me.boboballoon.innovativeitems.items.ability;

import com.google.common.collect.ImmutableSet;
import me.boboballoon.innovativeitems.functions.context.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class that contains all possible causes to trigger an ability
 */
public enum AbilityTrigger {
    RIGHT_CLICK("right-click", InteractContext.class, null),
    LEFT_CLICK("left-click", InteractContext.class, null),
    RIGHT_CLICK_BLOCK("right-click-block", InteractContextBlock.class, null, "?block"),
    LEFT_CLICK_BLOCK("left-click-block", InteractContextBlock.class, null, "?block"),
    DAMAGE_DEALT("damage-dealt", DamageContext.class, null,"?entity"),
    DAMAGE_TAKEN("damage-taken", DamageContext.class, null, "?entity"),
    CONSUME_ITEM("item-consume", ConsumeContext.class, null),
    TIMER("timer", RuntimeContext.class, "timer:\\d+"),
    BLOCK_BREAK("block-break", BlockBreakContext.class, null, "?block"),
    NONE("none", RuntimeContext.class, null);

    private final String identifier;
    private final Class<? extends RuntimeContext> expectedContext;
    private final String regex;
    private final ImmutableSet<String> allowedTargeters;

    AbilityTrigger(String identifier, Class<? extends RuntimeContext> expectedContext, @Nullable String regex, String... allowedTargeters) {
        this.identifier = identifier;
        this.expectedContext = expectedContext;
        this.regex = regex;
        this.allowedTargeters = this.toImmutableSet(allowedTargeters);
    }

    /**
     * A method that returns the identifier that is used to identify and sometimes parse the ability trigger
     *
     * @return the identifier that is used to identify and sometimes parse the ability trigger
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * A method that returns the class of the runtime context that is expected to be used for each ability trigger
     *
     * @return the class of the runtime context that is expected to be used for each ability trigger
     */
    public Class<? extends RuntimeContext> getExpectedContext() {
        return this.expectedContext;
    }

    /**
     * A method that returns the specific regex that is used to parse the ability trigger
     *
     * @return the specific regex that is used to parse the ability trigger
     */
    @Nullable
    public String getRegex() {
        return this.regex;
    }

    /**
     * A method that returns all allowed targeters used for this trigger
     *
     * @return all allowed targeters used for this trigger
     */
    public ImmutableSet<String> getAllowedTargeters() {
        return this.allowedTargeters;
    }


    /**
     * A method used to check if the current ability trigger is compatible with the specified ability trigger
     *
     * @param trigger the specified ability trigger
     * @return a boolean that is true when the current ability trigger is compatible with the specified ability trigger
     */
    public boolean isCompatible(@NotNull AbilityTrigger trigger) {
        for (String allowedTargeter : trigger.getAllowedTargeters()) {
            if (!this.getAllowedTargeters().contains(allowedTargeter)) {
                return false;
            }
        }

        return true;
    }

    /**
     * A method used to identify an ability trigger via a provided string
     *
     * @param provided the provided string
     * @return the corresponding ability trigger (null if nothing matches)
     */
    @Nullable
    public static AbilityTrigger getFromIdentifier(@NotNull String provided) {
        for (AbilityTrigger trigger : AbilityTrigger.values()) {
            if (trigger.getRegex() != null && provided.matches(trigger.getRegex())) {
                return trigger;
            }

            if (provided.equalsIgnoreCase(trigger.getIdentifier())) {
                return trigger;
            }
        }

        return null;
    }

    /**
     * A private method to convert var args to a list because Arrays.asList() is immutable
     *
     * @param strings the var arg
     * @return the list
     */
    private ImmutableSet<String> toImmutableSet(String... strings) {
        List<String> list = new ArrayList<>();

        Collections.addAll(list, strings);
        list.add("?player"); //player is valid on all triggers

        return ImmutableSet.copyOf(list);
    }
}
