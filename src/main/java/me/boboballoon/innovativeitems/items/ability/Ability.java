package me.boboballoon.innovativeitems.items.ability;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.api.AbilityExecuteEvent;
import me.boboballoon.innovativeitems.functions.condition.ActiveCondition;
import me.boboballoon.innovativeitems.functions.context.FlexibleContext;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.BlockContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.ItemContext;
import me.boboballoon.innovativeitems.functions.keyword.ActiveKeyword;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A class used to show an ability tied to an item
 */
public class Ability {
    private final String identifier;
    private final ImmutableList<ActiveKeyword> keywords;
    private final ImmutableList<ActiveCondition> conditions;
    private final AbilityTrigger<?, ?> trigger;
    private final String providedTriggerIdentifier;

    public Ability(@NotNull String identifier, @NotNull ImmutableList<ActiveKeyword> keywords, @NotNull ImmutableList<ActiveCondition> conditions, @NotNull AbilityTrigger<?, ?> trigger, @NotNull String providedTriggerIdentifier) {
        this.identifier = identifier;
        this.keywords = keywords;
        this.conditions = conditions;
        this.trigger = trigger;
        this.providedTriggerIdentifier = providedTriggerIdentifier;
        this.trigger.init(this);
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
    public AbilityTrigger<?, ?> getTrigger() {
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
     * A method used to get the provided identifier for a given ability trigger (only useful for regex usage, where you need to parse this string)
     *
     * @return the provided identifier for a given ability trigger (only useful for regex usage, where you need to parse this string)
     */
    public String getProvidedTriggerIdentifier() {
        return this.providedTriggerIdentifier;
    }

    /**
     * A method used to execute an ability (will always be fired async)
     *
     * @param context the context in which the ability was triggered
     * @return a boolean that is true when the ability executed successfully
     */
    public boolean execute(@NotNull RuntimeContext context) {
        if (Bukkit.getServer().isPrimaryThread()) {
            throw new IllegalStateException("The ability execute method cannot be called from the main thread!");
        }

        if (this.shouldWrapContext(context)) {
            context = FlexibleContext.wrap(context);
        } else if (!this.trigger.getContextClass().isInstance(context)) {
            LogUtil.log(LogUtil.Level.NOISE, "Ability: " + this.identifier + " failed to execute due to an incompatible runtime context. (if safety is not an issue try setting the strict field in the main config file to false)");
            return false;
        }

        AbilityExecuteEvent event = new AbilityExecuteEvent(context);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            LogUtil.log(LogUtil.Level.NOISE, "Ability: " + this.identifier + " failed to execute due to the ability execute event being cancelled.");
            return false;
        }

        for (ActiveCondition condition : this.conditions) {
            Boolean value = condition.execute(context);

            if (value == null) {
                LogUtil.log(LogUtil.Level.SEVERE, "There was an error trying to execute the " + this.identifier + " ability because the condition " + condition.getBase().getIdentifier() + " returned null!");
                return false;
            }

            //both must be opposites (when value is true, inverted must be false)
            if (value == condition.isInverted()) {
                LogUtil.log(LogUtil.Level.NOISE, "Condition: " + condition.getBase().getIdentifier() + " failed on the " + this.identifier + " ability.");
                return false;
            }
        }

        for (ActiveKeyword keyword : this.keywords) {
            keyword.execute(context);
        }

        return true;
    }

    /**
     * A method used to execute an ability (will always be fired async)
     *
     * @param player the player to execute the ability
     * @return a boolean that is true when the ability executed successfully
     */
    public boolean execute(@NotNull Player player) {
        RuntimeContext context = new RuntimeContext(player, this);

        return this.execute(context);
    }

    /**
     * A method that is used to check if the runtime context should be wrapped in a flexible context object
     *
     * @param context the current runtime context
     * @return if the current runtime context should be wrapped
     */
    private boolean shouldWrapContext(RuntimeContext context) {
        if (InnovativeItems.getInstance().getConfigManager().isStrict()) {
            return false;
        }

        return !((context instanceof EntityContext && context instanceof BlockContext && context instanceof ItemContext) || context instanceof FlexibleContext);
    }
}