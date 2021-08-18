package me.boboballoon.innovativeitems.items.ability;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.condition.ActiveCondition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.ActiveKeyword;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A class used to show an ability tied to an item
 */
public class Ability {
    private final String identifier;
    private final ImmutableList<ActiveKeyword> keywords;
    private final ImmutableList<ActiveCondition> conditions;
    private final AbilityTrigger trigger;

    public Ability(@NotNull String identifier, @NotNull List<ActiveKeyword> keywords, @NotNull List<ActiveCondition> conditions, @NotNull AbilityTrigger trigger) {
        this.identifier = identifier;
        this.keywords = ImmutableList.copyOf(keywords);
        this.conditions = ImmutableList.copyOf(conditions);
        this.trigger = trigger;
    }

    /**
     * A method that returns the name of the ability
     *
     * @return the name of the ability
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * A method that returns the list of all the keywords of this ability in execution order
     *
     * @return list of all the keywords of this ability in execution order
     */
    public ImmutableList<ActiveKeyword> getKeywords() {
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
     * A method that returns the list of all conditions that must be met for this ability to be executed
     *
     * @return list of all conditions that must be met for this ability to be executed
     */
    public ImmutableList<ActiveCondition> getConditions() {
        return this.conditions;
    }

    /**
     * A method used to execute an ability (will always be fired async)
     *
     * @param context the context in which the ability was triggered
     * @return a boolean that is true when the ability executed successfully
     */
    public boolean execute(RuntimeContext context) {
        if (!this.trigger.getExpectedContext().isInstance(context)) {
            //silently fail
            return false;
        }

        if (Bukkit.getServer().isPrimaryThread()) {
            throw new IllegalStateException("The ability execute method cannot be called from the main thread!");
        }

        for (ActiveCondition condition : this.conditions) {
            Boolean value = condition.execute(context);

            if (value == null) {
                LogUtil.log(LogUtil.Level.SEVERE, "There was an error trying to execute the " + this.identifier + " ability because the condition " + condition.getBase().getIdentifier() + " returned null!");
                return false;
            }

            //both must be opposites (when value is true, inverted must be false)
            if (condition.execute(context) == condition.isInverted()) {
                return false;
            }
        }

        for (ActiveKeyword keyword : this.keywords) {
            keyword.execute(context);
        }
        return true;
    }
}