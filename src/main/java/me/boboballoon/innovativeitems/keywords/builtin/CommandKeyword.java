package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedValues;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;

/**
 * Class that represents a keyword in an ability config file that executes a console command
 */
public class CommandKeyword extends Keyword {
    public CommandKeyword() {
        super("command",
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.STRING));
    }

    @Override
    protected void call(ImmutableList<Object> arguments, RuntimeContext context) {
        String rawCommand = (String) arguments.get(0);

        try {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rawCommand.replace("?player", context.getPlayer().getName()));
        } catch (CommandException e) {
            LogUtil.log(LogUtil.Level.WARNING, "There was an error trying to execute the command with the name and arguments of " + rawCommand + " on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
        }
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
