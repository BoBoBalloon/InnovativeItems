package me.boboballoon.innovativeitems.keywords.builtin;

import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.context.DamageContext;
import me.boboballoon.innovativeitems.keywords.keyword.context.KeywordContext;
import me.boboballoon.innovativeitems.keywords.keyword.context.RuntimeContext;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class DamageKeyword extends Keyword {
    public DamageKeyword() {
        super("damage", 2);
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
            LogUtil.log(Level.WARNING, "There is not a valid living entity currently present on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return;
        }

        double amount = (Double) arguments.get(1);

        target.damage(amount);
    }

    @Override
    public List<Object> load(KeywordContext context) {
        String[] raw = context.getContext();
        List<Object> args = new ArrayList<>();

        args.add(raw[0]);

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
