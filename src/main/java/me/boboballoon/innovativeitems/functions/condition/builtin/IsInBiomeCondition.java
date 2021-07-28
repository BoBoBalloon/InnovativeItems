package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.DamageContext;
import me.boboballoon.innovativeitems.functions.context.InteractContextBlock;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

/**
 * Class that represents a condition in an ability config file that checks if the target is in the selected biome
 */
public class IsInBiomeCondition extends Condition {
    public IsInBiomeCondition() {
        super("isinbiome",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY, FunctionTargeter.BLOCK),
                new ExpectedManual((rawValue, context) -> Biome.valueOf(rawValue.toUpperCase()), "biome"));
    }

    @Override
    protected Boolean call(ImmutableList<Object> arguments, RuntimeContext context) {
        Block block = null;
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);

        if (targeter == FunctionTargeter.PLAYER) {
            block = context.getPlayer().getLocation().getBlock();
        }

        if (targeter == FunctionTargeter.ENTITY && context instanceof DamageContext) {
            DamageContext damageContext = (DamageContext) context;
            block = damageContext.getEntity().getLocation().getBlock();
        }

        if (targeter == FunctionTargeter.BLOCK && context instanceof InteractContextBlock) {
            InteractContextBlock interactContextBlock = (InteractContextBlock) context;
            block = interactContextBlock.getBlock();
        }

        Biome biome = (Biome) arguments.get(1);

        return biome == block.getBiome();
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
