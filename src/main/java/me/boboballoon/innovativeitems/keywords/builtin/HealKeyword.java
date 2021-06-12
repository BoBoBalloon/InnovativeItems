package me.boboballoon.innovativeitems.keywords.builtin;

import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.context.DamageContext;
import me.boboballoon.innovativeitems.keywords.keyword.context.KeywordContext;
import me.boboballoon.innovativeitems.keywords.keyword.context.RuntimeContext;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Class that represents a keyword in an ability config file that damages a selected target
 */
public class HealKeyword extends Keyword {
    public HealKeyword() {
        super("heal", 2);
    }

    @Override
    public void execute(List<Object> arguments, RuntimeContext context) {
        LivingEntity target = null;
        String rawTarget = (String) arguments.get(0);

        if (rawTarget.equalsIgnoreCase("player")) {
            target = context.getPlayer();
        }

        if (context instanceof DamageContext && rawTarget.equalsIgnoreCase("entity")) {
            DamageContext damageContext = (DamageContext) context;
            target = damageContext.getEntity();
        }

        if (target == null) {
            LogUtil.log(Level.WARNING, "There is not a valid living entity currently present on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability! Are you sure the target and trigger are valid together?");
            return;
        }

        double amount = (Double) arguments.get(1);

        if (amount > target.getMaxHealth()) {
            amount = target.getMaxHealth();
        } else if (amount < 0) {
            amount = 0;
        }

        target.setHealth(amount);
    }

    @Override
    @Nullable
    public List<Object> load(KeywordContext context) {
        String[] raw = context.getContext();
        List<Object> args = new ArrayList<>();

        String rawTarget = raw[0];

        if (!rawTarget.equalsIgnoreCase("player") && !rawTarget.equalsIgnoreCase("entity")) {
            LogUtil.log(Level.WARNING, "There is not a valid target entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        args.add(rawTarget);

        double amount;
        try {
            amount = Double.parseDouble(raw[1]);
        } catch (NumberFormatException e) {
            LogUtil.log(Level.WARNING, "There is not a valid damage entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        args.add(amount);

        return args;
    }
}
