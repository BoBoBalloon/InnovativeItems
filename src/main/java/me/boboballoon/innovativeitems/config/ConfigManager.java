package me.boboballoon.innovativeitems.config;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.GarbageCollector;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.items.ItemDefender;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.items.item.RecipeType;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * A class used to cache and parse config files
 */
public final class ConfigManager {
    private static final ImmutableList<String> DEFAULT_ABILITIES = ImmutableList.of("fairy-fullset", "fairy-fullset-buff", "fairy-fullset-safety", "fairy-attack", "fairy-healing");
    private static final int MAX_FREE_ABILITIES = 15;

    //update checker
    private boolean checkForUpdates;

    //default configs
    private boolean generateDefaultConfigs;

    //strict mode
    private boolean strict;

    //placeable item error message
    private String failedItemPlaceMessage;

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

    //item defender
    private boolean itemDefenderEnabledLocal;

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
        if (config.isBoolean("check-for-updates")) {
            checkForUpdates = config.getBoolean("check-for-updates");
        } else {
            checkForUpdates = true;
            config.set("check-for-updates", true);
        }
        this.setCheckForUpdates(checkForUpdates);

        //load up default config values, set to true if none is present
        boolean generateDefaultConfigs;
        if (config.isBoolean("generate-default-configs")) {
            generateDefaultConfigs = config.getBoolean("generate-default-configs");
        } else {
            generateDefaultConfigs = true;
            config.set("generate-default-configs", true);
        }
        this.setGenerateDefaultConfigs(generateDefaultConfigs);

        //strict mode
        boolean strict;
        if (config.isBoolean("strict")) {
            strict = config.getBoolean("strict");
        } else {
            strict = true;
            config.set("strict", true);
        }
        this.setStrict(strict);

        String failedItemPlaceMessage;
        if (config.isString("failed-item-place")) {
            failedItemPlaceMessage = config.getString("failed-item-place", "null");
        } else {
            failedItemPlaceMessage = "null";
            config.set("failed-item-place", "null");
        }
        this.setFailedItemPlaceMessage(failedItemPlaceMessage);

        //load up debug level, sets to 2 if no value is present
        int debugLevel;
        if (config.isInt("debug-level")) {
            debugLevel = config.getInt("debug-level");
        } else {
            debugLevel = 2;
            config.set("debug-level", 2);
        }
        this.setDebugLevel(debugLevel, false);

        //load up garbage collector should update option, sets to true if no value is present
        boolean shouldUpdate;
        if (config.isBoolean("garbage-collector.should-update")) {
            shouldUpdate = config.getBoolean("garbage-collector.should-update");
        } else {
            shouldUpdate = true;
            config.set("garbage-collector.should-update", true);
        }
        this.setShouldUpdate(shouldUpdate);

        //load up garbage collector should delete option, sets to true if no value is present
        boolean shouldDelete;
        if (config.isBoolean("garbage-collector.should-delete")) {
            shouldDelete = config.getBoolean("garbage-collector.should-delete");
        } else {
            shouldDelete = true;
            config.set("garbage-collector.should-delete", true);
        }
        this.setShouldDelete(shouldDelete);

        //if the item defender should be active
        boolean enabled;
        if (config.isBoolean("item-defender.enabled")) {
            enabled = config.getBoolean("item-defender.enabled");
        } else {
            enabled = true;
            config.set("item-defender.enabled", true);
        }
        this.setIsItemDefenderEnabled(enabled);

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
     * Gets the message to be sent to players when they try and place down a custom item that is not placeable
     *
     * @return the message to be sent to players when they try and place down a custom item that is not placeable
     */
    @NotNull
    public String getFailedItemPlaceMessage() {
        return this.failedItemPlaceMessage;
    }

