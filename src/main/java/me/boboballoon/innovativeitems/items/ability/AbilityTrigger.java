package me.boboballoon.innovativeitems.items.ability;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class that contains all possible causes to trigger an ability
 */
public enum AbilityTrigger {
    /**
     * An ability trigger that will always fire the InteractContext.java
     */
    RIGHT_CLICK("right-click", null),

    /**
     * An ability trigger that will always fire the InteractContext.java
     */
    LEFT_CLICK("left-click", null),

    /**
     * An ability trigger that will always fire the InteractContext.java
     */
    RIGHT_CLICK_BLOCK("right-click-block", null, "?block"),

    /**
     * An ability trigger that will always fire the InteractContext.java
     */
    LEFT_CLICK_BLOCK("left-click-block", null, "?block"),

    /**
     * An ability trigger that will always fire the DamageContext.java
     */
    DAMAGE_DEALT("damage-dealt", null, "?entity"),

    /**
     * An ability trigger that will always fire the DamageContext.java
     */
    DAMAGE_TAKEN("damage-taken", null, "?entity"),

    /**
     * An ability trigger that will always fire the ConsumeContext.java
     */
    CONSUME_ITEM("item-consume", null),

    /**
     * An ability trigger that will always fire the RuntimeContext.java
     */
    TIMER("timer", "timer:\\d+"),

    /**
     * An ability trigger that will always fire the BlockBreakContext.java
     */
    BLOCK_BREAK("block-break", null, "?block"),

    /**
     * An ability trigger that will always fire the RuntimeContext.java
     */
    NONE("none", null);

    private final String identifier;
    private final String regex;
    private final ImmutableList<String> allowedTargeters;

    AbilityTrigger(String identifier, @Nullable String regex, String... allowedTargeters) {
        this.identifier = identifier;
        this.regex = regex;
        this.allowedTargeters = this.toImmutableList(allowedTargeters);
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
    public ImmutableList<String> getAllowedTargeters() {
        return this.allowedTargeters;
    }

    /**
     * A method used to identify an ability trigger via a provided string
     *
     * @param provided the provided string
     * @return the corresponding ability trigger (null if nothing matches)
     */
    @Nullable
    public static AbilityTrigger getFromIdentifier(String provided) {
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
    private ImmutableList<String> toImmutableList(String... strings) {
        List<String> list = new ArrayList<>();

        Collections.addAll(list, strings);
        list.add("?player"); //player is valid on all triggers

        return ImmutableList.copyOf(list);
    }
}
