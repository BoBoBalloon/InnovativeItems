package me.boboballoon.innovativeitems.keywords.context;

import me.boboballoon.innovativeitems.items.ability.AbilityTrigger;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

/**
 * A class that represents context where the item was clicked and targeting a block that can assist execution that cannot be cached and must be parsed during runtime separately
 */
public class InteractContextBlock extends InteractContext {
    private final Block block;

    public InteractContextBlock(Player player, String abilityName, AbilityTrigger abilityTrigger, Action action, EquipmentSlot hand, Block block) {
        super(player, abilityName, abilityTrigger, action, hand);
        this.block = block;
    }

    /**
     * A method that returns the block involved with this context (may be null if not present)
     *
     * @return the block involved with this context (may be null if not present)
     */
    public Block getBlock() {
        return this.block;
    }
}
