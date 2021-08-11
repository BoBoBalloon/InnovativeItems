package me.boboballoon.innovativeitems.util;

import org.jetbrains.annotations.Nullable;

/**
 * Class used to determine the time of day
 */
public enum TimeOfDay {
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
