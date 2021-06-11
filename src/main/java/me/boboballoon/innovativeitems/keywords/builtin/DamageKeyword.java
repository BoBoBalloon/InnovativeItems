package me.boboballoon.innovativeitems.keywords.builtin;

import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordContext;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class DamageKeyword extends Keyword {
    public DamageKeyword() {
        super("damage", 2);
    }

    @Override
    public void execute(List<Object> arguments) {
        Player target = (Player) arguments.get(0);
        double amount = (Double) arguments.get(1);

        target.damage(amount);
    }

    @Override
    public List<Object> load(KeywordContext context) {
        String[] raw = context.getContext();

        if (raw.length != this.getArgumentsLength()) {
            LogUtil.log(Level.WARNING, "There are not enough arguments to execute the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        List<Object> args = new ArrayList<>();

        LivingEntity target;
        if (raw[0].equalsIgnoreCase("@player")) {
            target = context.getPlayer();
        } else if (raw[0].equalsIgnoreCase("@target") && context.getTargetDamaged() != null) {
            target = context.getTargetDamaged();
        } else {
            target = null;
        }

        if (target == null) {
            LogUtil.log(Level.WARNING, "There is not a valid player entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        args.add(target);

        int amount;
        try {
            amount = Integer.parseInt(raw[1]);
        } catch (NumberFormatException e) {
            LogUtil.log(Level.WARNING, "There is not a valid damage entered on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
            return null;
        }

        args.add(amount);

        return args;
    }
}
