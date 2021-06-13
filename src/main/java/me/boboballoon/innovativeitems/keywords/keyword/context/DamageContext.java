package me.boboballoon.innovativeitems.keywords.keyword.context;

import me.boboballoon.innovativeitems.keywords.keyword.RuntimeContext;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * A class that represents context where an entity was damaged that can assist execution that cannot be cached and must be parsed during runtime separately
 */
public class DamageContext extends RuntimeContext {
    private final LivingEntity entity;
    private final boolean playerDamager;

    public DamageContext(Player player, String abilityName, LivingEntity entity, boolean playerDamager) {
        super(player, abilityName);
        this.entity = entity;
        this.playerDamager = playerDamager;
    }

    /**
     * A method that returns the living entity that was involved with this context
     *
     * @return the living entity that was involved with this context
     */
    public LivingEntity getEntity() {
        return this.entity;
    }

    /**
     * A method that returns a boolean that is true when the player was the one who dealt the damage
     *
     * @return a boolean that is true when the player was the one who dealt the damage
     */
    public boolean isPlayerDamager() {
        return this.playerDamager;
    }
}
