package me.boboballoon.innovativeitems.config;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.GarbageCollector;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.items.item.Ability;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
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

                String name = section.getName();

                if (cache.contains(name)) {
                    LogUtil.log(Level.WARNING, "Element with the name of " + name + ", is already registered! Skipping item...");
                    continue;
                }

                if (!section.contains("material")) {
                    LogUtil.log(Level.WARNING, "Could not find material field while parsing the item by the name of " + name + "!");
                    continue;
                }

                Material material;
                try {
                    material = Material.valueOf(section.getString("material").toUpperCase());
                } catch (IllegalArgumentException e) {
                    LogUtil.log(Level.WARNING, "Unknown material provided while parsing the item by the name of " + name + " during item initialization and parsing stage!");
                    continue;
                }


                Ability ability;
                if (section.contains("ability")) {
                    ability = this.getAbility(section, name, cache);
                } else {
                    ability = null;
                }

                String displayName;
                if (section.contains("display-name")) {
                    displayName = TextUtil.format(section.getString("display-name"));
                } else {
                    displayName = null;
                }

                List<String> lore;
                if (section.contains("lore")) {
                    lore = this.getLore(section);
                } else {
                    lore = null;
                }

                Map<Enchantment, Integer> enchantments;
                if (section.contains("enchantments")) {
                    enchantments = this.getEnchantments(section, name);
                } else {
                    enchantments = null;
                }

                List<ItemFlag> flags;
                if (section.contains("flags")) {
                    flags = this.getItemFlags(section, name);
                } else {
                    flags = null;
                }

                Multimap<Attribute, AttributeModifier> attributes;
                if (section.contains("attributes")) {
                    attributes = this.getAttributes(section, name);
                } else {
                    attributes = null;
                }

                Integer customModelData;
                if (section.contains("custom-model-data")) {
                    customModelData = section.getInt("custom-model-data");
                } else {
                    customModelData = null;
                }

                boolean unbreakable;
                if (section.contains("unbreakable")) {
                    unbreakable = section.getBoolean("unbreakable");
                } else {
                    unbreakable = false;
                }

                cache.registerItem(name, new CustomItem(name, ability, material, displayName, lore, enchantments, flags, attributes, customModelData, unbreakable));
            }
        }

        LogUtil.log(Level.INFO, "Item initialization and parsing complete!");
    }

    /**
     * Get the ability field from an item config section
     */
    private Ability getAbility(ConfigurationSection section, String itemName, InnovativeCache cache) {
        String rawAbility = section.getString("ability");
        Ability ability = cache.getAbility(rawAbility);

        if (ability == null) {
            LogUtil.log(Level.WARNING, "Could not find ability with the name " + rawAbility + " while parsing the item by the name of " + itemName + " during item initialization and parsing stage!");
        }

        return ability;
    }

    /**
     * Get the lore field from an item config section
     */
    private List<String> getLore(ConfigurationSection section) {
        List<String> lore = section.getStringList("lore");

        for (int i = 0; i < lore.size(); i++) {
            String element = TextUtil.format(lore.get(i));
            lore.set(i, element);
        }

        return lore;
    }

    /**
     * Get the enchantment field from an item config section
     */
    private Map<Enchantment, Integer> getEnchantments(ConfigurationSection section, String itemName) {
        ConfigurationSection enchantmentSection = section.getConfigurationSection("enchantments");
        Map<Enchantment, Integer> enchantments = new HashMap<>();

        for (String enchantmentName : enchantmentSection.getKeys(false)) {
            int level = enchantmentSection.getInt(enchantmentName);
            Enchantment enchantment = Enchantment.getByName(enchantmentName);

            if (enchantment == null) {
                LogUtil.log(Level.WARNING, "Could not find enchantment with the name " + enchantmentName + " while parsing the item by the name of " + itemName + " during item initialization and parsing stage!");
                continue;
            }

            enchantments.put(enchantment, level);
        }

        return enchantments;
    }

    /**
     * Get the item flags field from an item config section
     */
    private List<ItemFlag> getItemFlags(ConfigurationSection section, String itemName) {
        List<ItemFlag> flags = new ArrayList<>();
        for (String flag : section.getStringList("flags")) {
            ItemFlag itemFlag;
            try {
                itemFlag = ItemFlag.valueOf(flag.toUpperCase());
            } catch (IllegalArgumentException e) {
                LogUtil.log(Level.WARNING, "Unknown itemflag provided while parsing the item by the name of " + itemName + " during item initialization and parsing stage!");
                continue;
            }
            flags.add(itemFlag);
        }

        return flags;
    }

    /**
     * Get the attributes field from an item config section
     */
    private Multimap<Attribute, AttributeModifier> getAttributes(ConfigurationSection section, String itemName) {
        Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();
        ConfigurationSection attributeSection = section.getConfigurationSection("attributes");

        for (String slotName : attributeSection.getKeys(false)) {
            EquipmentSlot slot;
            try {
                if (slotName.equalsIgnoreCase("ALL")) {
                    slot = null;
                } else {
                    slot = EquipmentSlot.valueOf(slotName.toUpperCase());
                }
            } catch (IllegalArgumentException e) {
                LogUtil.log(Level.WARNING, "Unknown equipment slot provided in the attribute section while parsing the item by the name of " + itemName + " during item initialization and parsing stage!");
                continue;
            }

            ConfigurationSection modifierSection = attributeSection.getConfigurationSection(slotName);
            for (String attributeName : modifierSection.getKeys(false)) {
                Attribute attribute;
                try {
                    attribute = Attribute.valueOf(attributeName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    LogUtil.log(Level.WARNING, "Unknown attribute provided while parsing the item by the name of " + itemName + " during item initialization and parsing stage!");
                    continue;
                }

                if (slot != null) {
                    attributes.put(attribute, new AttributeModifier(UUID.randomUUID(), "test-value", modifierSection.getDouble(attributeName), AttributeModifier.Operation.ADD_NUMBER, slot));
                } else {
                    for (EquipmentSlot everySlot : EquipmentSlot.values()) {
                        attributes.put(attribute, new AttributeModifier(UUID.randomUUID(), "test-value", modifierSection.getDouble(attributeName), AttributeModifier.Operation.ADD_NUMBER, everySlot));
                    }
                }
            }
        }

        return attributes;
    }
}