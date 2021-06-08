package me.boboballoon.innovativeitems.items.item;

import me.boboballoon.innovativeitems.config.keywords.Keyword;

import java.util.Arrays;
import java.util.List;

/**
 * A class used to show an ability tied to an item
 */
public class Ability {
    private final List<Keyword> keywords;

    public Ability(Keyword... keywords) {
        this.keywords = Arrays.asList(keywords);
    }

    /**
     * A method that returns the list of all the keywords of this ability in execution order
     *
     * @return list of all the keywords of this ability in execution order
     */
    public List<Keyword> getKeywords() {
        return this.keywords;
    }
}