package me.boboballoon.innovativeitems;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import me.boboballoon.innovativeitems.command.InnovativeItemsCommand;
import me.boboballoon.innovativeitems.config.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class InnovativeItems extends JavaPlugin {
    private static InnovativeItems instance;

    private BukkitCommandManager commandManager;

    /*
    TODO LIST:
    1. Add item updater (check items when player opens container or joins and check if any custom items do not match the cached items)
    2. Build auto-updater
    3. Build abilities
    4. Finish ability parser (in ConfigManager.java)
     */

    @Override
    public void onEnable() {
        InnovativeItems.instance = this;

        //add auto updater here

        //load up and parse configs
        ConfigManager.init();

        //register commands and conditions
        this.commandManager = new BukkitCommandManager(this);

        this.commandManager.getCommandConditions().addCondition("is-player", context -> {
            if (!(context.getIssuer().getIssuer() instanceof Player)) {
                throw new ConditionFailedException("This command cannot be run from console!");
            }
        });

        this.commandManager.getCommandCompletions().registerAsyncCompletion("valid-items", context -> ConfigManager.ITEMS.keySet());

        this.commandManager.registerCommand(new InnovativeItemsCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static InnovativeItems getInstance() {
        return InnovativeItems.instance;
    }

    public BukkitCommandManager getCommandManager() {
        return this.commandManager;
    }
}
