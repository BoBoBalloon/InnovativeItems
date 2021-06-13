package me.boboballoon.innovativeitems.keywords.keyword.context;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

public class InteractContextBlock extends InteractContext {
    private final Block block;

    public InteractContextBlock(Player player, String abilityName, Action action, EquipmentSlot hand, Block block) {
        super(player, abilityName, action, hand);
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
