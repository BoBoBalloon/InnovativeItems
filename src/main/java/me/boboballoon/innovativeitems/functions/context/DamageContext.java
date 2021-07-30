package me.boboballoon.innovativeitems.functions.context;

import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.items.ability.AbilityTrigger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * A class that represents context where an entity was damaged that can assist execution that cannot be cached and must be parsed during runtime separately
 */
public class DamageContext extends RuntimeContext implements EntityContext {
    private final LivingEntity entity;
    private final boolean playerDamager;

    public DamageContext(Player player, String abilityName, AbilityTrigger abilityTrigger, LivingEntity entity, boolean playerDamager) {
        super(player, abilityName, abilityTrigger);
        this.entity = entity;
        this.playerDamager = playerDamager;
    }

    @Override
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
