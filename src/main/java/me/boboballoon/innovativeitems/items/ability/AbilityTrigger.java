package me.boboballoon.innovativeitems.items.ability;

import me.boboballoon.innovativeitems.keywords.keyword.context.DamageContext;
import me.boboballoon.innovativeitems.keywords.keyword.context.InteractContext;
import me.boboballoon.innovativeitems.keywords.keyword.context.RuntimeContext;
import org.jetbrains.annotations.Nullable;

/**
 * A class that contains all possible causes to trigger an ability
 */
public enum AbilityTrigger {
    /**
     * An ability trigger that will always fire the InteractContext.java
     */
    RIGHT_CLICK("right-click", InteractContext.class),

    /**
     * An ability trigger that will always fire the InteractContext.java
     */
    LEFT_CLICK("left-click", InteractContext.class),

    /**
     * An ability trigger that will always fire the DamageContext.java
     */
    DAMAGE_DEALT("damage-dealt", DamageContext.class),

    /**
     * An ability trigger that will always fire the DamageContext.java
     */
    DAMAGE_TAKEN("damage-taken", DamageContext.class);

    private final String identifier;
    private final Class<? extends RuntimeContext> expectedContext;

    AbilityTrigger(String identifier, Class<? extends RuntimeContext> expectedContext) {
        this.identifier = identifier;
        this.expectedContext = expectedContext;
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
     * A method that returns the class of the expected context from this trigger
     *
     * @return the class of the expected context from this trigger
     */
    public Class<? extends RuntimeContext> getExpectedContext() {
        return this.expectedContext;
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
