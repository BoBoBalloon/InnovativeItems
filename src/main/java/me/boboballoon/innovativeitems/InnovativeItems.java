package me.boboballoon.innovativeitems;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
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
import me.boboballoon.innovativeitems.listeners.CraftingListener;
import me.boboballoon.innovativeitems.listeners.ItemFieldListeners;
import me.boboballoon.innovativeitems.listeners.UIViewListeners;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.ResponseUtil;
import me.boboballoon.innovativeitems.util.UpdateChecker;
import me.boboballoon.innovativeitems.util.armorevent.ArmorListener;
import me.boboballoon.innovativeitems.util.armorevent.DispenserArmorListener;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
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
    1. Test ResponseUtil class
    2. Make item creation gui
    3. Make ability creation gui
    4. Increase maximum amount of items and abilities that can be made on the free version
    5. Exclude any of the default abilities/items from the count and make the maximum values constant instead of whether or not generate default configs is true
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

    /**
     * The method that loads up the plugin before bukkit is initialized; used to register functions and triggers for this plugin's API
     */
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
                new SetBlockAtKeyword(), new SetExperienceKeyword(), new AddExperienceKeyword(), new ModifyDurabilityKeyword(),
                new JsonMessageKeyword(), new GillsKeyword(), new SetYawKeyword(), new SetPitchKeyword(),
                new AddScoreboardTagKeyword(), new RemoveScoreboardTagKeyword(), new SetInvulnerableKeyword(), new LoopKeyword());

        this.functionManager.registerConditions(new IsClearWeatherCondition(), new IsInBiomeCondition(), new IsHeathAtCondition(), new IsTimeCondition(),
                new IsPermissionPresentCondition(), new IsGamemodeCondition(), new IsPlayerCondition(), new IsSneakingCondition(),
                new IsBlockingCondition(), new IsGlidingCondition(), new IsEntityTypeCondition(), new IsBlockCondition(),
                new IsBlockAtCondition(), new IsFacingCondition(), new IsFallingCondition(), new IsInWorldCondition(),
                new IsUsingItemCondition(), new IsUsingCustomItemCondition(), new IsExperienceAtCondition(), new IsHeathPercentAtCondition(),
                new HasPotionEffectCondition(), new IsBurningCondition(), new IsCoordinateCondition(), new HasScoreboardTagCondition(),
                new ChanceCondition(), new HasLineOfSightCondition());

        //dependent functions

        this.functionManager.registerConditions("WorldGuard", new IsInRegionCondition());

        //ability triggers

        this.functionManager.registerTriggers(new BlockBreakTrigger(), new ConsumeItemTrigger(), new CrouchTrigger(), new DamageDealtTrigger(),
                new DamageTakenTrigger(), new LeftClickBlockTrigger(), new LeftClickTrigger(), new NoneTrigger(),
                new RightClickBlockTrigger(), new RightClickTrigger(), new TimerTrigger(), new RightClickEntityTrigger(),
                new ArrowHitEntityTrigger(), new ArrowHitBlockTrigger(), new DoubleLeftClickTrigger(), new DoubleRightClickTrigger());
    }

    /**
     * Method that loads up plugin features and systems that depend on bukkit/spigot to work; this method finishes plugin initialization
     */
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
        this.itemDefender = new ItemDefender(this.configManager.isItemDefenderEnabled());

        //register listeners
        LogUtil.log(LogUtil.Level.INFO, "Registering native event listeners...");

        this.registerListeners(this.garbageCollector, new ItemFieldListeners(), this.itemDefender, new ArmorListener(), new DispenserArmorListener(), new ArrowFireListener(), new CraftingListener(), new UIViewListeners());
        this.functionManager.registerCachedTriggers();
        ResponseUtil.enable();

        LogUtil.log(LogUtil.Level.INFO, "Event listener registration complete!");
    }

    /**
     * Method that executes when plugin is shutting down
     */
    @Override
    public void onDisable() {
        for (String id : this.cache.getItemIdentifiers()) {
            ImmutableList<Recipe> recipes = this.cache.getItem(id).getRecipes();

            if (recipes == null) {
                continue;
            }

            for (Recipe recipe : recipes) {
                if (!(recipe instanceof Keyed)) {
                    LogUtil.log(LogUtil.Level.DEV, "An internal error has occurred, one of the recipes registered on the " + id + " item does not implement the keyed interface!");
                    continue;
                }

                Keyed keyed = (Keyed) recipe;

                if (!Bukkit.removeRecipe(keyed.getKey())) {
                    LogUtil.log(LogUtil.Level.WARNING, "An error occurred while trying to unregister the custom crafting recipe for the " + id + " custom item!");
                }
            }
        }
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
