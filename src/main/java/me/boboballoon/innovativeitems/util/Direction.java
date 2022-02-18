package me.boboballoon.innovativeitems.util;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A class used to represent all cardinal directions
 */
public enum Direction {
    NORTH,
    NORTHEAST,
    EAST,
    SOUTHEAST,
    SOUTH,
    SOUTHWEST,
    WEST,
    NORTHWEST;

    /**
     * A util method I stole from https://www.spigotmc.org/threads/player-direction.175482/#post-1854571 to get a players direction
     */
    @Nullable
    public static Direction getCardinalDirection(@NotNull Entity entity) {
        double rotation = (entity.getLocation().getYaw() - 90F) % 360F;
        if (rotation < 0.0D) {
            rotation += 360.0D;
        }

        if ((0.0D <= rotation) && (rotation < 22.5D)) {
            return NORTH;
        }

        if ((22.5D <= rotation) && (rotation < 67.5D)) {
            return NORTHEAST;
        }

        if ((67.5D <= rotation) && (rotation < 112.5D)) {
            return EAST;
        }

        if ((112.5D <= rotation) && (rotation < 157.5D)) {
            return SOUTHEAST;
        }

        if ((157.5D <= rotation) && (rotation < 202.5D)) {
            return SOUTH;
        }

        if ((202.5D <= rotation) && (rotation < 247.5D)) {
            return SOUTHWEST;
        }

        if ((247.5D <= rotation) && (rotation < 292.5D)) {
            return WEST;
        }

        if ((292.5D <= rotation) && (rotation < 337.5D)) {
            return NORTHWEST;
        }

        if ((337.5D <= rotation) && (rotation < 360.0D)) {
            return NORTH;
        }

        return null;
    }
}
