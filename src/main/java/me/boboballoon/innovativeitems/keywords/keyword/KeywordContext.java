package me.boboballoon.innovativeitems.keywords.keyword;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the context in which a keyword was used in
 */
public interface KeywordContext {
    /**
     * A method that returns the array of arguments used for the keyword
     *
     * @return the array of arguments used for the keyword
     */
    String[] getContext();

    /**
     * A method that returns the player responsible for firing the keyword
     *
     * @return the player responsible for firing the keyword
     */
    Player getPlayer();


    /**
     * A method that returns the name of the ability that contains this keyword
     *
     * @return the name of the ability that contains this keyword
     */
    String getAbilityName();


    /**
     * A method that returns the living entity damaged by the player responsible for firing the keyword (if present)
     *
     * @return the living entity damaged by the player responsible for firing the keyword (if present)
     */
    @Nullable
    LivingEntity getTargetDamaged();
}
