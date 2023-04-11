package me.boboballoon.innovativeitems.items.item;

import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

/**
 * An enum that represents a valid recipe type that is supported by the {@link me.boboballoon.innovativeitems.InnovativeItems} plugin
 */
public enum RecipeType {
    /**
     * Normal crafting {@link org.bukkit.inventory.ShapedRecipe}
     */
    SHAPED(ShapedRecipe.class),

    /**
     * Smelting via furnace {@link org.bukkit.inventory.FurnaceRecipe}
     */
    FURNACE(FurnaceRecipe.class),

    /**
     * Smelting via blast furnace {@link org.bukkit.inventory.BlastingRecipe}
     */
    BLAST_FURNACE(BlastingRecipe.class),

    /**
     * Smelting via smoker {@link org.bukkit.inventory.SmokingRecipe}
     */
    SMOKER(SmokingRecipe.class),

    /**
     * Cooking via campfire {@link org.bukkit.inventory.CampfireRecipe}
     */
    CAMPFIRE(CampfireRecipe.class);

    private final Class<? extends Recipe> clazz;

    RecipeType(@NotNull Class<? extends Recipe> clazz) {
        this.clazz = clazz;
    }

    /**
     * A method used to get the recipe class that this type represents
     *
     * @return the recipe class that this type represents
     */
    @NotNull
    public Class<? extends Recipe> getClazz() {
        return this.clazz;
    }
}
