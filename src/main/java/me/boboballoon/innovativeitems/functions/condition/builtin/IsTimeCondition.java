package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import org.jetbrains.annotations.Nullable;

/**
 * Class that represents a condition in an ability config file that checks the time of day
 */
public class IsTimeCondition extends Condition {
    public IsTimeCondition() {
        super("istime",
                new ExpectedManual((rawValue, context) -> TimeOfDay.valueOf(rawValue.toUpperCase()), "time of day"));
    }

    @Override
    protected Boolean call(ImmutableList<Object> arguments, RuntimeContext context) {
        long time = context.getPlayer().getWorld().getTime();

        TimeOfDay expected = (TimeOfDay) arguments.get(0);
        TimeOfDay current = TimeOfDay.getViaTime(time);

        return expected == current;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    /**
     * Private util class used to determine the time of day
     */
    private enum TimeOfDay {
        MORNING(0L),
        NOON(6000L),
        NIGHT(13000L);

        private final long time;

        TimeOfDay(long time) {
            this.time = time;
        }

        /**
         * Get the long of when this time of day starts
         *
         * @return long of when this time of day starts
         */
        public long getTime() {
            return this.time;
        }

        /**
         * A method used to return the closest time of day to match the provided time
         *
         * @param time the provided time
         * @return closest time of day to match the provided time
         */
        @Nullable
        public static TimeOfDay getViaTime(long time) {
            if (time >= 0 && time < 6000) {
                return MORNING;
            }

            if (time >= 6000 && time < 13000) {
                return NOON;
            }

            if (time >= 13000) {
                return NIGHT;
            }

            return null;
        }
    }
}
