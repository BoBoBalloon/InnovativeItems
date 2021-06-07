package me.boboballoon.innovativeitems;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import me.boboballoon.innovativeitems.command.InnovativeItemsCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class InnovativeItems extends JavaPlugin {
    private static InnovativeItems instance;

    private BukkitCommandManager commandManager;
    private ConfigManager configManager;
    private InnovativeCache cache;

    /*
    TODO LIST:
    1. Add garbage collector (item updater) (check items when player opens container or joins and check if any custom items do not match the cached items)
    2. Build auto-updater (check spigot and see if update has come out)
    3. Add extra item options (leather armor dye and player head skins)
    4. Build keywords + keyword api (do something along the lines of the EnchantmentManager class in the enchantment plugin)
    5. Finish ability parser (in ConfigManager.java)
    6. Add obfuscation to maven
     */

    @Override
    public void onEnable() {
        InnovativeItems.instance = this;

        //config manager
        this.configManager = new ConfigManager();

        //add auto updater here

        //load up and parse configs
        this.cache = new InnovativeCache();

        this.configManager.init();

        //register commands and conditions
        this.commandManager = new BukkitCommandManager(this);

        this.commandManager.getCommandConditions().addCondition("is-player", context -> {
            if (!(context.getIssuer().getIssuer() instanceof Player)) {
                throw new ConditionFailedException("This command cannot be run from console!");
            }
        });

        this.commandManager.getCommandCompletions().registerAsyncCompletion("valid-items", context -> this.cache.getItemIdentifiers());

        this.commandManager.registerCommand(new InnovativeItemsCommand());
    }

    /**
     * A method used to return the active instance of the plugin
     *
     * @return the active instance of the plugin
     */
    public static InnovativeItems getInstance() {
        return InnovativeItems.instance;
    }

    public BukkitCommandManager getCommandManager() {
        return this.commandManager;
    }

    /**
     * A method used to return the active instance of the cache
     *
     * @return the active instance of the cache
     */
    public InnovativeCache getCache() {
        return this.cache;
    }

    /**
     * A method used to return the active instance of the config manager
     *
     * @return the active instance of the config manager
     */
    public ConfigManager getConfigManager() {
        return this.configManager;
    }
}
