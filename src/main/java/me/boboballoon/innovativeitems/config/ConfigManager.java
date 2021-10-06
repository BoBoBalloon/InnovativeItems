package me.boboballoon.innovativeitems.config;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.GarbageCollector;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * A class used to cache and parse config files
 */
public final class ConfigManager {
    //update checker
    private boolean checkForUpdates;

    //default configs
    private boolean generateDefaultConfigs;

    //strict mode
    private boolean strict;

    //debug level
    /**
     * Debug level of 5 allows everything
     * Debug level of 4 allows warnings and severes and info and dev
     * Debug level of 3 allows warnings and severes and info
     * Debug level of 2 allows warnings and severes
     * Debug level of 1 allows severes
     * Debug level of 0 allows nothing
     */
    private int debugLevel;

    //garbage collector (copy of values)
    private boolean shouldUpdateLocal;
    private boolean shouldDeleteLocal;

    //item defender (copy of values)
    private boolean allowCraftingLocal;
    private boolean allowSmeltingLocal;
    private boolean allowBrewingLocal;

    public ConfigManager() {
        this.reloadMainConfigValues();
    }

    /**
     * A util method that reloads all values of the main plugin config file
     */
    public void reloadMainConfigValues() {
        Plugin plugin = InnovativeItems.getInstance();
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        //load up update checker values, set to true if none is present
        boolean checkForUpdates;
        if (config.contains("check-for-updates")) {
            checkForUpdates = config.getBoolean("check-for-updates");
        } else {
            checkForUpdates = true;
            config.set("check-for-updates", true);
        }
        this.setCheckForUpdates(checkForUpdates);

        //load up default config values, set to true if none is present
        boolean generateDefaultConfigs;
        if (config.contains("generate-default-configs")) {
            generateDefaultConfigs = config.getBoolean("generate-default-configs");
        } else {
            generateDefaultConfigs = true;
            config.set("generate-default-configs", true);
        }
        this.setGenerateDefaultConfigs(generateDefaultConfigs);

        //strict mode
        boolean strict;
        if (config.contains("strict")) {
            strict = config.getBoolean("strict");
        } else {
            strict = true;
            config.set("strict", true);
        }
        this.setStrict(strict);

        //load up debug level, sets to 2 if no value is present
        int debugLevel;
        if (config.contains("debug-level")) {
            debugLevel = config.getInt("debug-level");
        } else {
            debugLevel = 2;
            config.set("debug-level", 2);
        }
        this.setDebugLevel(debugLevel, false);

        //load up garbage collector should update option, sets to true if no value is present
        boolean shouldUpdate;
        if (config.contains("garbage-collector.should-update")) {
            shouldUpdate = config.getBoolean("garbage-collector.should-update");
        } else {
            shouldUpdate = true;
            config.set("garbage-collector.should-update", true);
        }
        this.setShouldUpdate(shouldUpdate);

        //load up garbage collector should delete option, sets to true if no value is present
        boolean shouldDelete;
        if (config.contains("garbage-collector.should-delete")) {
            shouldDelete = config.getBoolean("garbage-collector.should-delete");
        } else {
            shouldDelete = true;
            config.set("garbage-collector.should-delete", true);
        }
        this.setShouldDelete(shouldDelete);

        //load up item defender allow crafting option, sets to true if no value is present
        boolean allowCrafting;
        if (config.contains("item-defender.allow-crafting")) {
            allowCrafting = config.getBoolean("item-defender.allow-crafting");
        } else {
            allowCrafting = false;
            config.set("item-defender.allow-crafting", false);
        }
        this.setShouldAllowCrafting(allowCrafting);

        //load up item defender allow smelting option, sets to true if no value is present
        boolean allowSmelting;
        if (config.contains("item-defender.allow-smelting")) {
            allowSmelting = config.getBoolean("item-defender.allow-smelting");
        } else {
            allowSmelting = false;
            config.set("item-defender.allow-smelting", false);
        }
        this.setShouldAllowSmelting(allowSmelting);

        //load up item defender allow brewing option, sets to true if no value is present
        boolean allowBrewing;
        if (config.contains("item-defender.allow-brewing")) {
            allowBrewing = config.getBoolean("item-defender.allow-brewing");
        } else {
            allowBrewing = false;
            config.set("item-defender.allow-brewing", false);
        }
        this.setShouldAllowBrewing(allowBrewing);

        plugin.saveConfig();
    }

    /**
     * A method that returns true when the plugin should check for updates on startup
     *
     * @return true when the plugin should check for updates on startup
     */
    public boolean shouldCheckForUpdates() {
        return this.checkForUpdates;
    }

