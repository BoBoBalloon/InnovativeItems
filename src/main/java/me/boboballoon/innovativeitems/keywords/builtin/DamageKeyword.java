package me.boboballoon.innovativeitems.keywords.builtin;

import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordArgument;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DamageKeyword extends Keyword {
    public DamageKeyword() {
        super("damage", KeywordArgument.BLANK, new KeywordArgument("0"));
    }

    @Override
    public void execute(KeywordContext context) {
        String[] args = context.getContext();

        String targetRaw = args[0];
        Player target;
        if (targetRaw.equalsIgnoreCase("@player")) {
            target = context.getPlayer();
        } else {
            target = Bukkit.getPlayerExact(targetRaw);
        }

        if (target == null) {
            return;
        }

        int damage;
        try {
            damage = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return;
        }

        target.damage(damage);
    }
}