    /**
     * Sets the message to be sent to players when they try and place down a custom item that is not placeable
     *
     * @param failedItemPlaceMessage the message to be sent to players when they try and place down a custom item that is not placeable
     */
    public void setFailedItemPlaceMessage(@NotNull String failedItemPlaceMessage) {
        this.failedItemPlaceMessage = TextUtil.format(failedItemPlaceMessage);
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
        this.debugLevel = Math.max(Math.min(debugLevel, 5), 0); //pick the lowest number, the max or debugLevel (if higher picks 5, if lower picks debugLevel) pick the biggest number next (self explanatory)

        if (!saveConfig) {
            return;
        }

        Plugin plugin = InnovativeItems.getInstance();
        FileConfiguration config = plugin.getConfig();

        config.set("debug-level", this.debugLevel);
        plugin.saveConfig();
    }

    /**
     * (VALUE IS LOCAL AND DOES NOT ALWAYS MATCH THE ACTIVE INSTANCE OF THE BOOLEAN)
     * A method used to get if the item defender should be active
     *
     * @return if the item defender should be active
     */
    public boolean isItemDefenderEnabled() {
        return this.itemDefenderEnabledLocal;
    }

    /**
     * (VALUE IS LOCAL AND DOES NOT ALWAYS MATCH THE ACTIVE INSTANCE OF THE BOOLEAN)
     * A method used to set if the item defender should be active
     *
     * @param itemDefenderEnabled a boolean that is true if the item defender should be active
     */
    public void setIsItemDefenderEnabled(boolean itemDefenderEnabled) {
        this.itemDefenderEnabledLocal = itemDefenderEnabled;
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

            InnovativeCache cache = plugin.getItemCache();

            for (String id : cache.getItemIdentifiers()) {
                ImmutableList<Recipe> recipes = cache.getItem(id).getRecipes();

                if (recipes == null) {
                    continue;
                }

                Bukkit.getScheduler().runTask(InnovativeItems.getInstance(), () -> {
                    for (Recipe recipe : recipes) {
                        if (!(recipe instanceof Keyed)) {
                            LogUtil.log(LogUtil.Level.DEV, "An internal error has occurred, the recipe registered on the " + id + " item does not implement the keyed interface!");
                            return;
                        }

                        Keyed keyed = (Keyed) recipe;

                        if (!Bukkit.removeRecipe(keyed.getKey())) {
                            LogUtil.log(LogUtil.Level.WARNING, "An error occurred while trying to unregister the custom crafting recipe for the " + id + " custom item!");
                        }
                    }
                });
            }

            cache.clearCache();
            plugin.getAbilityTimerManager().clearCache();

            LogUtil.log(LogUtil.Level.INFO, "Cache invalidation complete!");

            this.init();

            LogUtil.log(LogUtil.Level.INFO, "Setting garbage collector settings to match config...");

            garbageCollector.setShouldUpdate(this.shouldUpdateLocal);
            garbageCollector.setShouldDelete(this.shouldDeleteLocal);

            LogUtil.log(LogUtil.Level.INFO, "Garbage collector settings now match config!");

            LogUtil.log(LogUtil.Level.INFO, "Re-enabling garbage collector!");

            garbageCollector.setEnabled(true);

            Bukkit.getScheduler().runTask(InnovativeItems.getInstance(), () -> garbageCollector.cleanAllPlayerInventories(true));

            LogUtil.log(LogUtil.Level.INFO, "Updating item defender to match config...");

            ItemDefender itemDefender = plugin.getItemDefender();
            itemDefender.setEnabled(this.itemDefenderEnabledLocal);

            LogUtil.log(LogUtil.Level.INFO, "Item defender settings now match config!");

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
    private void generateDefaultConfigs(@NotNull InnovativeItems plugin, @NotNull File abilities, @NotNull File items) {
        LogUtil.log(LogUtil.Level.INFO, "Starting default configuration generation...");
        File defaultAbilities = new File(abilities, "default-abilities.yml");
        File defaultItems = new File(items, "default-items.yml");

        try {
            if (!defaultAbilities.exists()) {
                defaultAbilities.createNewFile();
                Files.copy(plugin.getResource("default-abilities.yml"), defaultAbilities.toPath(), StandardCopyOption.REPLACE_EXISTING);
                LogUtil.log(LogUtil.Level.INFO, "Created a new default abilities file!");
            }

            if (!defaultItems.exists()) {
                defaultItems.createNewFile();
                Files.copy(plugin.getResource("default-items.yml"), defaultItems.toPath(), StandardCopyOption.REPLACE_EXISTING);
                LogUtil.log(LogUtil.Level.INFO, "Created a new default items file!");
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
    private void loadAbilities(@NotNull File home, @NotNull InnovativeCache cache) {
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

            int registered = 0;

            for (String key : configuration.getKeys(false)) {
                if (!InnovativeItems.isPluginPremium() && registered >= ConfigManager.MAX_FREE_ABILITIES) {
                    LogUtil.logUnblocked(LogUtil.Level.WARNING, "You have reached the maximum amount of abilities for the free version of the plugin! Skipping the ability identified as: " + key);
                    continue;
                }

                ConfigurationSection section = configuration.getConfigurationSection(key);

                if (section == null) {
                    continue;
                }

                boolean register = AbilityParser.buildAbility(section, cache); //register ability, return false if something went wrong

                if (register && (!this.generateDefaultConfigs || !ConfigManager.DEFAULT_ABILITIES.contains(key))) { //if it was registered and not a default ability, count it
                    registered++;
                }
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
    private void loadItems(@NotNull File home, @NotNull InnovativeCache cache) {
        LogUtil.log(LogUtil.Level.INFO, "Starting item initialization and parsing...");

        LinkedList<ItemNode> nodes = new LinkedList<>();

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

            for (String key : configuration.getKeys(false)) {
                ConfigurationSection section = configuration.getConfigurationSection(key);

                if (section == null) {
                    continue;
                }

                String name = section.getName();

                if (cache.contains(name)) {
                    LogUtil.log(LogUtil.Level.WARNING, "Element with the name of " + name + ", is already registered! Skipping item...");
                    continue;
                }

                nodes.add(new ItemNode(section));
            }
        }

        for (ItemNode node : nodes) {
            node.findDependantItems(cache, nodes);
        }

        while (!nodes.isEmpty()) {
            Optional<ItemNode> current = nodes.stream().filter(node -> node.getDependantItems().size() <= 0).findAny();

            if (!current.isPresent()) {
                LogUtil.logUnblocked(LogUtil.Level.SEVERE, "A cycle has been found in the heap of custom items! You are not allowed to have two items that depend on each other or an item that depends on itself as that would cause an infinite loop!");
                break;
            }

            ItemNode node = current.get();

            for (ItemNode parent : nodes) {
                parent.getDependantItems().remove(node.getIdentifier());
            }

            nodes.remove(node);

            LogUtil.log(LogUtil.Level.NOISE, "Parsing item: " + node.getIdentifier());

            CustomItem item = ItemParser.parseItem(node.getConfigurationSection(), node.getIdentifier());

            if (item != null) {
                cache.registerItem(item);
                LogUtil.log(LogUtil.Level.NOISE, "Registered item: " + node.getIdentifier());
            } else {
                LogUtil.log(LogUtil.Level.NOISE, "Failed to register item: " + node.getIdentifier());
            }
        }

        if (nodes.isEmpty()) { //always true unless cycle was found
            LogUtil.log(LogUtil.Level.INFO, "Item initialization and parsing complete!");
            return;
        }

        LogUtil.logUnblocked(LogUtil.Level.SEVERE, "As a cycle was detected all items will be loaded without custom crafting recipes!");

        for (ItemNode node : nodes) {
            CustomItem item = ItemParser.parseItem(node.getConfigurationSection(), node.getIdentifier(), false);

            if (item != null) {
                cache.registerItem(item);
                LogUtil.log(LogUtil.Level.NOISE, "Registered item: " + node.getIdentifier());
            } else {
                LogUtil.log(LogUtil.Level.NOISE, "Failed to register item: " + node.getIdentifier());
            }
        }

        LogUtil.log(LogUtil.Level.INFO, "Item initialization and parsing complete!");
    }



    /**
     * A class used to read but not parse items that contain recipes to get a snapshot of what they contain
     */
    private static final class ItemNode {
        private final ConfigurationSection section;
        private final String identifier;
        private Set<String> dependantItems;

        public ItemNode(@NotNull ConfigurationSection section) {
            this.section = section;
            this.identifier = section.getName();
            this.dependantItems = null;
        }

        /**
         * Method used to get the configuration section that this reader is reading
         *
         * @return the configuration section that this reader is reading
         */
        @NotNull
        public ConfigurationSection getConfigurationSection() {
            return this.section;
        }

        /**
         * Method used to get the identifier of the item that this reader is reading from
         *
         * @return the identifier of the item that this reader is reading from
         */
        @NotNull
        public String getIdentifier() {
            return this.identifier;
        }

        /**
         * Method used to get all the items that are dependant on this item
         *
         * @return all the items that are dependant on this item
         */
        @Nullable
        public Set<String> getDependantItems() {
            return this.dependantItems;
        }

        /**
         * A method used to calculate and set the getDependantItems method to @NotNull
         */
        public void findDependantItems(@NotNull InnovativeCache cache, @NotNull LinkedList<ItemNode> nodes) {
            Set<String> dependantItems = new HashSet<>();

            if (!this.section.isConfigurationSection("recipes")) {
                this.dependantItems = dependantItems;
                return;
            }

            ConfigurationSection recipes = this.section.getConfigurationSection("recipes");

            for (String recipeName : recipes.getKeys(false)) {
                if (!recipes.isConfigurationSection(recipeName)) {
                    continue;
                }

                ConfigurationSection recipe = recipes.getConfigurationSection(recipeName);

                if (!recipe.isString("type")) {
                    continue;
                }

                RecipeType type;
                try {
                    type = RecipeType.valueOf(recipe.getString("type").toUpperCase());
                } catch (IllegalArgumentException e) {
                    continue;
                }

                boolean isCookingRecipe = type == RecipeType.FURNACE || type == RecipeType.BLAST_FURNACE || type == RecipeType.SMOKER || type == RecipeType.CAMPFIRE;

                if ((type == RecipeType.SHAPED && (!recipe.isList("keys") || !recipe.isList("shape"))) ||
                        (isCookingRecipe && !recipe.isString("key"))) {
                    continue;
                }

                List<String> keys = type == RecipeType.SHAPED ? recipe.getStringList("keys") : isCookingRecipe ? Collections.singletonList(recipe.getString("key")) : null;

                if (keys == null) {
                    LogUtil.log(LogUtil.Level.DEV, "Error on item " + this.identifier + " on recipe " + recipeName + ": An unknown RecipeType was found with no implementation on creating a snapshot with the purpose of creating a list of dependant items!");
                    continue;
                }

                for (String key : keys) {
                    if (key.startsWith("~")) {
                        continue;
                    }

                    String regex = type == RecipeType.SHAPED ? "\\w:.+" : null;

                    if (regex != null && !key.matches(regex)) {
                        continue;
                    }

                    String rawId = type == RecipeType.SHAPED ? key.split(":")[1] : key;

                    if (cache.getItem(rawId) != null || nodes.stream().anyMatch(node -> node.getIdentifier().equals(rawId))) {
                        dependantItems.add(rawId); //items are prioritized in parsing when an assert is not present so this a-ok
                    }
                }
            }

            this.dependantItems = dependantItems;

            /*
            if (this.dependantItems.size() >= 1) {
                LogUtil.logUnblocked(LogUtil.Level.DEV, "---------------------------------------------------");
                LogUtil.logUnblocked(LogUtil.Level.DEV, "dependant items for " + this.identifier + ":");
                for (String item : this.dependantItems) {
                    LogUtil.logUnblocked(LogUtil.Level.DEV, item);
                }
                LogUtil.logUnblocked(LogUtil.Level.DEV, "---------------------------------------------------");
            }
             */
        }
    }
}