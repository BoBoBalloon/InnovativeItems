package me.boboballoon.innovativeitems.config;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.GarbageCollector;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * A class used to cache and parse config files
 */
public final class ConfigManager {
    //debug level
    private int debugLevel;

    //garbage collector (copy of values)
    private boolean shouldUpdateLocal;
    private boolean shouldDeleteLocal;

    public ConfigManager() {
        Plugin plugin = InnovativeItems.getInstance();
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        this.setDebugLevel(config.getInt("debug-level"));
        this.setShouldUpdate(config.getBoolean("garbage-collector.should-update"));
        this.setShouldDelete(config.getBoolean("garbage-collector.should-delete"));
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
     */
    public void setDebugLevel(int debugLevel) {
        Plugin plugin = InnovativeItems.getInstance();
        FileConfiguration config = plugin.getConfig();

        if (debugLevel > 3) {
            this.debugLevel = 3;
        } else if (debugLevel < 1) {
            this.debugLevel = 1;
        } else {
            this.debugLevel = debugLevel;
        }

        config.set("debug-level", this.debugLevel);
        plugin.saveConfig();
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
     * A method used to clear the cache and reload all elements
     */
    public void reload() {
        LogUtil.logUnblocked(Level.INFO, "Starting plugin reload in five seconds, some bugs may occur during this time...");
        Bukkit.getScheduler().runTaskLaterAsynchronously(InnovativeItems.getInstance(), () -> {
            InnovativeItems plugin = InnovativeItems.getInstance();

            LogUtil.log(Level.INFO, "Temporarily disabling garbage collector...");

            GarbageCollector garbageCollector = plugin.getGarbageCollector();
            garbageCollector.setEnabled(false);

            LogUtil.log(Level.INFO, "Starting basic config reload...");

            plugin.reloadConfig();

            FileConfiguration config = plugin.getConfig();

            //debug level
            this.setDebugLevel(config.getInt("debug-level"));

            //garbage collector
            this.setShouldUpdate(config.getBoolean("garbage-collector.should-update"));
            this.setShouldDelete(config.getBoolean("garbage-collector.should-delete"));

            LogUtil.log(Level.INFO, "Basic config reload complete!");

            LogUtil.log(Level.INFO, "Starting cache invalidation...");

            InnovativeCache cache = plugin.getCache();
            cache.clearCache();

            LogUtil.log(Level.INFO, "Cache invalidation complete!");

            this.init();

            LogUtil.log(Level.INFO, "Setting garbage collector settings to match config...");

            garbageCollector.setShouldUpdate(this.shouldUpdateLocal);
            garbageCollector.setShouldDelete(this.shouldDeleteLocal);

            LogUtil.log(Level.INFO, "Garbage collector settings now match config!");

            LogUtil.log(Level.INFO, "Re-enabling garbage collector!");

            garbageCollector.setEnabled(true);

            garbageCollector.cleanAllPlayerInventories();

            LogUtil.logUnblocked(Level.INFO, "Plugin reload complete!");
        }, 100L);
    }

    /**
     * A startup method to start parsing all config files
     */
    public void init() {
        LogUtil.logUnblocked(Level.INFO, "Starting basic plugin initialization...");

        InnovativeItems plugin = InnovativeItems.getInstance();

        LogUtil.log(Level.INFO, "Starting directory initialization...");

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

        LogUtil.log(Level.INFO, "Directory initialization complete!");

        InnovativeCache cache = plugin.getCache();

        //ConfigManager.loadAbilities(abilities); (add in later)

        this.loadItems(items, cache);

        LogUtil.logUnblocked(Level.INFO, "Basic plugin initialization complete!");
    }

    /**
     * A method used to parse and cache abilities from yml files
     *
     * @param home the home directory of all ability yml files
     * @param cache the cache where loaded items will be registered to
     */
    private void loadAbilities(File home, InnovativeCache cache) {
        LogUtil.log(Level.INFO, "Starting item ability initialization and parsing...");

        YamlConfiguration configuration = new YamlConfiguration();

        for (File file : home.listFiles()) {
            try {
                configuration.load(file);
            } catch (IOException e) {
                LogUtil.log(Level.WARNING, "An IO exception occurred while loading " + file.getName() + " during item ability initialization and parsing stage!");
                e.printStackTrace();
                continue;
            } catch (InvalidConfigurationException ignore) {
                continue;
            }

            for (String key : configuration.getKeys(false)) {
                ConfigurationSection section = configuration.getConfigurationSection(key);
                //parse keywords
                //build interface
                //cache interface
            }
        }

        LogUtil.log(Level.INFO, "Item ability initialization and parsing complete!");
    }

    /**
     * A method used to parse and cache items from yml files
     *
     * @param home the home directory of all item yml files
     * @param cache the cache where loaded items will be registered to
     */
    private void loadItems(File home, InnovativeCache cache) {
        LogUtil.log(Level.INFO, "Starting item initialization and parsing...");

        for (File file : home.listFiles()) {
            YamlConfiguration configuration = new YamlConfiguration();

            try {
                configuration.load(file);
            } catch (IOException e) {
                LogUtil.log(Level.SEVERE, "An IO exception occurred while loading " + file.getName() + " during item initialization and parsing stage!");
                e.printStackTrace();
                continue;
            } catch (InvalidConfigurationException ignore) {
                continue;
            }

            for (String key : configuration.getKeys(false)) {
                ConfigurationSection section = configuration.getConfigurationSection(key);

                String name = section.getName();

                if (cache.contains(name)) {
                    LogUtil.log(Level.WARNING, "Element with the name of " + name + ", is already registered! Skipping item...");
                    continue;
                }

                cache.registerItem(ItemParser.parseItem(section));
            }
        }

        LogUtil.log(Level.INFO, "Item initialization and parsing complete!");
    }
}