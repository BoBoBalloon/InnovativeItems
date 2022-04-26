package me.boboballoon.innovativeitems;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import me.boboballoon.innovativeitems.command.InnovativeItemsCommand;
import me.boboballoon.innovativeitems.config.ConfigManager;
import me.boboballoon.innovativeitems.functions.FunctionManager;
import me.boboballoon.innovativeitems.functions.condition.builtin.*;
import me.boboballoon.innovativeitems.functions.condition.builtin.dependent.IsInRegionCondition;
import me.boboballoon.innovativeitems.functions.keyword.builtin.*;
import me.boboballoon.innovativeitems.items.GarbageCollector;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.items.ItemDefender;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.*;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.projectile.ArrowFireListener;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.projectile.ArrowHitBlockTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.projectile.ArrowHitEntityTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.timer.AbilityTimerManager;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.timer.TimerTrigger;
import me.boboballoon.innovativeitems.listeners.ItemFieldListeners;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.UpdateChecker;
import me.boboballoon.innovativeitems.util.armorevent.ArmorListener;
import me.boboballoon.innovativeitems.util.armorevent.DispenserArmorListener;
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
    0. Equipped custom armor have durability reset on every clean even without editing them MUST FIX
    1. Add new item option (item to item basis) called "lenient" that defaults to false and if true the garbage collector will ignore all changes in the item name and enchantments of custom item and when updating other stuff will keep said changes (also will allow some inventories to the item defender)
    2. Add option for abilities to consume mana cost (hook into MMOCore developer api)
    3. Make custom durability options for custom items
    4. Make new implementation of the ExpectedArguments interface (called ExpectedCollective) that is provided a vararg of ExpectedArguments (keep it as an array, zero null elements) this will be provided the raw string and will parse it using any of the provided implementations, will return an object and switch statement to check for each case
    5. Support variables and replace the ExpectedTargeters return type as the expected return type from the context into a new list and pass that in the ActiveFunction class
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
        return true;
    }

    @Override
    public void onLoad() {
        //instance init
        InnovativeItems.instance = this;

        //load up and register all keywords and conditions
        this.functionManager = new FunctionManager();

        this.functionManager.registerKeywords(new DelayKeyword(), new DamageKeyword(), new HealKeyword(), new ParticleKeyword(),
                new MessageKeyword(), new EffectKeyword(), new AbilityKeyword(), new CommandKeyword(),
                new RandomAbilityKeyword(), new DamagePercentKeyword(), new HealPercentKeyword(), new SetHealthKeyword(),
                new FeedKeyword(), new LightningKeyword(), new KindleKeyword(), new PlaySoundKeyword(),
                new GiveItemKeyword(), new GiveCustomItemKeyword(), new RemoveHeldItemKeyword(), new GamemodeKeyword(),
                new VelocityKeyword(), new SwitcherooKeyword(), new ActionbarKeyword(), new TitleMessageKeyword(),
                new TeleportKeyword(), new TeleportToKeyword(), new TimeKeyword(), new SudoKeyword(),
                new DropItemKeyword(), new DropCustomItemKeyword(), new ExplodeKeyword(), new ShootProjectileKeyword(),
                new SetEquipmentSlotKeyword(), new LungeKeyword(), new RemoveEffectKeyword(), new SetBlockKeyword(),
                new SetBlockAtKeyword(), new SetExperienceKeyword(), new AddExperienceKeyword(), new ModifyDurabilityKeyword());

        this.functionManager.registerConditions(new IsClearWeatherCondition(), new IsInBiomeCondition(), new IsHeathAtCondition(), new IsTimeCondition(),
                new IsPermissionPresentCondition(), new IsGamemodeCondition(), new IsPlayerCondition(), new IsSneakingCondition(),
                new IsBlockingCondition(), new IsGlidingCondition(), new IsEntityTypeCondition(), new IsBlockCondition(),
                new IsBlockAtCondition(), new IsFacingCondition(), new IsFallingCondition(), new IsInWorldCondition(),
                new IsUsingItemCondition(), new IsUsingCustomItemCondition(), new IsExperienceAtCondition(), new IsHeathPercentAtCondition());

        //dependent functions

        this.functionManager.registerConditions("WorldGuard", new IsInRegionCondition());

        //ability triggers

        this.functionManager.registerTriggers(new BlockBreakTrigger(), new ConsumeItemTrigger(), new CrouchTrigger(), new DamageDealtTrigger(),
                new DamageTakenTrigger(), new LeftClickBlockTrigger(), new LeftClickTrigger(), new NoneTrigger(),
                new RightClickBlockTrigger(), new RightClickTrigger(), new TimerTrigger(), new RightClickEntityTrigger(),
                new ArrowHitEntityTrigger(), new ArrowHitBlockTrigger(), new DoubleLeftClickTrigger(), new DoubleRightClickTrigger());
    }

    @Override
    public void onEnable() {
        LogUtil.logUnblocked(LogUtil.Level.INFO, "Loading up the " + (InnovativeItems.isPluginPremium() ? "premium" : "free") + " version of the plugin...");

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
        this.itemDefender = new ItemDefender(this.configManager.isItemDefenderEnabled(), this.configManager.shouldCloseInventories());

        //register listeners
        LogUtil.log(LogUtil.Level.INFO, "Registering native event listeners...");

        this.registerListeners(this.garbageCollector, new ItemFieldListeners(), this.itemDefender, new ArmorListener(), new DispenserArmorListener(), new ArrowFireListener());
        this.functionManager.registerCachedTriggers();

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
