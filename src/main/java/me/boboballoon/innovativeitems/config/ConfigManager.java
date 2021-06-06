package me.boboballoon.innovativeitems.config;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.Ability;
import me.boboballoon.innovativeitems.items.Item;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * A class used to cache and parse config files
 */
public class ConfigManager {
    public static final HashMap<String, Ability> ABILITIES = new HashMap<>();
    public static final HashMap<String, Item> ITEMS = new HashMap<>();

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
            //start player inventory item checker
            //end player inventory item checker
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

        //ConfigManager.loadAbilities(abilities); (add in later)

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

                String name = section.getName();

                if (ConfigManager.ITEMS.containsKey(name)) {
                    LogUtil.log(Level.SEVERE, "Item by the name of " + name + ", already is registered! Skipping item...");
                    continue;
                }

                if (!section.contains("material")) {
                    LogUtil.log(Level.SEVERE, "Could not find material field while parsing the item by the name of " + name + "!");
                    continue;
                }

                Material material;
                try {
                    material = Material.valueOf(section.getString("material").toUpperCase());
                } catch (IllegalArgumentException e) {
                    LogUtil.log(Level.SEVERE, "Unknown material provided while parsing the item by the name of " + name + " during item initialization and parsing stage!");
                    continue;
                }


                Ability ability;
                if (section.contains("ability")) {
                    String rawAbility = section.getString("ability");
                    ability = ConfigManager.ABILITIES.get(rawAbility);

                    if (ability == null) {
                        LogUtil.log(Level.SEVERE, "Could not find ability with the name " + rawAbility + " while parsing the item by the name of " + name + " during item initialization and parsing stage!");
                    }
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
                    lore = section.getStringList("lore");

                    for (int i = 0; i < lore.size(); i++) {
                        String element = TextUtil.format(lore.get(i));
                        lore.set(i, element);
                    }
                } else {
                    lore = null;
                }

                Map<Enchantment, Integer> enchantments;
                if (section.contains("enchantments")) {
                    ConfigurationSection enchantmentSection = section.getConfigurationSection("enchantments");
                    enchantments = new HashMap<>();

                    for (String enchantmentName : enchantmentSection.getKeys(false)) {
                        int level = enchantmentSection.getInt(enchantmentName);
                        Enchantment enchantment = Enchantment.getByName(enchantmentName);

                        if (enchantment == null) {
                            LogUtil.log(Level.SEVERE, "Could not find enchantment with the name " + enchantmentName + " while parsing the item by the name of " + name + " during item initialization and parsing stage!");
                            continue;
                        }

                        enchantments.put(enchantment, level);
                    }
                } else {
                    enchantments = null;
                }

                List<ItemFlag> flags;
                if (section.contains("flags")) {
                    flags = new ArrayList<>();
                    for (String flag : section.getStringList("flags")) {
                        ItemFlag itemFlag;
                        try {
                            itemFlag = ItemFlag.valueOf(flag.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            LogUtil.log(Level.SEVERE, "Unknown itemflag provided while parsing the item by the name of " + name + " during item initialization and parsing stage!");
                            continue;
                        }
                        flags.add(itemFlag);
                    }
                } else {
                    flags = null;
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

                ConfigManager.ITEMS.put(name, new Item(name, ability, material, displayName, lore, enchantments, flags, customModelData, unbreakable));
            }
        }

        LogUtil.log(Level.INFO, "Item initialization and parsing complete!");
    }
}