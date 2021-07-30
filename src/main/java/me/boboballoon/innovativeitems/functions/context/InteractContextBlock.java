package me.boboballoon.innovativeitems.functions.context;

import me.boboballoon.innovativeitems.functions.context.interfaces.BlockContext;
import me.boboballoon.innovativeitems.items.ability.AbilityTrigger;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

/**
 * A class that represents context where the item was clicked and targeting a block that can assist execution that cannot be cached and must be parsed during runtime separately
 */
public class InteractContextBlock extends InteractContext implements BlockContext {
    private final Block block;

    public InteractContextBlock(Player player, String abilityName, AbilityTrigger abilityTrigger, Action action, EquipmentSlot hand, Block block) {
        super(player, abilityName, abilityTrigger, action, hand);
        this.block = block;
    }

    @Override
    public Block getBlock() {
        return this.block;
    }
}
