package me.boboballoon.innovativeitems;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import me.boboballoon.innovativeitems.command.InnovativeItemsCommand;
import me.boboballoon.innovativeitems.config.ConfigManager;
import me.boboballoon.innovativeitems.functions.FunctionManager;
import me.boboballoon.innovativeitems.functions.keyword.builtin.*;
import me.boboballoon.innovativeitems.items.AbilityTimerManager;
import me.boboballoon.innovativeitems.items.GarbageCollector;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.listeners.AbilityTriggerListeners;
import me.boboballoon.innovativeitems.listeners.BlockPlaceableListener;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class InnovativeItems extends JavaPlugin {
    private static InnovativeItems instance;

    private PaperCommandManager commandManager;
    private ConfigManager configManager;
    private FunctionManager functionManager;
    private InnovativeCache cache;
    private AbilityTimerManager timerManager;
    private GarbageCollector garbageCollector;

    /*
    TODO LIST:
    -2. Test if the \ character works before a ( in the message keyword
    -1. Build some conditions and test if they work
    0. Add everything new to the docs (check added list below) and push a new update
    1. Add support for anonymous abilities (ability that are in the item config section with no name and not stored in cache)
        a. Make AbstractAbility abstract class with everything except name
        b. Extend AbstractAbility in normal ability class and add name field and build new AnonymousAbility class with no changes
        c. Make separate method in AbilityParser for anonymous abilities like-
        AbilityParser.parseAnonymousAbility(ConfigurationSection section, CustomItem item), make ability superclass have replacement for name
    2. Add example configs that are generated on reload (put option in main config to disable)
    (new update at this point 2.0)
    3. Contact striker2ninja@gmail.com to make a youtube video on the plugin (https://www.youtube.com/c/SoulStriker)
    4. Add support for custom blocks
        (LOOK INTO NBTBlock OBJECT BEFORE MAKING CACHE AND ALL THAT BULLSHIT)
        a. Cache all custom blocks in a map "Map<Location, CustomBlock>"
        b. Listen for all block events to make sure nobody can fuck with locations
        c. When server stops serialize the map as a json file
        d. When server starts read from json file and keep everything in memory
        e. Add block support to garbage collector
          a. GarbageCollector.checkBlock(CustomBlock block)
          b. GarbageCollector.checkBlocks(Set<CustomBlock> blocks) call the .checkBlock(CustomBlock block) method
          d. GarbageCollector.checkAllBlocks() make sure to grab all blocks in cache and call the .checkBlocks(Set<CustomBlock> blocks) method
        f. Add support for block abilities (keep chunks loaded maybe???)
     (new update 3.0)
     maybe make a new premium plugin that hooks into this one that is an in game GUI to build item and ability config files?
     */

    /*
    CHANGE LIST:
    1. KeywordContext is now known as FunctionContext
    2. KeywordTargeter is now known as FunctionTargeter
    3. Keyword call method is now known as calling
    4. KeywordManager is now known as FunctionManager
    5. Valid keyword and condition names can only contain (a-z or A-Z or _)
    6. The escape character can now be used on open parenthesis "("
    7. Fixed bug where DEV and NOISE level debug would not appear in console
     */

    @Override
    public void onLoad() {
        //instance init
        InnovativeItems.instance = this;

        //load up and register all keywords
        this.functionManager = new FunctionManager();

        this.functionManager.registerKeywords(new DelayKeyword(), new DamageKeyword(), new HealKeyword(), new ParticleKeyword(),
                new MessageKeyword(), new EffectKeyword(), new AbilityKeyword(), new CommandKeyword(),
                new RandomAbilityKeyword(), new DamagePercentKeyword(), new HealPercentKeyword(), new SetHealthKeyword(),
                new FeedKeyword(), new LightningKeyword(), new KindleKeyword(), new PlaySoundKeyword(),
                new GiveItemKeyword(), new GiveCustomItemKeyword(), new RemoveHeldItemKeyword(), new GamemodeKeyword());
    }

    @Override
    public void onEnable() {
        //config manager init
        this.configManager = new ConfigManager();

        //update checker run (if value is true)
        if (this.configManager.shouldCheckForUpdates()) {
            UpdateChecker updateChecker = new UpdateChecker(this);
            updateChecker.checkForUpdates();
        }

        //load up and parse configs
        this.cache = new InnovativeCache();
        this.timerManager = new AbilityTimerManager();

        this.configManager.init();

        //init garbage collector
        this.garbageCollector = new GarbageCollector(this.configManager.shouldUpdateItems(), this.configManager.shouldDeleteItems());

        //register commands and conditions
        LogUtil.log(LogUtil.Level.INFO, "Registering commands...");
        this.commandManager = new PaperCommandManager(this);

        this.commandManager.getCommandConditions().addCondition("is-player", context -> {
            if (!(context.getIssuer().getIssuer() instanceof Player)) {
                throw new ConditionFailedException("This command cannot be run from console!");
            }
        });

        this.commandManager.getCommandCompletions().registerAsyncCompletion("valid-items", context -> this.cache.getItemIdentifiers());

        this.commandManager.registerCommand(new InnovativeItemsCommand());

        LogUtil.log(LogUtil.Level.INFO, "Command registration complete!");

        //register listeners
        LogUtil.log(LogUtil.Level.INFO, "Registering event listeners...");

        this.registerListeners(this.garbageCollector, new AbilityTriggerListeners(), new BlockPlaceableListener());

        LogUtil.log(LogUtil.Level.INFO, "Event listener registration complete!");
    }

    /**
     * A method used to return the active instance of the plugin
     *
     * @return the active instance of the plugin
     */
    public static InnovativeItems getInstance() {
        return InnovativeItems.instance;
    }

    /**
     * A method used to return the active instance of the command manager
     *
     * @return the active instance of the command manager
     */
    public PaperCommandManager getCommandManager() {
        return this.commandManager;
    }

    /**
     * A method used to return the active instance of the cache
     *
     * @return the active instance of the cache
     */
    public InnovativeCache getItemCache() {
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
    public FunctionManager getFunctionManager() {
        return this.functionManager;
    }

    /**
     * A method used to return the active instance of the ability timer manager
     *
     * @return the active instance of the ability timer manager
     */
    public AbilityTimerManager getAbilityTimerManager() {
        return this.timerManager;
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
