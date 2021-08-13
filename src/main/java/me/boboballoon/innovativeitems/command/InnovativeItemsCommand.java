package me.boboballoon.innovativeitems.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.config.ConfigManager;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.items.ability.Ability;
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
            TextUtil.format("&r&e&l- /innovativeitems reload"),
            TextUtil.format("&r&e&l- /innovativeitems execute <ability> <player>"),
            TextUtil.format("&r&e&l- /innovativeitems garbagecollector clean <player>"));

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

        TextUtil.sendMessage(player, "&r&aAdded " + amount + " " + customItem.getIdentifier() + " to your inventory!");
    }

    /**
     * A "command" that gives a player a custom item
     */
    @Subcommand("give")
    @CommandCompletion("@players @valid-items @range:1-64 -s @nothing")
    public void onGiveItem(CommandSender sender, String[] args) {
        if (args.length < 2 || args.length > 4) {
            TextUtil.sendMessage(sender, "&r&cYou have entered improper arguments to execute this command!");
            this.onHelp(sender);
            return;
        }

        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            TextUtil.sendMessage(sender, "&r&cYou have entered the name of a player that is not online!");
            return;
        }

        CustomItem customItem = InnovativeItems.getInstance().getItemCache().getItem(args[1]);

        if (customItem == null) {
            TextUtil.sendMessage(sender, "&r&cYou have entered an item that does not exist!");
            return;
        }

        int amount;
        try {
            if (args.length > 2) {
                amount = Integer.parseInt(args[2]);
            } else {
                amount = 1;
            }
        } catch (NumberFormatException e) {
            TextUtil.sendMessage(sender, "&r&cYou have entered an invalid amount!");
            return;
        }

        boolean silent = (args.length > 3 && args[3].equalsIgnoreCase("-s"));

        for (int i = 0; i < amount; i++) {
            target.getInventory().addItem(customItem.getItemStack());
        }

        if (!silent) TextUtil.sendMessage(target, "&r&aAdded " + amount + " " + customItem.getIdentifier() + " to your inventory!");

        TextUtil.sendMessage(sender, "&r&aGave " + amount + " " + customItem.getIdentifier() + " to " + target.getName() + "!");
    }

    /**
     * A "command" used to set the current debug level
     */
    @Subcommand("debug")
    @CommandCompletion("@range:0-5 @nothing")
    public void onDebug(CommandSender sender, String[] args) {
        if (args.length != 1) {
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

    /**
     * A "command" used to execute an ability
     */
    @Subcommand("execute")
    @CommandCompletion("@valid-abilities @players @nothing")
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length < 1 || args.length > 2) {
            TextUtil.sendMessage(sender, "&r&cYou have entered improper arguments to execute this command!");
            this.onHelp(sender);
            return;
        }

        if (args.length != 2 && !(sender instanceof Player)) {
            TextUtil.sendMessage(sender, "&r&cYou must be a player to execute this command without a target!");
            this.onHelp(sender);
            return;
        }

        Ability ability = InnovativeItems.getInstance().getItemCache().getAbility(args[0]);

        if (ability == null) {
            TextUtil.sendMessage(sender, "&r&cYou have entered an ability that does not exist!");
            return;
        }

        Player target;
        if (args.length == 2) {
            target = Bukkit.getPlayerExact(args[1]);
        } else {
            target = (Player) sender;
        }

        if (target == null) {
            TextUtil.sendMessage(sender, "&r&cYou have entered the name of a player that is not online!");
            return;
        }

        RuntimeContext context = new RuntimeContext(target, ability);

        Bukkit.getScheduler().runTaskAsynchronously(InnovativeItems.getInstance(), () -> ability.execute(context));

        TextUtil.sendMessage(sender, "&r&aYou have successfully executed the " + ability.getIdentifier() + " ability!");
    }

    /**
     * A "command" used to clean players inventories
     */
    @Subcommand("clean")
    @CommandCompletion("@players @nothing")
    public void onClean(CommandSender sender, String[] args) {
        if (args.length == 1) {
            this.cleanPlayer(sender, args[0]);
        } else {
            TextUtil.sendMessage(sender, "&r&aCleaning up all players inventories!");
            InnovativeItems.getInstance().getGarbageCollector().cleanAllPlayerInventories(true);
        }
    }

    /**
     * A method that cleans a players inventory
     */
    private void cleanPlayer(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayerExact(playerName);

        if (target == null) {
            TextUtil.sendMessage(sender, "&r&cYou have entered the name of a player that is not online!");
            return;
        }

        TextUtil.sendMessage(sender, "&r&aCleaning up the inventory of a player by the name of " + target.getName() + "!");
        InnovativeItems.getInstance().getGarbageCollector().cleanInventory(target.getInventory(), true);
    }
}
