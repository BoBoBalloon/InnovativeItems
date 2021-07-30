package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * Class that represents a condition in an ability config file that checks if the target is the set gamemode
 */
public class IsGamemodeCondition extends Condition {
    public IsGamemodeCondition() {
        super("isgamemode",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedManual((rawValue, context) -> GameMode.valueOf(rawValue.toUpperCase()), "gamemode"));
    }

    @Override
    protected Boolean call(ImmutableList<Object> arguments, RuntimeContext context) {
        Player target = null;
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);

        if (targeter == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (targeter == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;

            if (!(entityContext.getEntity() instanceof Player)) {
                return false;
            }

            target = (Player) entityContext.getEntity();
        }

        GameMode gameMode = (GameMode) arguments.get(1);

        return target.getGameMode() == gameMode;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
