package me.boboballoon.innovativeitems.keywords.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordContext;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a keyword in an ability config file that executes a console command
 */
public class CommandKeyword extends Keyword {
    public CommandKeyword() {
        super("command", false);
    }

    @Override
    protected void call(List<Object> arguments, RuntimeContext context) {
        String rawCommand = (String) arguments.get(0);

        try {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rawCommand.replace("?player", context.getPlayer().getName()));
        } catch (CommandException e) {
            LogUtil.log(LogUtil.Level.WARNING, "There was an error trying to execute the command with the name and arguments of " + rawCommand + " on the " + this.getIdentifier() + " keyword on the " + context.getAbilityName() + " ability!");
        }
    }

    @Override
    @Nullable
    public List<Object> load(KeywordContext context) {
        String[] raw = context.getContext();
        List<Object> args = new ArrayList<>();

        String command = raw[0];

        args.add(command);

        return args;
    }

    @Override
    public ImmutableList<String> getValidTargeters() {
        return ImmutableList.of();
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
