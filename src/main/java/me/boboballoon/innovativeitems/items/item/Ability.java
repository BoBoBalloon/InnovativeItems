package me.boboballoon.innovativeitems.items.item;

import me.boboballoon.innovativeitems.keywords.keyword.ActiveKeyword;

import java.util.List;

/**
 * A class used to show an ability tied to an item
 */
public class Ability {
    private final String name;
    private final List<ActiveKeyword> keywords;

    public Ability(String name, List<ActiveKeyword> keywords) {
        this.name = name;
        this.keywords = keywords;
    }

    /**
     * A method that returns the name of the ability
     *
     * @return the name of the ability
     */
    public String getName() {
        return this.name;
    }

    /**
     * A method that returns the list of all the keywords of this ability in execution order
     *
     * @return list of all the keywords of this ability in execution order
     */
    public List<ActiveKeyword> getKeywords() {
        return this.keywords;
    }
}