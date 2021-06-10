package me.boboballoon.innovativeitems;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import me.boboballoon.innovativeitems.command.InnovativeItemsCommand;
import me.boboballoon.innovativeitems.config.ConfigManager;
import me.boboballoon.innovativeitems.items.GarbageCollector;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.keywords.KeywordManager;
import me.boboballoon.innovativeitems.keywords.builtin.DamageKeyword;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class InnovativeItems extends JavaPlugin {
    private static InnovativeItems instance;

    private BukkitCommandManager commandManager;
    private ConfigManager configManager;
    private KeywordManager keywordManager;
    private InnovativeCache cache;
    private GarbageCollector garbageCollector;

    /*
    TODO LIST:
    1. Learn regular expressions (regex) so I can parse abilities better
    2. Finish ability parser (in ConfigManager.java)
    3. Add support for mythicmobs and denizens and script
    4. Add obfuscation to maven
    5. Build auto-updater AFTER FIRST PUBLISHED (https://www.spigotmc.org/wiki/creating-an-update-checker-that-checks-for-updates/)
     */

    @Override
    public void onLoad() {
        //instance init
        InnovativeItems.instance = this;

        //load up and register all keywords
        this.keywordManager = new KeywordManager();

        this.keywordManager.registerKeywords(new DamageKeyword());
    }

    @Override
    public void onEnable() {
        //config manager init
        this.configManager = new ConfigManager();

        //auto updater run (if value is true)

        //load up and parse configs
        this.cache = new InnovativeCache();

        this.configManager.init();

        //init garbage collector
        this.garbageCollector = new GarbageCollector(this.configManager.shouldUpdateItems(), this.configManager.shouldDeleteItems());

        //register commands and conditions
        LogUtil.log(Level.INFO, "Registering commands...");
        this.commandManager = new BukkitCommandManager(this);

        this.commandManager.getCommandConditions().addCondition("is-player", context -> {
            if (!(context.getIssuer().getIssuer() instanceof Player)) {
                throw new ConditionFailedException("This command cannot be run from console!");
            }
        });

        this.commandManager.getCommandCompletions().registerAsyncCompletion("valid-items", context -> this.cache.getItemIdentifiers());

        this.commandManager.registerCommand(new InnovativeItemsCommand());

        LogUtil.log(Level.INFO, "Command registration complete!");

        //register listeners
        LogUtil.log(Level.INFO, "Registering event listeners...");

        this.registerListeners(this.garbageCollector);

        LogUtil.log(Level.INFO, "Event listener registration complete!");
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

    /**
     * A method used to return the active instance of the garbage collector
     *
     * @return the active instance of the garbage collector
     */
    public GarbageCollector getGarbageCollector() {
        return this.garbageCollector;
    }

    /**
     * A method used to return the active instance of the keyword manager
     *
     * @return the active instance of the keyword manager
     */
    public KeywordManager getKeywordManager() {
        return this.keywordManager;
    }

    /**
     * Dumb util method to avoid repetitive code
     */
    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }
}
