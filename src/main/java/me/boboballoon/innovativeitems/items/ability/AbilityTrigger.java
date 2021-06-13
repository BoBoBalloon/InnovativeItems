package me.boboballoon.innovativeitems.items.ability;

import org.jetbrains.annotations.Nullable;

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
    RIGHT_CLICK_BLOCK("right-click-block"),

    /**
     * An ability trigger that will always fire the InteractContext.java
     */
    LEFT_CLICK_BLOCK("left-click-block"),

    /**
     * An ability trigger that will always fire the DamageContext.java
     */
    DAMAGE_DEALT("damage-dealt"),

    /**
     * An ability trigger that will always fire the DamageContext.java
     */
    DAMAGE_TAKEN("damage-taken"),

    /**
     * An ability trigger that will always fire the ConsumeContext.java
     */
    CONSUME_ITEM("item-consume");

    private final String identifier;

    AbilityTrigger(String identifier) {
        this.identifier = identifier;
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
}
