package me.boboballoon.innovativeitems.config;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.item.*;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.RevisedEquipmentSlot;
import me.boboballoon.innovativeitems.util.TextUtil;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * A class that is responsible for building items and placing them into the cache
 */
public class ItemBuilder {
    private final InitializationManager parent;
    private final File itemDirectory;
    private final Map<String, IncompleteItem<?>> finalizedItems;
    private final ConfigManager configManager;
    private final InnovativeCache cache;

    public ItemBuilder(InitializationManager parent, ConfigManager configManager, InnovativeCache cache) {
        this.parent = parent;
        this.itemDirectory = this.parent.getItemDirectory();
        this.finalizedItems = new HashMap<>();
        this.configManager = configManager;
        this.cache = cache;
    }

    /**
     * Gets the instance of the parent init manager that made this item builder
     *
     * @return the instance of the parent init manager that made this item builder
     */
    public InitializationManager getParent() {
        return this.parent;
    }

    /**
     * A method used to get the directory that holds all the item configuration files
     *
     * @return the directory that holds all the item configuration files
     */
    public File getItemDirectory() {
        return this.itemDirectory;
    }

    /**
     * A method used to return a map of incomplete items ready to be built and registered
     *
     * @return a map of incomplete items ready to be built and registered
     */
    public Map<String, IncompleteItem<?>> getFinalizedItems() {
        return this.finalizedItems;
    }

    /**
     * A util method used to parse a custom item from a config section
     */
    public void buildIncompleteItems() {
        int maxCount = configManager.shouldGenerateDefaultConfigs() ? 16 : 10;

        for (File file : this.itemDirectory.listFiles()) {
            YamlConfiguration configuration = new YamlConfiguration();

            try {
                configuration.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                LogUtil.log(LogUtil.Level.WARNING, "A " + e.getClass().getSimpleName() + " occurred while loading " + file.getName() + " during item initialization and parsing stage!");
                if (configManager.getDebugLevel() >= LogUtil.Level.DEV.getDebugLevel()) {
                    e.printStackTrace();
                }
                continue;
            }

            for (String key : configuration.getKeys(false)) {
                ConfigurationSection section = configuration.getConfigurationSection(key);

                String name = section.getName();

                if (this.cache.contains(name)) {
                    LogUtil.log(LogUtil.Level.WARNING, "Element with the name of " + name + ", is already registered! Skipping item...");
                    continue;
                }

                if (!InnovativeItems.isPluginPremium() && this.cache.getItemAmount() >= maxCount) {
                    LogUtil.logUnblocked(LogUtil.Level.WARNING, "You have reached the maximum amount of custom items for the free version of the plugin! Skipping the item identified as: " + key);
                    continue;
                }

                this.parseItem(section, name);
            }
        }
    }