    /**
     * A method that is used to set whether the plugin should check for updates on startup
     *
     * @param checkForUpdates whether the plugin should check for updates on startup
     */
    public void setCheckForUpdates(boolean checkForUpdates) {
        this.checkForUpdates = checkForUpdates;
    }

    /**
     * A method that returns true when the plugin should generate default configs on reload
     *
     * @return true when the plugin should generate default configs on reload
     */
    public boolean shouldGenerateDefaultConfigs() {
        return this.generateDefaultConfigs;
    }

    /**
     * A method that is used to set whether the plugin should generate default configs on reload
     *
     * @param generateDefaultConfigs whether the plugin should generate default configs on reload
     */
    public void setGenerateDefaultConfigs(boolean generateDefaultConfigs) {
        this.generateDefaultConfigs = generateDefaultConfigs;
    }

    /**
     * A method used to get whether the plugin should be strict with ability execution context
     *
     * @return whether the plugin should be strict with ability execution context
     */
    public boolean isStrict() {
        return this.strict;
    }

    /**
     * A method used to set whether the plugin should be strict with ability execution context
     *
     * @param strict whether the plugin should be strict with ability execution context
     */
    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    /**
     * A method that returns the current debug level
     *
     * @return the current debug level
     */
    public int getDebugLevel() {
        return this.debugLevel;
    }

    /**
     * A method used to set the current debug level
     *
     * @param debugLevel the debug level you wish to set to
     * @param saveConfig if the config file should be updated to reflect this change
     */
    public void setDebugLevel(int debugLevel, boolean saveConfig) {
        if (debugLevel > 5) {
            this.debugLevel = 5;
        } else if (debugLevel < 0) {
            this.debugLevel = 0;
        } else {
            this.debugLevel = debugLevel;
        }

        if (saveConfig) {
            Plugin plugin = InnovativeItems.getInstance();
            FileConfiguration config = plugin.getConfig();

            config.set("debug-level", this.debugLevel);
            plugin.saveConfig();
        }
    }

    /**
     * (VALUE IS LOCAL AND DOES NOT ALWAYS MATCH THE ACTIVE INSTANCE OF THE BOOLEAN)
     * A method that returns a boolean that is true when the garbage collector is set to update item mismatches
     *
     * @return a boolean that is true when the garbage collector is set to update item mismatches
     */
    public boolean shouldUpdateItems() {
        return this.shouldUpdateLocal;
    }

    /**
     * (VALUE IS LOCAL AND DOES NOT ALWAYS MATCH THE ACTIVE INSTANCE OF THE BOOLEAN)
     * A method that will set a boolean that when true the garbage collector will update item mismatches
     *
     * @param shouldUpdate a boolean that when true the garbage collector will update item mismatches
     */
    public void setShouldUpdate(boolean shouldUpdate) {
        this.shouldUpdateLocal = shouldUpdate;
    }

    /**
     * (VALUE IS LOCAL AND DOES NOT ALWAYS MATCH THE ACTIVE INSTANCE OF THE BOOLEAN)
     * A method that returns a boolean that is true when the garbage collector is set to delete items not found in cache
     *
     * @return a boolean that is true when the garbage collector is set to delete items not found in cache
     */
    public boolean shouldDeleteItems() {
        return this.shouldDeleteLocal;
    }

    /**
     * (VALUE IS LOCAL AND DOES NOT ALWAYS MATCH THE ACTIVE INSTANCE OF THE BOOLEAN)
     * A method that will set a boolean that when true the garbage collector will delete items not found in cache
     *
     * @param shouldDelete a boolean that when true the garbage collector will delete items not found in cache
     */
    public void setShouldDelete(boolean shouldDelete) {
        this.shouldDeleteLocal = shouldDelete;
    }

    /**
     * (VALUE IS LOCAL AND DOES NOT ALWAYS MATCH THE ACTIVE INSTANCE OF THE BOOLEAN)
     * A method that returns a boolean that when true the item defender will prevent custom items being used in crafting recipes
     *
     * @return a boolean that when true the item defender will prevent custom items being used in crafting recipes
     */
    public boolean shouldAllowCrafting() {
        return this.allowCraftingLocal;
    }

    /**
     * (VALUE IS LOCAL AND DOES NOT ALWAYS MATCH THE ACTIVE INSTANCE OF THE BOOLEAN)
     * A method that will set a boolean that when true the item defender will prevent custom items being used in crafting recipes
     *
     * @param allowCrafting a boolean that when true the item defender will prevent custom items being used in crafting recipes
     */
    public void setShouldAllowCrafting(boolean allowCrafting) {
        this.allowCraftingLocal = allowCrafting;
    }

