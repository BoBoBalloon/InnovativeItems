package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Class that represents a keyword in an ability config file that plays a sound for a player
 */
public class PlaySoundKeyword extends Keyword {
    public PlaySoundKeyword() {
        super("playsound",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedManual((rawValue, context) -> Sound.valueOf(rawValue.toUpperCase()), "sound"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.FLOAT, "volume"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.FLOAT, "pitch"));
    }

    @Override
    protected void calling(ImmutableList<Object> arguments, RuntimeContext context) {
        Player target = null;
        FunctionTargeter rawTarget = (FunctionTargeter) arguments.get(0);

        if (rawTarget == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (rawTarget == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;

            if (!(entityContext.getEntity() instanceof Player)) {
                return;
            }

            target = (Player) entityContext.getEntity();
        }

        Sound sound = (Sound) arguments.get(1);
        float volume = (float) arguments.get(2);
        float pitch = (float) arguments.get(3);

        target.playSound(target.getLocation(), sound, volume, pitch);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
