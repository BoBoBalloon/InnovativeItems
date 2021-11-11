package me.boboballoon.innovativeitems.config;

/**
 * An enumerated list of the stages of initialization
 */
public enum InitializationStage {
    /**
     * Init incomplete items
     */
    INCOMPLETE_ITEMS(1),
    /**
     * Init incomplete abilities
     */
    INCOMPLETE_ABILITIES(2),
    /**
     * Iterate through the incomplete abilities and register them, but also make sure none of the keywords depend on another ability or item, if item, WAIT, if ability, send to the back of the list
     */
    COMPLETE_NON_DEPENDANT_ABILITIES(3),
    /**
     * Iterate through the incomplete items and if the ability they depend on is registered, register them as well
     */
    COMPLETE_NON_DEPENDANT_ITEMS(4),
    /**
     * Iterate through the incomplete abilities that depend on items, if the item they depend on is registered, register them as well, if not, add them to a new waiting list
     */
    COMPLETE_DEPENDANT_ABILITIES(5),
    /**
     * Iterate through the incomplete items that depend on abilities, if the ability they depend on is registered, register them as well, if not, add them to a new waiting list
     */
    COMPLETE_DEPENDANT_ITEMS(6),
    /**
     * Iterate through the incomplete abilities and check if the items they depend on also depend on the very same ability, if so remove both and throw errors
     */
    FINALIZE_ABILITIES(7),
    /**
     * Iterate through the incomplete abilities and check if the ability the item depended on was removed, if so just set the item
     */
    FINALIZE_ITEMS(8);


    private final int order;

    InitializationStage(int order) {
        this.order = order;
    }

    /**
     * A method used to check if this stage occurs before the given stage
     *
     * @param stage the given stage
     * @return a boolean that is true when this stage occurs before the given stage
     */
    public boolean isBefore(InitializationStage stage) {
        return this.order < stage.getOrder();
    }

    /**
     * A method used to check if this stage occurs after the given stage
     *
     * @param stage the given stage
     * @return a boolean that is true when this stage occurs after the given stage
     */
    public boolean isAfter(InitializationStage stage) {
        return this.order > stage.getOrder();
    }

    /**
     * A method that returns the number that represents the order the init stages start at
     *
     * @return the order the init stages start at
     */
    private int getOrder() {
        return this.order;
    }
}