    /**
     * (VALUE IS LOCAL AND DOES NOT ALWAYS MATCH THE ACTIVE INSTANCE OF THE BOOLEAN)
     * A method that returns a boolean that when true the item defender will prevent custom items being used in smelting recipes
     *
     * @return a boolean that when true the item defender will prevent custom items being used in smelting recipes
     */
    public boolean shouldAllowSmelting() {
        return this.allowSmeltingLocal;
    }

    /**
     * (VALUE IS LOCAL AND DOES NOT ALWAYS MATCH THE ACTIVE INSTANCE OF THE BOOLEAN)
     * A method that will set a boolean that when true the item defender will prevent custom items being used in smelting recipes
     *
     * @param allowSmelting a boolean that when true the item defender will prevent custom items being used in smelting recipes
     */
    public void setShouldAllowSmelting(boolean allowSmelting) {
        this.allowSmeltingLocal = allowSmelting;
    }

    /**
     * (VALUE IS LOCAL AND DOES NOT ALWAYS MATCH THE ACTIVE INSTANCE OF THE BOOLEAN)
     * A method that returns a boolean that when true the item defender will prevent custom items being used in smelting recipes
     *
     * @return a boolean that when true the item defender will prevent custom items being used in smelting recipes
     */
    public boolean shouldAllowBrewing() {
        return this.allowBrewingLocal;
    }

    /**
     * (VALUE IS LOCAL AND DOES NOT ALWAYS MATCH THE ACTIVE INSTANCE OF THE BOOLEAN)
     * A method that will set a boolean that when true the item defender will prevent custom items being used in brewing recipes
     *
     * @param allowBrewing a boolean that when true the item defender will prevent custom items being used in brewing recipes
     */
    public void setShouldAllowBrewing(boolean allowBrewing) {
        this.allowBrewingLocal = allowBrewing;
    }

    /**
     * A method used to clear the cache and reload all elements
     */
    public void reload() {
        LogUtil.logUnblocked(LogUtil.Level.INFO, "Starting plugin reload in five seconds, some bugs may occur during this time...");
        Bukkit.getScheduler().runTaskLaterAsynchronously(InnovativeItems.getInstance(), () -> {
            InnovativeItems plugin = InnovativeItems.getInstance();

            LogUtil.log(LogUtil.Level.INFO, "Temporarily disabling garbage collector...");

            GarbageCollector garbageCollector = plugin.getGarbageCollector();
            garbageCollector.setEnabled(false);

            LogUtil.log(LogUtil.Level.INFO, "Starting basic config reload...");

            plugin.reloadConfig();

            this.reloadMainConfigValues();

            LogUtil.log(LogUtil.Level.INFO, "Basic config reload complete!");

            LogUtil.log(LogUtil.Level.INFO, "Starting cache invalidation...");

            plugin.getItemCache().clearCache();
            plugin.getAbilityTimerManager().clearCache();

            LogUtil.log(LogUtil.Level.INFO, "Cache invalidation complete!");

            this.init();

            LogUtil.log(LogUtil.Level.INFO, "Setting garbage collector settings to match config...");

            garbageCollector.setShouldUpdate(this.shouldUpdateLocal);
            garbageCollector.setShouldDelete(this.shouldDeleteLocal);

            LogUtil.log(LogUtil.Level.INFO, "Garbage collector settings now match config!");

            LogUtil.log(LogUtil.Level.INFO, "Re-enabling garbage collector!");

            garbageCollector.setEnabled(true);

            garbageCollector.cleanAllPlayerInventories(false);

            LogUtil.logUnblocked(LogUtil.Level.INFO, "Plugin reload complete!");
        }, 100L);
    }

    /**
     * A startup method to start parsing all config files
     */
    public void init() {
        LogUtil.logUnblocked(LogUtil.Level.INFO, "Starting basic plugin initialization...");

        InnovativeItems plugin = InnovativeItems.getInstance();

        LogUtil.log(LogUtil.Level.INFO, "Starting directory initialization...");

        File home = plugin.getDataFolder();
        File items = new File(home, "items");
        File abilities = new File(home, "abilities");

        if (!home.exists()) {
            home.mkdir();
        }

        if (!items.exists()) {
            items.mkdir();
        }

        if (!abilities.exists()) {
            abilities.mkdir();
        }

        LogUtil.log(LogUtil.Level.INFO, "Directory initialization complete!");

        if (this.generateDefaultConfigs) {
            this.generateDefaultConfigs(plugin, abilities, items);
        }

        InnovativeCache cache = plugin.getItemCache();

        this.loadAbilities(abilities, cache);

        this.loadItems(items, cache);

        LogUtil.logUnblocked(LogUtil.Level.INFO, "Basic plugin initialization complete!");
    }

