package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a keyword in an ability config file that executes a console command
 */
public class CommandKeyword extends Keyword {
    public CommandKeyword() {
        super("command",
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        String rawCommand = (String) arguments.get(0);
        String parsed = rawCommand.replace("?player", context.getPlayer().getName());

        if (context instanceof EntityContext) {
            LivingEntity entity = ((EntityContext) context).getEntity();
            parsed = parsed.replace("?entity", entity instanceof Player ? entity.getName() : entity.getUniqueId().toString());
        }

        try {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
        } catch (CommandException e) {
            LogUtil.log(LogUtil.Level.WARNING, "There was an error trying to execute the command with the name and arguments of " + rawCommand + " on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
        }
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
