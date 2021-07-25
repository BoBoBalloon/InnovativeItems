package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.context.DamageContext;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordTargeter;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedTargeters;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * Class that represents a keyword in an ability config file that sets the targets gamemode
 */
public class GamemodeKeyword extends Keyword {
    public GamemodeKeyword() {
        super("gamemode",
                new ExpectedTargeters(KeywordTargeter.PLAYER, KeywordTargeter.ENTITY),
                new ExpectedManual(((rawValue, context) -> GameMode.valueOf(rawValue.toUpperCase())), "gamemode"));
    }

    @Override
    protected void call(ImmutableList<Object> arguments, RuntimeContext context) {
        Player target = null;
        KeywordTargeter rawTarget = (KeywordTargeter) arguments.get(0);

        if (rawTarget == KeywordTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (rawTarget == KeywordTargeter.ENTITY && context instanceof DamageContext) {
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
