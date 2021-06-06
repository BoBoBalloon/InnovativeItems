package me.boboballoon.innovativeitems.items;

/**
 * A class used to show an ability tied to an item
 */
@FunctionalInterface
public interface Ability {
    /**
     * A method that triggers code that is to fire along with an item
     */
    void execute();
}