package me.boboballoon.innovativeitems.items.ability;

import org.jetbrains.annotations.Nullable;

/**
 * A class that contains all possible causes to trigger an ability
 */
public enum AbilityTrigger {
    RIGHT_CLICK("right-click"),
    ENTITY_HURT("entity-hurt");

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
