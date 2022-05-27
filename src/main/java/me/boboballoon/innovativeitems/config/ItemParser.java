package me.boboballoon.innovativeitems.config;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.item.*;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.RevisedEquipmentSlot;
import me.boboballoon.innovativeitems.util.TextUtil;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A class built for parsing configuration sections and convert into CustomItem objects
 */
public final class ItemParser {
    /**
     * Constructor to prevent people from using this util class in an object oriented way
     */
    private ItemParser() {}

    /**
     * A util method used to parse a custom item from a config section
     * 
     * @param section the config section
     * @param name the name of the item
     * @return the custom item (null if an error occurred)
     */
    @Nullable
    public static CustomItem parseItem(ConfigurationSection section, String name) {
        if (!section.isString("material")) {
            LogUtil.log(LogUtil.Level.WARNING, "Could not find material field while parsing the item by the name of " + name + "!");
            return null;
        }

        Material material;
        try {
            material = Material.valueOf(section.getString("material").toUpperCase());
        } catch (IllegalArgumentException e) {
            LogUtil.log(LogUtil.Level.WARNING, "Unknown material provided while parsing the item by the name of " + name + " during item initialization and parsing stage!");
            return null;
        }

        Ability ability = section.isString("ability") || section.isConfigurationSection("ability") ? ItemParser.getAbility(section, name) : null;

        String displayName = section.isString("display-name") ? TextUtil.format(section.getString("display-name")) : null;

        List<String> lore = section.isList("lore") ? ItemParser.getLore(section) : null;

        Map<Enchantment, Integer> enchantments = section.isConfigurationSection("enchantments") ? ItemParser.getEnchantments(section, name) : null;

        List<ItemFlag> flags = section.isList("flags") ? ItemParser.getItemFlags(section, name) : null;

        Multimap<Attribute, AttributeModifier> attributes = section.isConfigurationSection("attributes") ? ItemParser.getAttributes(section, name) : null;

        Integer customModelData = section.isInt("custom-model-data") ? section.getInt("custom-model-data") : null;

        boolean unbreakable = section.getBoolean("unbreakable"); //default value is false if not provided

        boolean placeable = section.getBoolean("placeable"); //default value is false if not provided

        boolean soulbound = section.getBoolean("soulbound"); //default value is false if not provided

        boolean wearable = section.isBoolean("wearable") ? section.getBoolean("wearable") : true;

        int maxDurability = section.isInt("max-durability") ? section.getInt("max-durability") : material.getMaxDurability();

        //skull item
        if (section.isConfigurationSection("skull") && material == Material.PLAYER_HEAD) {
            ConfigurationSection skullSection = section.getConfigurationSection("skull");
            return new CustomItemSkull(name, ability, displayName, lore, enchantments, flags, attributes, customModelData, placeable, soulbound, wearable, ItemParser.getSkullName(skullSection), ItemParser.getSkullBase64(skullSection));
        }

        //leather armor item
        if (section.isConfigurationSection("leather-armor") && CustomItemLeatherArmor.isLeatherArmor(material)) {
            ConfigurationSection leatherArmorSection = section.getConfigurationSection("leather-armor");
            DyeColor color = ItemParser.getColor(leatherArmorSection, name);
            return new CustomItemLeatherArmor(name, ability, material, displayName, lore, enchantments, flags, attributes, customModelData, unbreakable, soulbound, wearable, maxDurability, ItemParser.getRGB(leatherArmorSection, name), color != null ? color.getColor() : null);
        }

        //potion item
        if (section.isConfigurationSection("potion") && CustomItemPotion.isPotion(material)) {
            ConfigurationSection potionSection = section.getConfigurationSection("potion");
            DyeColor color = ItemParser.getColor(potionSection, name);
            return new CustomItemPotion(name, ability, material, displayName, lore, enchantments, flags, attributes, customModelData, soulbound, ItemParser.getRGB(potionSection, name), color != null ? color.getColor() : null, ItemParser.getPotionEffects(potionSection, name));
        }

        //banner item
        if (section.isConfigurationSection("banner") && CustomItemBanner.isBanner(material)) {
            ConfigurationSection bannerSection = section.getConfigurationSection("banner");
            return new CustomItemBanner(name, ability, material, displayName, lore, enchantments, flags, attributes, customModelData, placeable, soulbound, wearable, maxDurability, ItemParser.getBannerPatterns(bannerSection, name));
        }

        //firework item
        if (section.isConfigurationSection("firework") && material == Material.FIREWORK_ROCKET) {
            ConfigurationSection fireworkSection = section.getConfigurationSection("firework");
            return new CustomItemFirework(name, ability, displayName, lore, enchantments, flags, attributes, customModelData, soulbound, ItemParser.getFireworkEffects(fireworkSection, name), ItemParser.getFireworkPower(fireworkSection, name));
        }

        //shield item
        if (section.isConfigurationSection("shield") && material == Material.SHIELD) {
            ConfigurationSection shieldSection = section.getConfigurationSection("shield");
            return new CustomItemShield(name, ability, displayName, lore, enchantments, flags, attributes, customModelData, soulbound, wearable, maxDurability, ItemParser.getBannerPatterns(shieldSection, name), ItemParser.getColor(shieldSection, name));
        }
        
        //generic item
        return new CustomItem(name, ability, material, displayName, lore, enchantments, flags, attributes, customModelData, unbreakable, placeable, soulbound, wearable, maxDurability);
    }

