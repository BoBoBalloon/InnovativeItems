package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.context.DamageContext;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * Class that represents a keyword in an ability config file that sets the targets gamemode
 */
public class GamemodeKeyword extends Keyword {
    public GamemodeKeyword() {
        super("gamemode",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedManual(((rawValue, context) -> GameMode.valueOf(rawValue.toUpperCase())), "gamemode"));
    }

    @Override
    protected void calling(ImmutableList<Object> arguments, RuntimeContext context) {
        Player target = null;
        FunctionTargeter rawTarget = (FunctionTargeter) arguments.get(0);

        if (rawTarget == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (rawTarget == FunctionTargeter.ENTITY && context instanceof DamageContext) {
            DamageContext damageContext = (DamageContext) context;

            if (!(damageContext.getEntity() instanceof Player)) {
                return;
            }

            target = (Player) damageContext.getEntity();
        }

        GameMode gameMode = (GameMode) arguments.get(1);

        target.setGameMode(gameMode);
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