    /**
     * A method used to generate the default configuration files
     */
    private void generateDefaultConfigs(InnovativeItems plugin, File abilities, File items) {
        LogUtil.log(LogUtil.Level.INFO, "Starting default configuration generation...");
        File defaultAbilities = new File(abilities, "default-abilities.yml");
        File defaultItems = new File(items, "default-items.yml");

        try {
            if (!defaultAbilities.exists()) {
                defaultAbilities.createNewFile();
                Files.copy(plugin.getResource("default-abilities.yml"), defaultAbilities.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            if (!defaultItems.exists()) {
                defaultItems.createNewFile();
                Files.copy(plugin.getResource("default-items.yml"), defaultItems.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            LogUtil.log(LogUtil.Level.SEVERE, "There was an error trying to write a new file to disk...");
            if (this.debugLevel >= LogUtil.Level.DEV.getDebugLevel()) {
                e.printStackTrace();
            }
            LogUtil.log(LogUtil.Level.INFO, "Configuration generation failed...");
            return;
        }

        LogUtil.log(LogUtil.Level.INFO, "Configuration generation complete!");
    }

    /**
     * A method used to parse and cache abilities from yml files
     *
     * @param home  the home directory of all ability yml files
     * @param cache the cache where loaded abilities will be registered to
     */
    private void loadAbilities(File home, InnovativeCache cache) {
        LogUtil.log(LogUtil.Level.INFO, "Starting ability initialization and parsing...");

        for (File file : home.listFiles()) {
            YamlConfiguration configuration = new YamlConfiguration();

            try {
                configuration.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                LogUtil.log(LogUtil.Level.SEVERE, "A " + e.getClass().getSimpleName() + " occurred while loading " + file.getName() + " during ability initialization and parsing stage!");
                if (this.getDebugLevel() >= LogUtil.Level.DEV.getDebugLevel()) {
                    e.printStackTrace();
                }
                continue;
            }


            int maxCount = (this.generateDefaultConfigs) ? 6 : 3;
            for (String key : configuration.getKeys(false)) {
                if (!InnovativeItems.isPluginPremium() && cache.getAbilitiesAmount() >= maxCount) {
                    LogUtil.logUnblocked(LogUtil.Level.WARNING, "You have reached the maximum amount of abilities for the free version of the plugin! Skipping the ability identified as: " + key);
                    continue;
                }

                ConfigurationSection section = configuration.getConfigurationSection(key);

                AbilityParser.buildAbility(section, cache);
            }
        }

        LogUtil.log(LogUtil.Level.INFO, "Ability initialization and parsing complete!");
    }

    /**
     * A method used to parse and cache items from yml files
     *
     * @param home  the home directory of all item yml files
     * @param cache the cache where loaded items will be registered to
     */
    private void loadItems(File home, InnovativeCache cache) {
        LogUtil.log(LogUtil.Level.INFO, "Starting item initialization and parsing...");

        for (File file : home.listFiles()) {
            YamlConfiguration configuration = new YamlConfiguration();

            try {
                configuration.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                LogUtil.log(LogUtil.Level.WARNING, "A " + e.getClass().getSimpleName() + " occurred while loading " + file.getName() + " during item initialization and parsing stage!");
                if (this.getDebugLevel() >= LogUtil.Level.DEV.getDebugLevel()) {
                    e.printStackTrace();
                }
                continue;
            }

            int maxCount = (this.generateDefaultConfigs) ? 16 : 10;
            for (String key : configuration.getKeys(false)) {
                ConfigurationSection section = configuration.getConfigurationSection(key);

                String name = section.getName();

                if (cache.contains(name)) {
                    LogUtil.log(LogUtil.Level.WARNING, "Element with the name of " + name + ", is already registered! Skipping item...");
                    continue;
                }

                if (!InnovativeItems.isPluginPremium() && cache.getItemAmount() >= maxCount) {
                    LogUtil.logUnblocked(LogUtil.Level.WARNING, "You have reached the maximum amount of custom items for the free version of the plugin! Skipping the item identified as: " + key);
                    continue;
                }

                CustomItem item = ItemParser.parseItem(section, name);

                if (item == null) {
                    //error message was already sent from parseItem method, no need to put in here
                    continue;
                }

                cache.registerItem(item);
            }
        }

        LogUtil.log(LogUtil.Level.INFO, "Item initialization and parsing complete!");
    }
}