    /**
     * A util method used to parse a custom item from a config section
     *
     * @param section the config section
     * @param name    the name of the item
     */
    private void parseItem(ConfigurationSection section, String name) {
        if (!section.isString("material")) {
            LogUtil.log(LogUtil.Level.WARNING, "Could not find material field while parsing the item by the name of " + name + "!");
            return;
        }

        Object ability = section.isString("ability") || section.isConfigurationSection("ability") ? this.getAbility(section, name) : null;
        
        Material material;
        try {
            material = Material.valueOf(section.getString("material").toUpperCase());
        } catch (IllegalArgumentException e) {
            LogUtil.log(LogUtil.Level.WARNING, "Unknown material provided while parsing the item by the name of " + name + " during item initialization and parsing stage!");
            return;
        }

        String displayName;
        if (section.isString("display-name")) {
            displayName = TextUtil.format(section.getString("display-name"));
        } else {
            displayName = null;
        }

        List<String> lore;
        if (section.isList("lore")) {
            lore = this.getLore(section);
        } else {
            lore = null;
        }

        Map<Enchantment, Integer> enchantments;
        if (section.isConfigurationSection("enchantments")) {
            enchantments = this.getEnchantments(section, name);
        } else {
            enchantments = null;
        }

        List<ItemFlag> flags;
        if (section.isList("flags")) {
            flags = this.getItemFlags(section, name);
        } else {
            flags = null;
        }

        Multimap<Attribute, AttributeModifier> attributes;
        if (section.isConfigurationSection("attributes")) {
            attributes = this.getAttributes(section, name);
        } else {
            attributes = null;
        }

        Integer customModelData;
        if (section.isInt("custom-model-data")) {
            customModelData = section.getInt("custom-model-data");
        } else {
            customModelData = null;
        }

        boolean unbreakable;
        if (section.isBoolean("unbreakable")) {
            unbreakable = section.getBoolean("unbreakable");
        } else {
            unbreakable = false;
        }

        boolean placeable;
        if (section.isBoolean("placeable")) {
            placeable = section.getBoolean("placeable");
        } else {
            placeable = false;
        }

        //skull item
        if (section.isConfigurationSection("skull") && material == Material.PLAYER_HEAD) {
            ConfigurationSection skullSection = section.getConfigurationSection("skull");
            this.finalizedItems.put(name, new IncompleteItem<>(CustomItemSkull.class, name, ability, displayName, lore, enchantments, flags, attributes, customModelData, placeable, this.getSkullName(skullSection), this.getSkullBase64(skullSection)));
            return;
        }

        //leather armor item
        if (section.isConfigurationSection("leather-armor") && CustomItemLeatherArmor.isLeatherArmor(material)) {
            ConfigurationSection leatherArmorSection = section.getConfigurationSection("leather-armor");
            this.finalizedItems.put(name, new IncompleteItem<>(CustomItemLeatherArmor.class, name, ability, material, displayName, lore, enchantments, flags, attributes, customModelData, unbreakable, this.getRGB(leatherArmorSection, name), this.getColor(leatherArmorSection, name)));
            return;
        }

        //potion item
        if (section.isConfigurationSection("potion") && CustomItemPotion.isPotion(material)) {
            ConfigurationSection potionSection = section.getConfigurationSection("potion");
            this.finalizedItems.put(name, new IncompleteItem<>(CustomItemPotion.class, name, ability, material, displayName, lore, enchantments, flags, attributes, customModelData, this.getRGB(potionSection, name), this.getColor(potionSection, name), this.getPotionEffects(potionSection, name)));
            return;
        }

        //banner item
        if (section.isConfigurationSection("banner") && CustomItemBanner.isBanner(material)) {
            ConfigurationSection bannerSection = section.getConfigurationSection("banner");
            this.finalizedItems.put(name, new IncompleteItem<>(CustomItemBanner.class, name, ability, material, displayName, lore, enchantments, flags, attributes, customModelData, placeable, this.getBannerPatterns(bannerSection, name)));
            return;
        }

        //firework item
        if (section.isConfigurationSection("firework") && material == Material.FIREWORK_ROCKET) {
            ConfigurationSection fireworkSection = section.getConfigurationSection("firework");
            this.finalizedItems.put(name, new IncompleteItem<>(CustomItemFirework.class, name, ability, displayName, lore, enchantments, flags, attributes, customModelData, this.getFireworkEffects(fireworkSection, name), this.getFireworkPower(fireworkSection, name)));
            return;
        }

        //generic item
        this.finalizedItems.put(name, new IncompleteItem<>(CustomItem.class, name, ability, material, displayName, lore, enchantments, flags, attributes, customModelData, unbreakable, placeable));
    }

