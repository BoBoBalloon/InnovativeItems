package me.boboballoon.innovativeitems.util;

import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

/**
 * A class used to determine an equipment slot with an additional ANY field
 */
public enum RevisedEquipmentSlot {
    HEAD(EquipmentSlot.HEAD),
    CHEST(EquipmentSlot.CHEST),
    LEGS(EquipmentSlot.LEGS),
    FEET(EquipmentSlot.FEET),
    HAND(EquipmentSlot.HAND),
    OFF_HAND(EquipmentSlot.OFF_HAND),
    ANY(null);

    private final EquipmentSlot slot;

    RevisedEquipmentSlot(EquipmentSlot slot) {
        this.slot = slot;
    }

    /**
     * A method used to return the corresponding equipment slot if applicable
     *
     * @return the corresponding equipment slot if applicable
     */
    @Nullable
    public EquipmentSlot getSlot() {
        return this.slot;
    }

    /**
     * A method that takes a traditional equipment slot and converts it to a revised equipment slot
     *
     * @param slot a traditional equipment slot
     * @return a revised equipment slot
     */
    @Nullable
    public static RevisedEquipmentSlot getFromSlot(EquipmentSlot slot) {
        for (RevisedEquipmentSlot equipmentSlot : RevisedEquipmentSlot.values()) {
            if (slot.name().equals(equipmentSlot.name())) {
                return equipmentSlot;
            }
        }

        return null;
    }
}
