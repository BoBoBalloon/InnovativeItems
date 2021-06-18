package me.boboballoon.innovativeitems.items;

import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.AbilityTimerTrigger;
import me.boboballoon.innovativeitems.items.ability.AbilityTrigger;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that is responsible for holding ability trigger timers in memory during runtime
 */
public final class AbilityTimerManager {
    private final Map<Long, AbilityTimerTrigger> timers;

    public AbilityTimerManager() {
        this.timers = new HashMap<>();
    }

    /**
     * A method used to place an ability in an existing timer or build a new one
     *
     * @param ability the ability that needs a timer
     * @param timer the delay in between executions
     */
    public void registerTimer(Ability ability, long timer) {
        if (ability.getTrigger() != AbilityTrigger.TIMER) {
            return;
        }

        if (this.timers.containsKey(timer)) {
            this.timers.get(timer).addAbility(ability);
            return;
        }

        this.timers.put(timer, new AbilityTimerTrigger(timer, ability));
    }

    /**
     * A method used to wipe all data currently in the cache
     */
    public void clearCache() {
        for (AbilityTimerTrigger trigger : this.timers.values()) {
            trigger.cancel();
        }

        this.timers.clear();
    }
}
