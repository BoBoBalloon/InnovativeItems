package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a keyword in an ability config file that sends a title message to a selected target
 */
public class TitleMessageKeyword extends Keyword {
    public TitleMessageKeyword() {
        super("titlemessage",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "fade in"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "duration"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.INTEGER, "fade out"));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
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

        String title = (String) arguments.get(1);

        String subtitle = (String) arguments.get(2);
        if (subtitle.equals("null")) {
            subtitle = null;
        }

        int fadeIn = (int) arguments.get(3);
        int duration = (int) arguments.get(4);
        int fadeOut = (int) arguments.get(5);

        target.sendTitle(title, subtitle, fadeIn, duration, fadeOut);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