    /**
     * Get the ability field from an item config section
     */
    private static Ability getAbility(ConfigurationSection section, String itemName) {
        Ability ability = null;
        String abilityName = null;

        if (section.isString("ability")) {
            String rawAbility = section.getString("ability");
            abilityName = rawAbility;
            ability = InnovativeItems.getInstance().getItemCache().getAbility(rawAbility);
        }

        boolean isConfigurationSection = section.isConfigurationSection("ability");
        if (isConfigurationSection && InnovativeItems.isPluginPremium()) {
            ConfigurationSection abilitySection = section.getConfigurationSection("ability");
            abilityName = itemName + "-anonymous-ability";
            ability = AbilityParser.parseAbility(abilitySection, abilityName);
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
    private static List<String> getLore(ConfigurationSection section) {
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
    private static Map<Enchantment, Integer> getEnchantments(ConfigurationSection section, String itemName) {
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
    private static List<ItemFlag> getItemFlags(ConfigurationSection section, String itemName) {
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
    private static Multimap<Attribute, AttributeModifier> getAttributes(ConfigurationSection section, String itemName) {
        Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();
        ConfigurationSection attributeSection = section.getConfigurationSection("attributes");

        for (String slotName : attributeSection.getKeys(false)) {
            RevisedEquipmentSlot slot;
            try {
                slot = RevisedEquipmentSlot.valueOf(slotName.toUpperCase());
            } catch (IllegalArgumentException e) {
                LogUtil.log(LogUtil.Level.WARNING, "The unknown equipment slot " + slotName.toUpperCase() + " was provided in the attribute section while parsing the item by the name of " + itemName + " during item initialization and parsing stage!");
                continue;
            }

            ConfigurationSection modifierSection = attributeSection.getConfigurationSection(slotName);
            byte counter = Byte.MIN_VALUE;
            for (String attributeName : modifierSection.getKeys(false)) {
                Attribute attribute;
                try {
                    attribute = Attribute.valueOf(attributeName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    LogUtil.log(LogUtil.Level.WARNING, "Unknown attribute provided while parsing the item by the name of " + itemName + " during item initialization and parsing stage!");
                    continue;
                }

                byte[] array = Arrays.copyOf(itemName.getBytes(), itemName.getBytes().length + 1);
                array[itemName.getBytes().length] = counter;
                attributes.put(attribute, new AttributeModifier(UUID.nameUUIDFromBytes(array), "test-value", modifierSection.getDouble(attributeName), AttributeModifier.Operation.ADD_NUMBER, slot.getSlot()));
                counter++;
            }
        }

        return attributes;
    }

    /**
     * Get the skull name field from an skull config section
     */
    private static String getSkullName(ConfigurationSection section) {
        if (!section.isString("player-name")) {
            return null;
        }

        return section.getString("player-name");
    }

    /**
     * Get the skull base64 field from an skull config section
     */
    private static String getSkullBase64(ConfigurationSection section) {
        if (!section.isString("base64")) {
            return null;
        }

        return section.getString("base64");
    }

    /**
     * Get the color from rgb value field from an item config section
     */
    private static Color getRGB(ConfigurationSection section, String itemName) {
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
    private static DyeColor getColor(ConfigurationSection section, String itemName) {
        if (!section.isString("color")) {
            return null;
        }

        String rawColor = section.getString("color").toUpperCase();

        DyeColor color;
        try {
            color = DyeColor.valueOf(rawColor);
        } catch (IllegalArgumentException ignore) {
            LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing the color of " + itemName + "! Please make sure that the value you entered was a real color!");
            return null;
        }

        return color;
    }

    /**
     * Get the potion effects from the effects field from a potion config section
     */
    private static List<PotionEffect> getPotionEffects(ConfigurationSection section, String itemName) {
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
    private static List<Pattern> getBannerPatterns(ConfigurationSection section, String itemName) {
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
    private static List<FireworkEffect> getFireworkEffects(ConfigurationSection section, String itemName) {
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

            FireworkEffect effect = ItemParser.getFireworkEffect(effectSection, itemName);

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
    private static FireworkEffect getFireworkEffect(ConfigurationSection section, String itemName) {
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
    private static Integer getFireworkPower(ConfigurationSection section, String itemName) {
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
