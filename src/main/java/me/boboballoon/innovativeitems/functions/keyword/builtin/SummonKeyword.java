package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedEnum;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.BlockContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a keyword in an ability config file that executes another ability on all nearby entities
 */
public class SummonKeyword extends Keyword {
    public SummonKeyword() {
        super("summon",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY, FunctionTargeter.BLOCK),
                new ExpectedEnum<>(EntityType.class, "entity type"));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);
        Location origin = targeter == FunctionTargeter.ENTITY && context instanceof EntityContext ? ((EntityContext) context).getEntity().getLocation() : targeter == FunctionTargeter.BLOCK && context instanceof BlockContext ? ((BlockContext) context).getBlock().getLocation() : context.getPlayer().getLocation();

        EntityType type = (EntityType) arguments.get(1);

        if (!origin.isWorldLoaded()) {
            LogUtil.log(LogUtil.Level.WARNING, "The world of the " + targeter.getIdentifier() + " targeter on the summon keyword is not loaded!");
            return;
        }

        origin.getWorld().spawnEntity(origin, type);
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
