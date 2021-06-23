package me.boboballoon.innovativeitems.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.config.ConfigManager;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.TextUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Base command for all subcommands in innovative items
 */
@CommandAlias("innovativeitems||ii")
@CommandPermission("innovativeitems.command")
public class InnovativeItemsCommand extends BaseCommand {
    private static final List<String> HELP_MESSAGE = Arrays.asList(StringUtils.center(TextUtil.format("&r&e&lAvailable Commands:"), 40),
            TextUtil.format("&r&e&l- /innovativeitems get <item> <amount>"),
            TextUtil.format("&r&e&l- /innovativeitems give <player> <item> <amount> <silent>"),
            TextUtil.format("&r&e&l- /innovativeitems debug <level>"),
            TextUtil.format("&r&e&l- /innovativeitems reload"));

    /**
     * A "command" that gives a player all the possible commands they can execute
     *
     * @param sender the command sender that executed the command
     */
    @CatchUnknown
    @Default
    public void onHelp(CommandSender sender) {
        for (String line : HELP_MESSAGE) {
            sender.sendMessage(line);
        }
    }

    /**
     * A "command" that gives the executing player a custom item
     */
    @Subcommand("get")
    @Conditions("is-player")
    @CommandCompletion("@valid-items @range:1-64 @nothing")
    public void onGetItem(Player player, String[] args) {
        if (args.length < 1 || args.length > 2) {
            TextUtil.sendMessage(player, "&r&cYou have entered improper arguments to execute this command!");
            this.onHelp(player);
            return;
        }

        CustomItem customItem = InnovativeItems.getInstance().getItemCache().getItem(args[0]);

        if (customItem == null) {
            TextUtil.sendMessage(player, "&r&cYou have entered an item that does not exist!");
            return;
        }

        int amount = 1;
        if (args.length == 2) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                TextUtil.sendMessage(player, "&r&cYou have entered an invalid amount!");
                return;
            }
        }

        for (int i = 0; i < amount; i++) {
            player.getInventory().addItem(customItem.getItemStack());
        }

        TextUtil.sendMessage(player, "&r&aAdded " + amount + " " + customItem.getName() + " to your inventory!");
    }

    /**
     * A "command" that gives a player a custom item
     */
    @Subcommand("give")
    @CommandCompletion("@players @valid-items @range:1-64 -s @nothing")
    public void onGiveItem(CommandSender sender, String[] args) {
        if (args.length < 3 || args.length > 4) {
            TextUtil.sendMessage(sender, "&r&cYou have entered improper arguments to execute this command!");
            this.onHelp(sender);
            return;
        }

        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            TextUtil.sendMessage(sender, "&r&cYou have entered a player that is not online!");
            return;
        }

        CustomItem customItem = InnovativeItems.getInstance().getItemCache().getItem(args[1]);

        if (customItem == null) {
            TextUtil.sendMessage(sender, "&r&cYou have entered an item that does not exist!");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            TextUtil.sendMessage(sender, "&r&cYou have entered an invalid amount!");
            return;
        }

        boolean silent = (args.length > 3 && args[3].equalsIgnoreCase("-s"));

        for (int i = 0; i < amount; i++) {
            target.getInventory().addItem(customItem.getItemStack());
        }

        if (!silent) TextUtil.sendMessage(target, "&r&aAdded " + amount + " " + customItem.getName() + " to your inventory!");

        TextUtil.sendMessage(sender, "&r&aGave " + amount + " " + customItem.getName() + " to " + target.getName() + "!");
    }

    /**
     * A "command" used to set the current debug level
     */
    @Subcommand("debug")
    @CommandCompletion("@range:0-3 @nothing")
    public void onDebug(CommandSender sender, String[] args) {
        if (args.length < 1) {
            TextUtil.sendMessage(sender, "&r&cYou have entered improper arguments to execute this command!");
            this.onHelp(sender);
            return;
        }

        int level;
        try {
            level = Integer.parseInt(args[0]);
        } catch (NumberFormatException ignore) {
            TextUtil.sendMessage(sender, "&r&cYou have entered an invalid number!");
            return;
        }

        ConfigManager configManager = InnovativeItems.getInstance().getConfigManager();

        configManager.setDebugLevel(level, true);

        TextUtil.sendMessage(sender, "&r&aYou have set the debug level to " + configManager.getDebugLevel() + "!");
    }

    /**
     * A "command" used to reload all caches from disk
     */
    @Subcommand("reload")
    @CommandCompletion("@nothing")
    public void onReload(CommandSender sender) {
        if (sender instanceof Player) {
            TextUtil.sendMessage(sender, "&r&aStarting asynchronous reload in five seconds!");
        }
        InnovativeItems.getInstance().getConfigManager().reload();
    }
}
