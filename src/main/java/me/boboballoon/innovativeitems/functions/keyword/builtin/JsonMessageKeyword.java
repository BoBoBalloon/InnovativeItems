package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedEnum;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a keyword in an ability config file that sends an entity a JSON message
 */
public class JsonMessageKeyword extends Keyword {
    public JsonMessageKeyword() {
        super("jsonmessage",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING, "message"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING, "hover message"),
                new ExpectedEnum<>(ClickEvent.Action.class, "click action"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING, "click action value"));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);
        Player target;

        if (targeter == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        } else if (((EntityContext) context).getEntity() instanceof Player) { //if condition above was not true targeter must be entity
            target = (Player) ((EntityContext) context).getEntity();
        } else {
            return;
        }

        TextComponent message = new TextComponent((String) arguments.get(1)); //from legacy text is stupid
        String hoverText = (String) arguments.get(2);

        if (!hoverText.equals("null")) {
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverText)));
        }

        ClickEvent.Action action = (ClickEvent.Action) arguments.get(3);
        String value = (String) arguments.get(4);

        message.setClickEvent(new ClickEvent(action, value));

        target.spigot().sendMessage(message);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
