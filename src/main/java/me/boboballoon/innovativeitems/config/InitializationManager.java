package me.boboballoon.innovativeitems.config;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * A class responsible with the initialization of all elements in configuration files for the plugin
 */
public final class InitializationManager {
    private final File itemDirectory;
    private final File abilityDirectory;
    private InitializationStage stage;

    private static final ConfigManager CONFIG_MANAGER = InnovativeItems.getInstance().getConfigManager();
    private static final InnovativeCache CACHE = InnovativeItems.getInstance().getItemCache();

    public InitializationManager(File itemDirectory, File abilityDirectory) {
        if (!itemDirectory.isDirectory() || !abilityDirectory.isDirectory()) {
            throw new IllegalArgumentException("one or more of the provided directories are not a directory on disk!");
        }

        this.itemDirectory = itemDirectory;
        this.abilityDirectory = abilityDirectory;
        this.stage = null;
    }

    /**
     * A method that returns the item directory
     *
     * @return the item directory
     */
    public File getItemDirectory() {
        return this.itemDirectory;
    }

    /**
     * A method that returns the ability directory
     *
     * @return the ability directory
     */
    public File getAbilityDirectory() {
        return this.abilityDirectory;
    }

    /**
     * A method that returns what initialization stage the initialization manager is on
     * 
     * @return what initialization stage the initialization manager is on
     */
    public InitializationStage getStage() {
        return stage;
    }

    /**
     * A method that starts the initialization process
     */
    public void start() {
        LogUtil.log(LogUtil.Level.INFO, "Starting initialization stage one (building incomplete items)...");

        this.stage = InitializationStage.INCOMPLETE_ITEMS;

        ItemBuilder itemBuilder = new ItemBuilder(this, CONFIG_MANAGER, CACHE);

        itemBuilder.buildIncompleteItems();

        LogUtil.log(LogUtil.Level.INFO, "Initialization stage one (building incomplete items) complete!");

        //---------------------------------------------------------------------------------------------------------

        LogUtil.log(LogUtil.Level.INFO, "Starting initialization stage two (building incomplete abilities)...");

        this.stage = InitializationStage.INCOMPLETE_ABILITIES;

        //init new ability builder and make incomplete abilities

        LogUtil.log(LogUtil.Level.INFO, "Initialization stage two (building incomplete abilities) complete!");
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
                if (CONFIG_MANAGER.getDebugLevel() >= LogUtil.Level.DEV.getDebugLevel()) {
                    e.printStackTrace();
                }
                continue;
            }


            int maxCount = CONFIG_MANAGER.shouldGenerateDefaultConfigs() ? 6 : 3;
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



        LogUtil.log(LogUtil.Level.INFO, "Item initialization and parsing complete!");
    }
}
