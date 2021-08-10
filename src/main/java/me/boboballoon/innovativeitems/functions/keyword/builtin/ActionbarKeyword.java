package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.util.TextUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

/**
 * Class that represents a keyword in an ability config file that sends an action bar to a player
 */
public class ActionbarKeyword extends Keyword {
    public ActionbarKeyword() {
        super("actionbar",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                ((rawValue, context) -> new TextComponent(TextUtil.format(rawValue))));
    }

    @Override
    protected void calling(ImmutableList<Object> arguments, RuntimeContext context) {
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);
        Player target = null;

        if (targeter == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (targeter == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;

            if (!(entityContext.getEntity() instanceof Player)) {
                return;
            }

            target = (Player) entityContext.getEntity();
        }

        TextComponent message = (TextComponent) arguments.get(1);

        target.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
