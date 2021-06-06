package me.boboballoon.innovativeitems.config;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.Ability;
import me.boboballoon.innovativeitems.items.Item;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * A class used to cache and parse config files
 */
public class ConfigManager {
    public static final HashMap<String, Ability> ABILITIES = new HashMap();
    public static final HashMap<String, Item> ITEMS = new HashMap();

    /**
     * A method used to clear the cache and reload all elements
     */
    public static void reload() {
        LogUtil.log(Level.WARNING, "Starting plugin restart in five seconds, some bugs may occur during this time...");
        Bukkit.getScheduler().runTaskLaterAsynchronously(InnovativeItems.getInstance(), () -> {
            LogUtil.log(Level.INFO, "Starting cache invalidation...");
            ConfigManager.ITEMS.clear();
            ConfigManager.ABILITIES.clear();
            LogUtil.log(Level.INFO, "Cache invalidation complete!");
            ConfigManager.init();
            LogUtil.log(Level.INFO, "Plugin reload complete!");
        }, 100L);
    }

    /**
     * A startup method to start parsing all config files
     */
    public static void init() {
        LogUtil.log(Level.INFO, "Starting basic plugin initialization...");
        LogUtil.log(Level.INFO, "Starting directory initialization...");
        File home = InnovativeItems.getInstance().getDataFolder();
        File items = new File(home, "items");
        File abilities = new File(home, "abilities");

        if (!items.exists()) {
            items.mkdir();
        }

        if (!abilities.exists()) {
            abilities.mkdir();
        }

        LogUtil.log(Level.INFO, "Directory initialization complete!");

        ConfigManager.loadAbilities(abilities);

        ConfigManager.loadItems(items);

        LogUtil.log(Level.INFO, "Basic plugin initialization complete!");
    }

    /**
     * A method used to parse and cache abilities from yml files
     *
     * @param home the home directory of all ability yml files
     */
    private static void loadAbilities(File home) {
        LogUtil.log(Level.INFO, "Starting item ability initialization and parsing...");

        YamlConfiguration configuration = new YamlConfiguration();

        for (File file : home.listFiles()) {
            try {
                configuration.load(file);
            } catch (IOException e) {
                LogUtil.log(Level.SEVERE, "An IO exception occurred while loading " + file.getName() + " during item ability initialization and parsing stage!");
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
     */
    private static void loadItems(File home) {
        LogUtil.log(Level.INFO, "Starting item initialization and parsing...");

        YamlConfiguration configuration = new YamlConfiguration();

        for (File file : home.listFiles()) {
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
                //parse object
                //build object
                //cache object
            }
        }

        LogUtil.log(Level.INFO, "Item initialization and parsing complete!");
    }
}
