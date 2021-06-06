package me.boboballoon.innovativeitems.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.boboballoon.innovativeitems.config.ConfigManager;
import me.boboballoon.innovativeitems.items.Item;
import me.boboballoon.innovativeitems.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

/**
 * Base command for all subcommands in innovative items
 */
@CommandAlias("innovativeitems||ii")
@CommandPermission("innovativeitems.command")
public class InnovativeItemsCommand extends BaseCommand {

    /**
     * A "command" that gives a player all the possible commands they can execute
     *
     * @param sender the command sender that executed the command
     */
    @CatchUnknown
    @Default
    public void onHelp(CommandSender sender) {
        sender.sendMessage(TextUtil.format("&r&e&lAvailable Commands:"));
        sender.sendMessage(TextUtil.format("&r&e&l- /innovativeitems get <item>"));
        sender.sendMessage(TextUtil.format("&r&e&l- /innovativeitems give <player> <item>"));
        sender.sendMessage(TextUtil.format("&r&e&l- /innovativeitems reload"));
    }

    /**
     * A "command" that gives the executing player a custom item
     *
     * @param player the player that executed the command
     * @param args the args that the player entered
     */
    @Subcommand("get")
    @Conditions("is-player")
    @CommandCompletion("@valid-items @nothing")
    public void onGetItem(Player player, String[] args) {
        if (args.length < 1) {
            TextUtil.sendMessage(player, "&r&cYou have entered improper arguments to execute this command!");
            this.onHelp(player);
            return;
        }

        Item customItem = ConfigManager.ITEMS.get(args[0]);

        if (customItem == null) {
            TextUtil.sendMessage(player, "&r&cYou have entered an item that does not exist!");
            return;
        }

        PlayerInventory inventory = player.getInventory();
        if (inventory.firstEmpty() != -1) {
            inventory.addItem(customItem.getItemStack());
            TextUtil.sendMessage(player, "&r&aAdded " + customItem.getName() + " to your inventory!");
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), customItem.getItemStack());
            TextUtil.sendMessage(player, "&r&aYour inventory was full so " + customItem.getName() + " was dropped on the ground!");
        }
    }

    /**
     * A "command" that gives a player a custom item
     *
     * @param sender the command sender that executed the command
     * @param args the args that the player entered
     */
    @Subcommand("give")
    @CommandCompletion("@players @valid-items @nothing")
    public void onGiveItem(CommandSender sender, String[] args) {
        if (args.length < 2) {
            TextUtil.sendMessage(sender, "&r&cYou have entered improper arguments to execute this command!");
            this.onHelp(sender);
            return;
        }

        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            TextUtil.sendMessage(sender, "&r&cYou have entered a player that is not online!");
            return;
        }

        Item customItem = ConfigManager.ITEMS.get(args[1]);

        if (customItem == null) {
            TextUtil.sendMessage(sender, "&r&cYou have entered an item that does not exist!");
            return;
        }

        PlayerInventory inventory = target.getInventory();
        if (inventory.firstEmpty() != -1) {
            inventory.addItem(customItem.getItemStack());
            TextUtil.sendMessage(target, "&r&aAdded " + customItem.getName() + " to your inventory!");
        } else {
            target.getWorld().dropItemNaturally(target.getLocation(), customItem.getItemStack());
            TextUtil.sendMessage(target, "&r&aYour inventory was full so " + customItem.getName() + " was dropped on the ground!");
        }

        TextUtil.sendMessage(sender, "&r&aGave " + customItem.getName() + " to " + target.getName() + "!");
    }

    /**
     * A "command" used to reload all caches from disk
     *
     * @param sender the command sender that executed the command
     */
    @Subcommand("reload")
    @CommandCompletion("@nothing")
    public void onReload(CommandSender sender) {
        if (sender instanceof Player) {
            TextUtil.sendMessage(sender, "&r&aStarting asynchronous reload in five seconds!");
        }
        ConfigManager.reload();
    }
}
