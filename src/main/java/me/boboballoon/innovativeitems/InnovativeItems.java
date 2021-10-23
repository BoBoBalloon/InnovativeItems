package me.boboballoon.innovativeitems;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import me.boboballoon.innovativeitems.command.InnovativeItemsCommand;
import me.boboballoon.innovativeitems.config.ConfigManager;
import me.boboballoon.innovativeitems.functions.FunctionManager;
import me.boboballoon.innovativeitems.functions.condition.builtin.*;
import me.boboballoon.innovativeitems.functions.keyword.builtin.*;
import me.boboballoon.innovativeitems.functions.keyword.builtin.dependent.MythicMobSkillKeyword;
import me.boboballoon.innovativeitems.items.AbilityTimerManager;
import me.boboballoon.innovativeitems.items.GarbageCollector;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.items.ItemDefender;
import me.boboballoon.innovativeitems.listeners.AbilityTriggerListeners;
import me.boboballoon.innovativeitems.listeners.BlockPlaceableListener;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class of the innovative items plugin
 */
public final class InnovativeItems extends JavaPlugin {
    private static InnovativeItems instance;
    private PaperCommandManager commandManager;
    private ConfigManager configManager;
    private FunctionManager functionManager;
    private InnovativeCache cache;
    private AbilityTimerManager timerManager;
    private GarbageCollector garbageCollector;
    private ItemDefender itemDefender;

    /*
    TODO LIST:
    REMEMBER TO CHANGE THE isPluginPremium METHOD
    1. Look at custom blocks here: https://github.com/xenondevs/Nova/blob/main/src/main/kotlin/xyz/xenondevs/nova/tileentity/TileEntityManager.kt
    2. Contact contact@crysis.tv to make a youtube video on the plugin (https://www.youtube.com/c/TheCrysisYT/)
    3. Contact striker2ninja@gmail.com to make a youtube video on the plugin (https://www.youtube.com/c/SoulStriker)
     */

    /*
    CHANGE LIST:
     */

    /**
     * A quick method used to check if the plugin is the free or premium version
     *
     * @return a boolean that is true if the plugin is the premium version
     */
    public static boolean isPluginPremium() {
        return false;
    }

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
                new GiveItemKeyword(), new GiveCustomItemKeyword(), new RemoveHeldItemKeyword(), new GamemodeKeyword(),
                new VelocityKeyword(), new SwitcherooKeyword(), new ActionbarKeyword(), new TitleMessageKeyword(),
                new TeleportKeyword(), new TeleportToKeyword(), new TimeKeyword(), new SudoKeyword(),
                new DropItemKeyword(), new DropCustomItemKeyword(), new ExplodeKeyword(), new ShootProjectileKeyword(),
                new SetEquipmentSlotKeyword(), new LungeKeyword());

        this.functionManager.registerConditions(new IsClearWeatherCondition(), new IsInBiomeCondition(), new IsHeathAtCondition(), new IsTimeCondition(),
                new IsPermissionPresentCondition(), new IsGamemodeCondition(), new IsHoldingItemCondition(), new IsHoldingCustomItemCondition(),
                new IsWearingItemCondition(), new IsWearingCustomItemCondition(), new IsPlayerCondition(), new IsSneakingCondition(),
                new IsBlockingCondition(), new IsGlidingCondition(), new IsEntityTypeCondition(), new IsBlockCondition(),
                new IsBlockAtCondition(), new IsFacingCondition());

        //dependent functions

        this.functionManager.registerKeyword("MythicMobs", new MythicMobSkillKeyword());
    }

    @Override
    public void onEnable() {
        String word = InnovativeItems.isPluginPremium() ? "premium" : "free";
        LogUtil.logUnblocked(LogUtil.Level.INFO, "Loading up the " + word + " version of the plugin...");

        //config manager init
        this.configManager = new ConfigManager();

        //update checker run (if value is true)
        if (this.configManager.shouldCheckForUpdates()) {
            UpdateChecker updateChecker = new UpdateChecker(this);
            updateChecker.checkForUpdates();
        }

        //register commands and conditions
        LogUtil.log(LogUtil.Level.INFO, "Registering commands...");
        this.commandManager = new PaperCommandManager(this);

        this.commandManager.getCommandConditions().addCondition("is-player", context -> {
            if (!(context.getIssuer().getIssuer() instanceof Player)) {
                throw new ConditionFailedException("This command cannot be run from console!");
            }
        });

        this.commandManager.getCommandCompletions().registerAsyncCompletion("valid-items", context -> this.cache.getItemIdentifiers());
        this.commandManager.getCommandCompletions().registerAsyncCompletion("valid-abilities", context -> this.cache.getAbilityIdentifiers());

        this.commandManager.registerCommand(new InnovativeItemsCommand());

        LogUtil.log(LogUtil.Level.INFO, "Command registration complete!");

        //load up and parse configs
        this.cache = new InnovativeCache();
        this.timerManager = new AbilityTimerManager();

        this.configManager.init();

        //init garbage collector
        this.garbageCollector = new GarbageCollector(this.configManager.shouldUpdateItems(), this.configManager.shouldDeleteItems());

        //init item defender
        this.itemDefender = new ItemDefender(this.configManager.shouldAllowCrafting(), this.configManager.shouldAllowSmelting(), this.configManager.shouldAllowBrewing());

        //register listeners
        LogUtil.log(LogUtil.Level.INFO, "Registering event listeners...");

        this.registerListeners(this.garbageCollector, new AbilityTriggerListeners(), new BlockPlaceableListener(), this.itemDefender);

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
     * A method used to return the active instance of the item defender
     *
     * @return the active instance of the item defender
     */
    public ItemDefender getItemDefender() {
        return this.itemDefender;
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
