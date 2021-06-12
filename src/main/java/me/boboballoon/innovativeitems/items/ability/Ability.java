package me.boboballoon.innovativeitems.items.ability;

import me.boboballoon.innovativeitems.keywords.keyword.ActiveKeyword;
import me.boboballoon.innovativeitems.keywords.keyword.context.RuntimeContext;

import java.util.List;

/**
 * A class used to show an ability tied to an item
 */
public class Ability {
    private final String name;
    private final List<ActiveKeyword> keywords;
    private final AbilityTrigger trigger;

    public Ability(String name, List<ActiveKeyword> keywords, AbilityTrigger trigger) {
        this.name = name;
        this.keywords = keywords;
        this.trigger = trigger;
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

    /**
     * A method that returns the trigger that fires this ability
     *
     * @return the trigger that fires this ability
     */
    public AbilityTrigger getTrigger() {
        return this.trigger;
    }

    /**
     * A method used to execute an ability
     *
     * @param context the context in which the ability was triggered
     */
    public void execute(RuntimeContext context) {
        for (ActiveKeyword keyword : this.keywords) {
            keyword.execute(context);
        }
    }
}