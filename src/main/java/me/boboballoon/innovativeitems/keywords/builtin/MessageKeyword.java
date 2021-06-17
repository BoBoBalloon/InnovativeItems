package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordContext;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordTargeter;
import me.boboballoon.innovativeitems.keywords.keyword.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.context.DamageContext;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.TextUtil;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Class that represents a keyword in an ability config file that sends a message to a selected target
 */
public class MessageKeyword extends Keyword {
    public MessageKeyword() {
        super("message", true, false);
    }

    @Override
    public void execute(List<Object> arguments, RuntimeContext context) {
        LivingEntity target = null;
        KeywordTargeter rawTarget = (KeywordTargeter) arguments.get(0);

        if (rawTarget == KeywordTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (context instanceof DamageContext && rawTarget == KeywordTargeter.ENTITY) {
            DamageContext damageContext = (DamageContext) context;
            target = damageContext.getEntity();
        }

        if (target == null) {
            LogUtil.log(Level.WARNING, "There is not a valid living entity currently present on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability! Are you sure the target and trigger are valid together?");
            return;
        }

        String message = (String) arguments.get(1);

        target.sendMessage(message);
    }

    @Override
    @Nullable
    public List<Object> load(KeywordContext context) {
        String[] raw = context.getContext();
        List<Object> args = new ArrayList<>();

        KeywordTargeter rawTarget = KeywordTargeter.getFromIdentifier(raw[0]);

        if (rawTarget != KeywordTargeter.PLAYER && rawTarget != KeywordTargeter.ENTITY) {
            LogUtil.log(Level.WARNING, "There is not a valid target entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        args.add(rawTarget);

        String message = TextUtil.format(raw[1]);

        args.add(message);

        return args;
    }

    @Override
    public ImmutableList<String> getValidTargeters() {
        return ImmutableList.of("?player", "?entity");
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
