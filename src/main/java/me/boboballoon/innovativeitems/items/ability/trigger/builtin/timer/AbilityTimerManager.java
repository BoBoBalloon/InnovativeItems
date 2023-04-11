package me.boboballoon.innovativeitems.items.ability.trigger.builtin.timer;

import me.boboballoon.innovativeitems.items.ability.Ability;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that is responsible for holding ability trigger timers in memory during runtime
 */
public final class AbilityTimerManager {
    private final Map<Long, AbilityTimerClock> timers;

    public AbilityTimerManager() {
        this.timers = new HashMap<>();
    }

    /**
     * A method used to place an ability in an existing timer or build a new one
     *
     * @param ability the ability that needs a timer
     * @param timer the delay in between executions
     */
    public void registerTimer(@NotNull Ability ability, long timer) {
        if (!(ability.getTrigger() instanceof TimerTrigger)) {
            return;
        }

        if (this.timers.containsKey(timer)) {
            this.timers.get(timer).addAbility(ability);
            return;
        }

        this.timers.put(timer, new AbilityTimerClock(timer, ability));
    }

    /**
     * A method used to wipe all data currently in the cache
     */
    public void clearCache() {
        for (AbilityTimerClock trigger : this.timers.values()) {
            trigger.cancel();
        }

        this.timers.clear();
    }
}
