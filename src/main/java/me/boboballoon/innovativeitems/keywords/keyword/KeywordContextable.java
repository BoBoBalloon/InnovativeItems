package me.boboballoon.innovativeitems.keywords.keyword;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of KeywordContext interface
 */
public class KeywordContextable implements KeywordContext {
    //required
    private final String[] context;
    private final Player player;
    private final String abilityName;

    //optional
    @Nullable
    private final LivingEntity target;

    public KeywordContextable(String[] context, Player player, String abilityName, @Nullable LivingEntity target) {
        this.context = context;
        this.player = player;
        this.abilityName = abilityName;
        this.target = target;
    }

    public KeywordContextable(String[] context, Player player, String abilityName) {
        this(context, player, abilityName, null);
    }

    @Override
    public String[] getContext() {
        return this.context;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public String getAbilityName() {
        return this.abilityName;
    }

    @Override
    @Nullable
    public LivingEntity getTargetDamaged() {
        return this.target;
    }
}
