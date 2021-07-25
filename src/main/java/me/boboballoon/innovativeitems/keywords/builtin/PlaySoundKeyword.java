package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.context.DamageContext;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordTargeter;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedValues;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Class that represents a keyword in an ability config file that plays a sound for a player
 */
public class PlaySoundKeyword extends Keyword {
    public PlaySoundKeyword() {
        super("playsound",
                new ExpectedTargeters(KeywordTargeter.PLAYER, KeywordTargeter.ENTITY),
                new ExpectedManual((rawValue, context) -> Sound.valueOf(rawValue.toUpperCase()), "sound"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.FLOAT, "volume"),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.FLOAT, "pitch"));
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

        Sound sound = (Sound) arguments.get(1);
        float volume = (Float) arguments.get(2);
        float pitch = (Float) arguments.get(3);

        target.playSound(target.getLocation(), sound, volume, pitch);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
