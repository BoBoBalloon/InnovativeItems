package me.boboballoon.innovativeitems.config;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.item.*;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.TextUtil;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.logging.Level;

/**
 * A class built for parsing configuration sections and convert into CustomItem objects
 */
public class ItemParser {
    /**
     * A util method used to parse a custom item from a config section
     * 
     * @param section the config section
     * @param name the name of the item
     * @return the custom item (null if an error occurred)
     */
    public static CustomItem parseItem(ConfigurationSection section, String name) {
        if (!section.contains("material")) {
            LogUtil.log(Level.WARNING, "Could not find material field while parsing the item by the name of " + name + "!");
            return null;
        }

        Material material;
        try {
            material = Material.valueOf(section.getString("material").toUpperCase());
        } catch (IllegalArgumentException e) {
            LogUtil.log(Level.WARNING, "Unknown material provided while parsing the item by the name of " + name + " during item initialization and parsing stage!");
            return null;
        }


        Ability ability;
        if (section.contains("ability")) {
            ability = ItemParser.getAbility(section, name);
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
            lore = ItemParser.getLore(section);
        } else {
            lore = null;
        }

        Map<Enchantment, Integer> enchantments;
        if (section.contains("enchantments")) {
            enchantments = ItemParser.getEnchantments(section, name);
        } else {
            enchantments = null;
        }

        List<ItemFlag> flags;
        if (section.contains("flags")) {
            flags = ItemParser.getItemFlags(section, name);
        } else {
            flags = null;
        }

        Multimap<Attribute, AttributeModifier> attributes;
        if (section.contains("attributes")) {
            attributes = ItemParser.getAttributes(section, name);
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

        //skull item
        if (section.contains("skull") && material == Material.PLAYER_HEAD) {
            ConfigurationSection skullSection = section.getConfigurationSection("skull");
            return new CustomItemSkull(name, ability, displayName, lore, enchantments, flags, attributes, customModelData, unbreakable, ItemParser.getSkullName(skullSection));
        }

        //leather armor item
        if (section.contains("leather-armor") && CustomItemLeatherArmor.isLeatherArmor(material)) {
            ConfigurationSection leatherArmorSection = section.getConfigurationSection("leather-armor");
            return new CustomItemLeatherArmor(name, ability, material, displayName, lore, enchantments, flags, attributes, customModelData, unbreakable, ItemParser.getRGB(leatherArmorSection, name), ItemParser.getColor(leatherArmorSection, name));
        }

        //potion item
        if (section.contains("potion") && CustomItemPotion.isPotion(material)) {
            ConfigurationSection potionSection = section.getConfigurationSection("potion");
            return new CustomItemPotion(name, ability, material, displayName, lore, enchantments, flags, attributes, customModelData, unbreakable, ItemParser.getRGB(potionSection, name), ItemParser.getColor(potionSection, name), ItemParser.getPotionEffects(potionSection, name));
        }
        
        //generic item
        return new CustomItemGeneric(name, ability, material, displayName, lore, enchantments, flags, attributes, customModelData, unbreakable);
    }

    /**
     * Get the ability field from an item config section
     */
    private static Ability getAbility(ConfigurationSection section, String itemName) {
        String rawAbility = section.getString("ability");
        Ability ability = InnovativeItems.getInstance().getItemCache().getAbility(rawAbility);

        if (ability == null) {
            LogUtil.log(Level.WARNING, "Could not find ability with the name " + rawAbility + " while parsing the item by the name of " + itemName + " during item initialization and parsing stage!");
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
    private static List<ItemFlag> getItemFlags(ConfigurationSection section, String itemName) {
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
    private static Multimap<Attribute, AttributeModifier> getAttributes(ConfigurationSection section, String itemName) {
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

    /**
     * Get the skull name field from an skull config section
     */
    private static String getSkullName(ConfigurationSection section) {
        if (!section.contains("player-name")) {
            return null;
        }

        return section.getString("player-name");
    }

    /**
     * Get the color from rgb value field from an item config section
     */
    private static Color getRGB(ConfigurationSection section, String itemName) {
        if (!section.contains("rgb")) {
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
            LogUtil.log(Level.WARNING, "There was an error parsing the rgb values of " + itemName + "!");
            return null;
        }

        return Color.fromRGB(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * Get the color from color name value field from an item config section
     */
    private static Color getColor(ConfigurationSection section, String itemName) {
        if (!section.contains("color")) {
            return null;
        }

        String rawColor = section.getString("color").toUpperCase();

        Color color;
        try {
            color = DyeColor.valueOf(rawColor).getColor();
        } catch (IllegalArgumentException ignore) {
            LogUtil.log(Level.WARNING, "There was an error parsing the color of " + itemName + "! Please make sure that the value you entered was a real color!");
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
                LogUtil.log(Level.WARNING, "There was an error parsing one of the effect strings of " + itemName + "! Please make sure that the value you entered followed the potion effect syntax!");
                continue;
            }

            PotionEffectType type = PotionEffectType.getByName(components[0]);

            if (type == null) {
                LogUtil.log(Level.WARNING, "There was an error parsing one of the effect strings of " + itemName + "! Please make sure that the potion name you entered was correct!");
                continue;
            }

            int duration;
            try {
                duration = Integer.parseInt(components[1]);
            } catch (NumberFormatException e) {
                LogUtil.log(Level.WARNING, "There was an error parsing one of the effect strings of " + itemName + "! Please make sure that the duration you entered was an integer!");
                continue;
            }

            int level;
            try {
                level = Integer.parseInt(components[2]);
            } catch (NumberFormatException e) {
                LogUtil.log(Level.WARNING, "There was an error parsing one of the effect strings of " + itemName + "! Please make sure that the level you entered was an integer!");
                continue;
            }

            effects.add(new PotionEffect(type, duration, level));
        }

        return effects;
    }
}
