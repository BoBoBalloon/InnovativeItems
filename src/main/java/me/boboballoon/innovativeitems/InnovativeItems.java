package me.boboballoon.innovativeitems;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.command.InnovativeItemsCommand;
import me.boboballoon.innovativeitems.config.ConfigManager;
import me.boboballoon.innovativeitems.functions.FunctionManager;
import me.boboballoon.innovativeitems.functions.condition.builtin.ChanceCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.HasLineOfSightCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.HasPotionEffectCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.HasScoreboardTagCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsBlockAtCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsBlockCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsBlockingCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsBurningCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsClearWeatherCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsCoordinateCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsEntityTypeCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsExperienceAtCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsFacingCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsFallingCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsGamemodeCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsGlidingCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsHeathAtCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsHeathPercentAtCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsInBiomeCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsInWorldCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsPermissionPresentCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsPlayerCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsSneakingCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsTimeCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsUsingCustomItemCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.IsUsingItemCondition;
import me.boboballoon.innovativeitems.functions.condition.builtin.dependent.IsInRegionCondition;
import me.boboballoon.innovativeitems.functions.keyword.builtin.AbilityKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.ActionbarKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.AddExperienceKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.AddScoreboardTagKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.CommandKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.DamageKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.DamagePercentKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.DelayKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.DropCustomItemKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.DropItemKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.EffectKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.ExplodeKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.FeedKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.GamemodeKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.GillsKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.GiveCustomItemKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.GiveItemKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.HealKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.HealPercentKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.JsonMessageKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.KindleKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.LightningKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.LoopKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.LungeKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.MessageKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.ModifyDurabilityKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.NearbyKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.ParticleKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.PlaySoundKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.RandomAbilityKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.RemoveEffectKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.RemoveHeldItemKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.RemoveScoreboardTagKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.SetBlockAtKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.SetBlockKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.SetEquipmentSlotKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.SetExperienceKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.SetHealthKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.SetInvulnerableKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.SetPitchKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.SetYawKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.ShootProjectileKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.SudoKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.SwitcherooKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.TeleportKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.TeleportToKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.TimeKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.TitleMessageKeyword;
import me.boboballoon.innovativeitems.functions.keyword.builtin.VelocityKeyword;
import me.boboballoon.innovativeitems.items.GarbageCollector;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.items.ItemDefender;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.BlockBreakTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.ConsumeItemTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.CrouchTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.DamageDealtTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.DamageTakenTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.DoubleLeftClickTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.DoubleRightClickTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.LeftClickBlockTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.LeftClickTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.NoneTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.RightClickBlockTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.RightClickEntityTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.RightClickTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.projectile.ArrowFireListener;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.projectile.ArrowHitBlockTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.projectile.ArrowHitEntityTrigger;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.timer.AbilityTimerManager;
import me.boboballoon.innovativeitems.items.ability.trigger.builtin.timer.TimerTrigger;
import me.boboballoon.innovativeitems.items.item.CustomItem;
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
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

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
    -1. Summon keyword that summons mobs at targeters
    0. (Back-burner) Merge ability init and item init into one so we can find what abilities require items and what items require abilities and send error messages on reload instead of during runtime
    1. Add custom sub views to item builder so players do not have to input data with chat as much, mostly do it via other guis
    2. Add custom recipe support to item builder ui (with sub view)
    3. Add support so you can modify custom items as well as create them
    4. (LOOK INTO THIS FIRST, MAYBE AN UPDATE CAME OUT SINCE I AM NOT GETTING THIS ERROR ANYMORE) Get rid of ACF and rewrite commands so they are just with the bukkit command api (ACF throws deprecation warnings on newer versions and customers will spaz out)
    5. Make ability creation gui
    6. Add item field that makes it impossible for user to drop with their hand (add field to gui as well)
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
                new AddScoreboardTagKeyword(), new RemoveScoreboardTagKeyword(), new SetInvulnerableKeyword(), new LoopKeyword(),
                new NearbyKeyword());

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

        this.commandManager.getCommandCompletions().registerAsyncCompletion("valid-items", context -> this.cache.getItems().stream().map(CustomItem::getIdentifier).collect(Collectors.toList()));
        this.commandManager.getCommandCompletions().registerAsyncCompletion("valid-abilities", context -> this.cache.getAbilities().stream().map(Ability::getIdentifier).collect(Collectors.toList()));

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
        for (CustomItem item : this.cache.getItems()) {
            ImmutableList<Recipe> recipes = item.getRecipes();

            if (recipes == null) {
                continue;
            }

            for (Recipe recipe : recipes) {
                if (!(recipe instanceof Keyed)) {
                    LogUtil.log(LogUtil.Level.DEV, "An internal error has occurred, one of the recipes registered on the " + item.getIdentifier() + " item does not implement the keyed interface!");
                    continue;
                }

                Keyed keyed = (Keyed) recipe;

                if (!Bukkit.removeRecipe(keyed.getKey())) {
                    LogUtil.log(LogUtil.Level.WARNING, "An error occurred while trying to unregister the custom crafting recipe for the " + item.getIdentifier() + " custom item!");
                }
            }
        }
    }

    /**
     * A method used to return the active instance of the plugin
     *
     * @return the active instance of the plugin
     */
    @NotNull
    public static InnovativeItems getInstance() {
        if (InnovativeItems.instance == null) {
            throw new IllegalStateException("You cannot get the singleton instance of the plugin before the plugin is loaded!");
        }

        return InnovativeItems.instance;
    }

    /**
     * A method used to return the active instance of the command manager
     *
     * @return the active instance of the command manager
     */
    @NotNull
    public PaperCommandManager getCommandManager() {
        return this.commandManager;
    }

    /**
     * A method used to return the active instance of the cache
     *
     * @return the active instance of the cache
     */
    @NotNull
    public InnovativeCache getItemCache() {
        return this.cache;
    }

    /**
     * A method used to return the active instance of the config manager
     *
     * @return the active instance of the config manager
     */
    @NotNull
    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    /**
     * A method used to return the active instance of the garbage collector
     *
     * @return the active instance of the garbage collector
     */
    @NotNull
    public GarbageCollector getGarbageCollector() {
        return this.garbageCollector;
    }

    /**
     * A method used to return the active instance of the keyword manager
     *
     * @return the active instance of the keyword manager
     */
    @NotNull
    public FunctionManager getFunctionManager() {
        return this.functionManager;
    }

    /**
     * A method used to return the active instance of the ability timer manager
     *
     * @return the active instance of the ability timer manager
     */
    @NotNull
    public AbilityTimerManager getAbilityTimerManager() {
        return this.timerManager;
    }

    /**
     * A method used to return the active instance of the item defender
     *
     * @return the active instance of the item defender
     */
    @NotNull
    public ItemDefender getItemDefender() {
        return this.itemDefender;
    }

    /**
     * Dumb util method to avoid repetitive code
     */
    private void registerListeners(@NotNull Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }
}
