package me.boboballoon.innovativeitems.items.ability;

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
    RIGHT_CLICK("right-click"),

    /**
     * An ability trigger that will always fire the InteractContext.java
     */
    LEFT_CLICK("left-click"),

    /**
     * An ability trigger that will always fire the InteractContext.java
     */
    RIGHT_CLICK_BLOCK("right-click-block", "?block"),

    /**
     * An ability trigger that will always fire the InteractContext.java
     */
    LEFT_CLICK_BLOCK("left-click-block", "?block"),

    /**
     * An ability trigger that will always fire the DamageContext.java
     */
    DAMAGE_DEALT("damage-dealt", "?entity"),

    /**
     * An ability trigger that will always fire the DamageContext.java
     */
    DAMAGE_TAKEN("damage-taken", "?entity"),

    /**
     * An ability trigger that will always fire the ConsumeContext.java
     */
    CONSUME_ITEM("item-consume");

    private final String identifier;
    private final List<String> allowedTargeters;

    AbilityTrigger(String identifier, String... allowedTargeters) {
        this.identifier = identifier;
        this.allowedTargeters = this.toList(allowedTargeters);
        this.allowedTargeters.add("?player"); //player is valid on all triggers
    }

    /**
     * A method that returns the identifier that is used to parse the ability trigger
     *
     * @return the identifier that is used to parse the ability trigger
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * A method that returns all allowed targeters used for this trigger
     *
     * @return all allowed targeters used for this trigger
     */
    public List<String> getAllowedTargeters() {
        return this.allowedTargeters;
    }

    /**
     * A method used to identify an ability trigger via its identifier
     *
     * @param identifier the trigger's identifier
     * @return the corresponding ability trigger (null if nothing matches)
     */
    @Nullable
    public static AbilityTrigger getFromIdentifier(String identifier) {
        for (AbilityTrigger trigger : AbilityTrigger.values()) {
            if (identifier.equalsIgnoreCase(trigger.getIdentifier())) {
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
    private List<String> toList(String... strings) {
        List<String> list = new ArrayList<>();

        Collections.addAll(list, strings);

        return list;
    }
}