    /**
     * Get the ability field from an item config section
     */
    private Object getAbility(ConfigurationSection section, String itemName) {
        Ability ability = null;
        String abilityName = null;

        if (section.isString("ability")) {
            return section.getString("ability");
        }

        boolean isConfigurationSection = section.isConfigurationSection("ability");
        if (isConfigurationSection && InnovativeItems.isPluginPremium()) {
            ConfigurationSection abilitySection = section.getConfigurationSection("ability");
            abilityName = itemName + "-anonymous-ability";
            ability = AbilityParser.parseAbility(abilitySection, abilityName);
            AbilityParser.registerAbilityTimer(ability, abilitySection);
        } else if (isConfigurationSection) {
            LogUtil.logUnblocked(LogUtil.Level.WARNING, "An anonymous ability was used for the " + itemName + " item but are not supported in the free version of the plugin!");
            return null;
        }

        if (ability == null) {
            LogUtil.log(LogUtil.Level.WARNING, "Could not find or parse ability with the name " + abilityName + " while parsing the item by the name of " + itemName + " during item initialization and parsing stage!");
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
                LogUtil.log(LogUtil.Level.WARNING, "Could not find enchantment with the name " + enchantmentName + " while parsing the item by the name of " + itemName + " during item initialization and parsing stage!");
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
                LogUtil.log(LogUtil.Level.WARNING, "Unknown itemflag provided while parsing the item by the name of " + itemName + " during item initialization and parsing stage!");
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
            RevisedEquipmentSlot slot;
            try {
                slot = RevisedEquipmentSlot.valueOf(slotName.toUpperCase());
            } catch (IllegalArgumentException e) {
                LogUtil.log(LogUtil.Level.WARNING, "Unknown equipment slot provided in the attribute section while parsing the item by the name of " + itemName + " during item initialization and parsing stage!");
                continue;
            }

            ConfigurationSection modifierSection = attributeSection.getConfigurationSection(slotName);
            for (String attributeName : modifierSection.getKeys(false)) {
                Attribute attribute;
                try {
                    attribute = Attribute.valueOf(attributeName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    LogUtil.log(LogUtil.Level.WARNING, "Unknown attribute provided while parsing the item by the name of " + itemName + " during item initialization and parsing stage!");
                    continue;
                }

                if (slot != RevisedEquipmentSlot.ANY) {
                    attributes.put(attribute, new AttributeModifier(UUID.randomUUID(), "test-value", modifierSection.getDouble(attributeName), AttributeModifier.Operation.ADD_NUMBER, slot.getSlot()));
                } else {
                    for (EquipmentSlot everySlot : EquipmentSlot.values()) {
                        attributes.put(attribute, new AttributeModifier(UUID.randomUUID(), "test-value", modifierSection.getDouble(attributeName), AttributeModifier.Operation.ADD_NUMBER, everySlot));
                    }
                }
            }
        }

        return attributes;
    }

    /**
     * Get the skull name field from an skull config section
     */
    private String getSkullName(ConfigurationSection section) {
        if (!section.isString("player-name")) {
            return null;
        }

        return section.getString("player-name");
    }

    /**
     * Get the skull base64 field from an skull config section
     */
    private String getSkullBase64(ConfigurationSection section) {
        if (!section.isString("base64")) {
            return null;
        }

        return section.getString("base64");
    }

    /**
     * Get the color from rgb value field from an item config section
     */
    private Color getRGB(ConfigurationSection section, String itemName) {
        if (!section.isString("rgb")) {
            return null;
        }

        String[] rgbRaw = section.getString("rgb").split(",");

        if (rgbRaw.length != 3) {
            return null;
        }

        int[] rgb = new int[3];
        try {
            rgb[0] = Integer.parseInt(rgbRaw[0]);
            rgb[1] = Integer.parseInt(rgbRaw[1]);
            rgb[2] = Integer.parseInt(rgbRaw[2]);
        } catch (NumberFormatException e) {
            LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing the rgb values of " + itemName + "!");
            return null;
        }

        return Color.fromRGB(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * Get the color from color name value field from an item config section
     */
    private Color getColor(ConfigurationSection section, String itemName) {
        if (!section.isString("color")) {
            return null;
        }

        String rawColor = section.getString("color").toUpperCase();

        Color color;
        try {
            color = DyeColor.valueOf(rawColor).getColor();
        } catch (IllegalArgumentException ignore) {
            LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing the color of " + itemName + "! Please make sure that the value you entered was a real color!");
            return null;
        }

        return color;
    }

    /**
     * Get the potion effects from the effects field from a potion config section
     */
    private List<PotionEffect> getPotionEffects(ConfigurationSection section, String itemName) {
        List<String> rawEffects = section.getStringList("effects");
        List<PotionEffect> effects = new ArrayList<>();

        for (String rawEffect : rawEffects) {
            String[] components = rawEffect.split(" ");

            if (components.length != 3) {
                LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing one of the effect strings of " + itemName + "! Please make sure that the value you entered followed the potion effect syntax!");
                continue;
            }

            PotionEffectType type = PotionEffectType.getByName(components[0]);

            if (type == null) {
                LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing one of the effect strings of " + itemName + "! Please make sure that the potion name you entered was correct!");
                continue;
            }

            int duration;
            try {
                duration = Integer.parseInt(components[1]);
            } catch (NumberFormatException e) {
                LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing one of the effect strings of " + itemName + "! Please make sure that the duration you entered was an integer!");
                continue;
            }

            int level;
            try {
                level = Integer.parseInt(components[2]);
            } catch (NumberFormatException e) {
                LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing one of the effect strings of " + itemName + "! Please make sure that the level you entered was an integer!");
                continue;
            }

            effects.add(new PotionEffect(type, duration, level));
        }

        return effects;
    }

    /**
     * Get the patterns field from the banner field from a banner config section
     */
    private List<Pattern> getBannerPatterns(ConfigurationSection section, String itemName) {
        List<String> rawPatterns = section.getStringList("patterns");
        List<Pattern> patterns = new ArrayList<>();

        for (String rawPattern : rawPatterns) {
            String[] components = rawPattern.split(" ");

            if (components.length != 2) {
                LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing one of the pattern strings of " + itemName + "! Please make sure that the value you entered followed the banner pattern syntax!");
                continue;
            }

            PatternType type;
            try {
                type = PatternType.valueOf(components[0]);
            } catch (IllegalArgumentException e) {
                LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing one of the pattern strings of " + itemName + "! Please make sure that the pattern type name you entered was correct!");
                continue;
            }

            DyeColor color;
            try {
                color = DyeColor.valueOf(components[1]);
            } catch (IllegalArgumentException e) {
                LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing one of the pattern strings of " + itemName + "! Please make sure that the dye color name you entered was correct!");
                continue;
            }

            patterns.add(new Pattern(color, type));
        }

        return patterns;
    }

    /**
     * Get all the firework effects from the config file and parse and initialize
     */
    private List<FireworkEffect> getFireworkEffects(ConfigurationSection section, String itemName) {
        if (!section.isConfigurationSection("effects")) {
            return null;
        }

        ConfigurationSection effectsSection = section.getConfigurationSection("effects");

        List<FireworkEffect> effects = new ArrayList<>();

        for (String key : effectsSection.getKeys(false)) {
            if (!effectsSection.isConfigurationSection(key)) {
                continue;
            }

            ConfigurationSection effectSection = effectsSection.getConfigurationSection(key);

            FireworkEffect effect = this.getFireworkEffect(effectSection, itemName);

            if (effect == null) {
                continue;
            }

            effects.add(effect);
        }

        return effects;
    }

    /**
     * Get a firework effect from an effect config section
     */
    private FireworkEffect getFireworkEffect(ConfigurationSection section, String itemName) {
        boolean flicker;
        if (section.isBoolean("flicker")) {
            flicker = section.getBoolean("flicker");
        } else {
            flicker = false;
        }

        boolean trail;
        if (section.isBoolean("trail")) {
            trail = section.getBoolean("trail");
        } else {
            trail = false;
        }

        FireworkEffect.Type type;
        if (section.isString("type")) {
            try {
                type = FireworkEffect.Type.valueOf(section.getString("type"));
            } catch (IllegalArgumentException e) {
                LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing the firework type of " + itemName + "! Please make sure that the firework type name you entered was correct!");
                return null;
            }
        } else {
            type = FireworkEffect.Type.BALL;
        }

        List<Color> colors = new ArrayList<>();
        if (section.isList("colors")) {
            List<String> rawColors = section.getStringList("colors");

            for (String rawColor : rawColors) {
                try {
                    Color color = DyeColor.valueOf(rawColor).getColor();
                    colors.add(color);
                } catch (IllegalArgumentException ignore) {
                    LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing the color of " + itemName + "! Please make sure that the value you entered was a real color!");
                    return null;
                }
            }
        }

        List<Color> fadeColors = new ArrayList<>();
        if (section.isList("fade-colors")) {
            List<String> rawColors = section.getStringList("fade-colors");

            for (String rawColor : rawColors) {
                try {
                    Color color = DyeColor.valueOf(rawColor).getColor();
                    fadeColors.add(color);
                } catch (IllegalArgumentException ignore) {
                    LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing the fade color of " + itemName + "! Please make sure that the value you entered was a real color!");
                    return null;
                }
            }
        }

        return FireworkEffect.builder()
                .flicker(flicker)
                .trail(trail)
                .with(type)
                .withColor(colors)
                .withFade(fadeColors)
                .build();
    }

    /**
     * Get the flight time field and convert it to the power field that can be passed into the object
     */
    private Integer getFireworkPower(ConfigurationSection section, String itemName) {
        if (!section.isInt("flight-time")) {
            return null;
        }

        float flightTime = section.getInt("flight-time");

        int power = Math.round((flightTime / 20));

        if (power > 128 || power < 0) {
            LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing the firework flight time of " + itemName + "! Please make sure that the flight time is less than or equal to 1280 and great than or equal to 0!");
            return null;
        }

        return power;
    }
}
