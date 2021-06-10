package me.boboballoon.innovativeitems.keywords.keyword;

import org.bukkit.entity.Player;

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
}
