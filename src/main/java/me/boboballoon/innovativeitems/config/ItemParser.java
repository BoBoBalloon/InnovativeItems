package me.boboballoon.innovativeitems.config;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.items.item.RecipeType;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.RevisedEquipmentSlot;
import me.boboballoon.innovativeitems.util.TextUtil;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * A class built for parsing configuration sections and convert into CustomItem objects
 */
public final class ItemParser {
    /**
     * Constructor to prevent people from using this util class in an object oriented way
     */
    private ItemParser() {
    }

    /**
     * A util method used to parse a custom item from a config section
     *
     * @param section the config section
     * @param name    the name of the item
     * @return the custom item (null if an error occurred)
     */
    @Nullable
    public static CustomItem parseItem(@NotNull ConfigurationSection section, @NotNull String name) {
        return ItemParser.parseItem(section, name, true);
    }


    /**
     * A util method used to parse a custom item from a config section
     *
     * @param section     the config section
     * @param name        the name of the item
     * @param parseRecipe if the recipe of the custom item should be parsed
     * @return the custom item (null if an error occurred)
     */
    @Nullable
    public static CustomItem parseItem(@NotNull ConfigurationSection section, @NotNull String name, boolean parseRecipe) {
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

        boolean updateItem = section.isBoolean("update-item") ? section.getBoolean("update-item") : true;

        ItemStack underlying = ItemParser.createUnderlyingItemStack(section, name, material, displayName, lore, enchantments, flags, attributes, customModelData, unbreakable, maxDurability);

        ImmutableList<Recipe> recipes = parseRecipe && section.isConfigurationSection("recipes") ? ItemParser.getRecipe(section, name, underlying) : null;

        return new CustomItem(name, ability, underlying, placeable, soulbound, wearable, maxDurability, updateItem, recipes);
    }

    /**
     * Create the underlying itemstack of a custom item
     */
    private static ItemStack createUnderlyingItemStack(ConfigurationSection section, @NotNull String identifier, @NotNull Material material, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, boolean unbreakable, int durability) {
        //skull item
        if (section.isConfigurationSection("skull") && material == Material.PLAYER_HEAD) {
            ConfigurationSection skullSection = section.getConfigurationSection("skull");
            return SkullItemStack.generateItem(identifier, itemName, lore, enchantments, flags, attributes, customModelData, ItemParser.getSkullName(skullSection), ItemParser.getSkullBase64(skullSection));
        }

        //leather armor item
        if (section.isConfigurationSection("leather-armor") && LeatherArmorItemStack.isLeatherArmor(material)) {
            ConfigurationSection leatherArmorSection = section.getConfigurationSection("leather-armor");
            DyeColor color = ItemParser.getColor(leatherArmorSection, itemName);
            return LeatherArmorItemStack.generateItem(identifier, material, itemName, lore, enchantments, flags, attributes, customModelData, unbreakable, durability, ItemParser.getRGB(leatherArmorSection, itemName), color != null ? color.getColor() : null);
        }

        //potion item
        if (section.isConfigurationSection("potion") && PotionItemStack.isPotion(material)) {
            ConfigurationSection potionSection = section.getConfigurationSection("potion");
            DyeColor color = ItemParser.getColor(potionSection, itemName);
            return PotionItemStack.generateItem(identifier, material, itemName, lore, enchantments, flags, attributes, customModelData, ItemParser.getRGB(potionSection, itemName), color != null ? color.getColor() : null, ItemParser.getPotionEffects(potionSection, itemName));
        }

        //banner item
        if (section.isConfigurationSection("banner") && BannerItemStack.isBanner(material)) {
            ConfigurationSection bannerSection = section.getConfigurationSection("banner");
            return BannerItemStack.generateItem(identifier, material, itemName, lore, enchantments, flags, attributes, customModelData, durability, ItemParser.getBannerPatterns(bannerSection, itemName));
        }

        //firework item
        if (section.isConfigurationSection("firework") && material == Material.FIREWORK_ROCKET) {
            ConfigurationSection fireworkSection = section.getConfigurationSection("firework");
            return FireworkItemStack.generateItem(identifier, itemName, lore, enchantments, flags, attributes, customModelData, ItemParser.getFireworkEffects(fireworkSection, itemName), ItemParser.getFireworkPower(fireworkSection, itemName));
        }

        //shield item
        if (section.isConfigurationSection("shield") && material == Material.SHIELD) {
            ConfigurationSection shieldSection = section.getConfigurationSection("shield");
            return ShieldItemStack.generateItem(identifier, itemName, lore, enchantments, flags, attributes, customModelData, durability, ItemParser.getBannerPatterns(shieldSection, itemName), ItemParser.getColor(shieldSection, itemName));
        }

        //generic item
        return CustomItem.generateItem(identifier, material, itemName, lore, enchantments, flags, attributes, customModelData, unbreakable, durability);
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

    /**
     * Get and parse the custom recipes BUT DOES NOT REGISTER IT, that is done when the item is registered
     */
    @Nullable
    private static ImmutableList<Recipe> getRecipe(@NotNull ConfigurationSection section, @NotNull String itemName, @NotNull ItemStack underlying) {
        if (!InnovativeItems.isPluginPremium()) {
            LogUtil.logUnblocked(LogUtil.Level.WARNING, "You cannot create a custom crafting recipe using the free version of the plugin! Skipping the recipe of the item identified as: " + itemName);
            return null;
        }

        ConfigurationSection recipesSection = section.getConfigurationSection("recipes");
        List<Recipe> recipes = new ArrayList<>();

        int counter = 0;
        for (String recipeName : recipesSection.getKeys(false)) {
            if (!recipesSection.isConfigurationSection(recipeName)) {
                continue;
            }

            ConfigurationSection recipeSection = recipesSection.getConfigurationSection(recipeName);

            if (!recipeSection.isString("type")) {
                LogUtil.log(LogUtil.Level.WARNING, "Warning on recipe " + recipeName + " on item " + itemName + ": Every recipe must contain the type field to specific the type of crafting recipe! Refer to the documentation for valid options...");
                continue;
            }

            RecipeType type;
            try {
                type = RecipeType.valueOf(recipeSection.getString("type").toUpperCase());
            } catch (IllegalArgumentException e) {
                LogUtil.log(LogUtil.Level.WARNING, "Warning on recipe " + recipeName + " on item " + itemName + ": You have entered an invalid type of crafting recipe! Refer to the documentation for valid options...");
                continue;
            }

            Recipe recipe;
            if (type == RecipeType.SHAPED) {
                recipe = ItemParser.parseShapedRecipe(recipeSection, itemName, recipeName, underlying, counter);
            } else if (type == RecipeType.FURNACE || type == RecipeType.BLAST_FURNACE || type == RecipeType.SMOKER || type == RecipeType.CAMPFIRE) {
                recipe = ItemParser.parseCookingRecipe(recipeSection, itemName, recipeName, underlying, type, counter);
            } else {
                LogUtil.log(LogUtil.Level.DEV, "Warning on recipe " + recipeName + " on item " + itemName + ": A valid recipe type without a proper implementation has been detected... Please report this to the developer of the plugin immediately!");
                continue;
            }

            if (recipe == null) {
                //error was already sent
                continue;
            }

            recipes.add(recipe);
            counter++;
        }

        return !recipes.isEmpty() ? ImmutableList.copyOf(recipes) : null;
    }

    /**
     * A method used to parse a configuration section to create a {@link ShapedRecipe}
     */
    @Nullable
    private static ShapedRecipe parseShapedRecipe(@NotNull ConfigurationSection recipeSection, @NotNull String itemName, @NotNull String recipeName, @NotNull ItemStack underlying, int count) {
        if (!recipeSection.isList("keys")) {
            LogUtil.log(LogUtil.Level.WARNING, "Warning on recipe " + recipeName + " on item " + itemName + ": A list of keys is required when making a custom crafting recipe! Please review the documentation for the correct syntax and layout...");
            return null;
        }

        List<String> keys = recipeSection.getStringList("keys");

        if (!keys.stream().allMatch(text -> text.matches("~?\\w:.+"))) { //regex = ^~?\w:.+$
            LogUtil.log(LogUtil.Level.WARNING, "Warning on recipe " + recipeName + " on item " + itemName + ": The proper syntax is a unique character followed by a colon followed by a material or custom item is required! Please review the documentation for the correct syntax...");
            return null;
        }

        Map<Character, RecipeChoice> keyMap = new HashMap<>();

        for (String text : keys) {
            String[] parsed = text.split(":");

            boolean assertMaterial = parsed[0].startsWith("~");
            char key = assertMaterial ? parsed[0].charAt(1) : parsed[0].charAt(0);

            if (keyMap.containsKey(key)) {
                LogUtil.log(LogUtil.Level.WARNING, "Warning on recipe " + recipeName + " on item " + itemName + ": You have already registered this character as a key, keys must be unique!");
                return null;
            }

            Material material;
            try {
                material = Material.valueOf(parsed[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                material = null;
            }

            if (assertMaterial && material != null) {
                keyMap.put(key, new RecipeChoice.ExactChoice(new ItemStack(material)));
            } else if (assertMaterial) {
                LogUtil.log(LogUtil.Level.WARNING, "Warning on recipe " + recipeName + " on item " + itemName + ": You have asserted that the provided key was to represent a material, however no such material exists!");
                return null;
            }

            CustomItem item = InnovativeItems.getInstance().getItemCache().getItem(parsed[1]);

            if (material == null && item == null) {
                LogUtil.log(LogUtil.Level.WARNING, "Warning on recipe " + recipeName + " on item " + itemName + ": The object you provided for the key of " + key + " was invalid!");
                return null;
            }

            //one must be valid as the boolean above ensures
            keyMap.put(key, new RecipeChoice.ExactChoice(item != null ? item.getItemStack() : new ItemStack(material)));
        }

        if (!recipeSection.isList("shape")) {
            LogUtil.log(LogUtil.Level.WARNING, "Warning on recipe " + recipeName + " on item " + itemName + ": A shape grid is required when making a custom crafting recipe! Please review the documentation for the correct syntax and layout...");
            return null;
        }

        List<String> shape = recipeSection.getStringList("shape");

        if (shape.size() > 3 || shape.size() < 1 || shape.stream().anyMatch(text -> text.length() > 3 || text.length() < 1)) {
            LogUtil.log(LogUtil.Level.WARNING, "Warning on recipe " + recipeName + " on item " + itemName + ": The shape grid must not be greater than three, but must at least have a length of one! The contents of the grid must be three characters long, each being valid keys!");
            return null;
        }

        Set<Character> presentKeys = new HashSet<>();
        int lastSize = -1;
        for (String row : shape) {
            if (lastSize != -1 && lastSize != row.length()) {
                LogUtil.log(LogUtil.Level.WARNING, "Warning on recipe " + recipeName + " on item " + itemName + ": The shape grid must be rectangular");
                return null;
            }
            lastSize = row.length();

            for (char character : row.toCharArray()) {
                if (character != ' ') {
                    presentKeys.add(character);
                }
            }
        }

        if (!presentKeys.equals(keyMap.keySet())) {
            LogUtil.log(LogUtil.Level.WARNING, "Warning on recipe " + recipeName + " on item " + itemName + ": The shape grid must both contain all specified keys in the key section and must not contain any unidentified keys with the exception of whitespace which is equal to air!");
            return null;
        }

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(InnovativeItems.getInstance(), "innovativeplugin-customitem-" + itemName + "-recipe." + count), underlying);

        recipe.shape(shape.toArray(new String[0]));

        for (Map.Entry<Character, RecipeChoice> entry : keyMap.entrySet()) {
            recipe.setIngredient(entry.getKey(), entry.getValue());
        }

        return recipe;
    }

    /**
     * A method used to parse a configuration section to create a {@link CookingRecipe}
     */
    @Nullable
    private static CookingRecipe<?> parseCookingRecipe(@NotNull ConfigurationSection recipeSection, @NotNull String itemName, @NotNull String recipeName, @NotNull ItemStack underlying, @NotNull RecipeType type, int count) {
        if (type != RecipeType.FURNACE && type != RecipeType.BLAST_FURNACE && type != RecipeType.SMOKER && type != RecipeType.CAMPFIRE) {
            return null;
        }

        if (!recipeSection.isString("key")) {
            LogUtil.log(LogUtil.Level.WARNING, "Warning on recipe " + recipeName + " on item " + itemName + ": Every cooking recipe must contain a valid key field that contains a material or custom item!");
            return null;
        }

        String rawKey = recipeSection.getString("key");
        boolean assertMaterial = rawKey.startsWith("~");
        RecipeChoice key = null;

        if (assertMaterial) {
            rawKey = rawKey.substring(1);
        }

        Material material;
        try {
            material = Material.valueOf(rawKey);
        } catch (IllegalArgumentException e) {
            material = null;
        }

        if (assertMaterial && material != null) {
            key = new RecipeChoice.ExactChoice(new ItemStack(material));
        } else if (assertMaterial) {
            LogUtil.log(LogUtil.Level.WARNING, "Warning on recipe " + recipeName + " on item " + itemName + ": You have asserted that the provided key was to represent a material, however no such material exists!");
            return null;
        }

        CustomItem item = InnovativeItems.getInstance().getItemCache().getItem(rawKey);

        if (material == null && item == null) {
            LogUtil.log(LogUtil.Level.WARNING, "Warning on recipe " + recipeName + " on item " + itemName + ": The object you provided for one of the keys was invalid!");
            return null;
        }

        if (key == null) {
            key = new RecipeChoice.ExactChoice(item != null ? item.getItemStack() : new ItemStack(material));
        }

        float experience = (float) recipeSection.getDouble("experience", 0);
        int time = recipeSection.getInt("cooking-time", 60);
        NamespacedKey namespace = new NamespacedKey(InnovativeItems.getInstance(), "innovativeplugin-customitem-" + itemName + "-recipe." + count);

        CookingRecipe<?> recipe;
        if (type == RecipeType.FURNACE) {
            recipe = new FurnaceRecipe(namespace, underlying, key, experience, time);
        } else if (type == RecipeType.BLAST_FURNACE) {
            recipe = new BlastingRecipe(namespace, underlying, key, experience, time);
        } else if (type == RecipeType.SMOKER) {
            recipe = new SmokingRecipe(namespace, underlying, key, experience, time);
        } else if (type == RecipeType.CAMPFIRE) {
            recipe = new CampfireRecipe(namespace, underlying, key, experience, time);
        } else {
            LogUtil.log(LogUtil.Level.DEV, "Warning on recipe " + recipeName + " on item " + itemName + ": An internal error occured that allowed an internal RecipeType to bypass the precondition at the top of the parseCookingRecipe method in the ItemParser class... Please report this to the developer of the plugin immediately!");
            return null;
        }

        return recipe;
    }

    /**
     * A class that is used to create the custom banner item
     */
    private static final class BannerItemStack {
        /**
         * A method used to generate an itemstack based on this items internal values
         *
         * @param identifier      the internal name of the custom item
         * @param material        the material of the itemstack
         * @param itemName        the display name of the itemstack
         * @param lore            the lore of the itemstack
         * @param enchantments    the enchantments on the itemstack
         * @param flags           the item flags on the itemstack
         * @param attributes      all attributes for this item
         * @param customModelData the custom model data on the itemstack
         * @param durability      durability of the item
         * @param patterns        the patterns to be applied on the banner itemstack
         * @return the itemstack
         */
        public static ItemStack generateItem(@NotNull String identifier, @NotNull Material material, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, int durability, @Nullable List<Pattern> patterns) {
            //if not banner
            if (!BannerItemStack.isBanner(material)) {
                LogUtil.log(LogUtil.Level.DEV, "Error while loading item " + identifier + " because material is not an instance of a banner!");
                throw new IllegalArgumentException("Illegal material provided in CustomItemBanner constructor");
            }

            ItemStack item = CustomItem.generateItem(identifier, material, itemName, lore, enchantments, flags, attributes, customModelData, false, durability);
            BannerMeta meta = (BannerMeta) item.getItemMeta();

            if (patterns != null) {
                meta.setPatterns(patterns);
            }

            item.setItemMeta(meta);
            return item;
        }

        /**
         * Util method to check if a material is a banner
         *
         * @param material the material you want to check
         * @return a boolean that is true if the material provided is a banner
         */
        public static boolean isBanner(Material material) {
            return (material == Material.BLACK_BANNER ||
                    material == Material.BLUE_BANNER ||
                    material == Material.BROWN_BANNER ||
                    material == Material.CYAN_BANNER ||
                    material == Material.GRAY_BANNER ||
                    material == Material.GREEN_BANNER ||
                    material == Material.LIGHT_BLUE_BANNER ||
                    material == Material.LIGHT_GRAY_BANNER ||
                    material == Material.LIME_BANNER ||
                    material == Material.PINK_BANNER ||
                    material == Material.MAGENTA_BANNER ||
                    material == Material.WHITE_BANNER ||
                    material == Material.ORANGE_BANNER ||
                    material == Material.RED_BANNER ||
                    material == Material.YELLOW_BANNER ||
                    material == Material.PURPLE_BANNER);
        }
    }

    /**
     * A class that is used to create the custom firework item
     */
    private static final class FireworkItemStack {
        /**
         * A method used to generate an itemstack based on this items internal values
         *
         * @param identifier      the internal name of the custom item
         * @param itemName        the display name of the itemstack
         * @param lore            the lore of the itemstack
         * @param enchantments    the enchantments on the itemstack
         * @param flags           the item flags on the itemstack
         * @param attributes      all attributes for this item
         * @param customModelData the custom model data on the itemstack
         * @param fireworkEffects the effects of the firework
         * @param power           the amount of time until it is launched until it fires the provided effects
         * @return the itemstack
         */
        public static ItemStack generateItem(@NotNull String identifier, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, @Nullable List<FireworkEffect> fireworkEffects, @Nullable Integer power) {
            ItemStack item = CustomItem.generateItem(identifier, Material.FIREWORK_ROCKET, itemName, lore, enchantments, flags, attributes, customModelData, false, 0);
            FireworkMeta meta = (FireworkMeta) item.getItemMeta();

            if (fireworkEffects != null) {
                meta.addEffects(fireworkEffects);
            }

            if (power != null) {
                meta.setPower(power);
            }

            item.setItemMeta(meta);
            return item;
        }
    }

    /**
     * A class that is used to create the custom leather armor item
     */
    private static final class LeatherArmorItemStack {
        /**
         * A method used to generate an itemstack based on this items internal values
         *
         * @param identifier      the internal name of the custom item
         * @param material        the material of the itemstack
         * @param itemName        the display name of the itemstack
         * @param lore            the lore of the itemstack
         * @param enchantments    the enchantments on the itemstack
         * @param flags           the item flags on the itemstack
         * @param attributes      all attributes for this item
         * @param customModelData the custom model data on the itemstack
         * @param unbreakable     if the custom item is unbreakable
         * @param durability      durability
         * @param rgb             color of the leather armor via rgb
         * @param color           color of the leather armor via color name
         * @return the itemstack
         */
        public static ItemStack generateItem(@NotNull String identifier, @NotNull Material material, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, boolean unbreakable, int durability, @Nullable Color rgb, @Nullable Color color) {
            //if not leather armor
            if (!LeatherArmorItemStack.isLeatherArmor(material)) {
                LogUtil.log(LogUtil.Level.DEV, "Error while loading item " + identifier + " because material is not an instance of leather armor!");
                throw new IllegalArgumentException("Illegal material provided in CustomItemLeatherArmor constructor");
            }

            ItemStack item = CustomItem.generateItem(identifier, material, itemName, lore, enchantments, flags, attributes, customModelData, unbreakable, durability);
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

            if (rgb != null) {
                meta.setColor(rgb);
            }

            if (color != null && rgb == null) {
                meta.setColor(color);
            }

            item.setItemMeta(meta);
            return item;
        }

        /**
         * Util method to check if a material is leather armor
         *
         * @param material the material you want to check
         * @return a boolean that is true if the material provided is leather armor
         */
        public static boolean isLeatherArmor(Material material) {
            return (material == Material.LEATHER_HELMET ||
                    material == Material.LEATHER_CHESTPLATE ||
                    material == Material.LEATHER_LEGGINGS ||
                    material == Material.LEATHER_BOOTS);
        }
    }

    /**
     * A class that is used to create the custom potion item
     */
    private static final class PotionItemStack {
        /**
         * A method used to generate an itemstack based on this items internal values
         *
         * @param identifier      the internal name of the custom item
         * @param material        the material of the itemstack
         * @param itemName        the display name of the itemstack
         * @param lore            the lore of the itemstack
         * @param enchantments    the enchantments on the itemstack
         * @param flags           the item flags on the itemstack
         * @param attributes      all attributes for this item
         * @param customModelData the custom model data on the itemstack
         * @param rgb             color of the potion via rgb
         * @param color           color of the potion via color name
         * @param effects         all the custom potion effects applied on this item
         * @return the itemstack
         */
        public static ItemStack generateItem(@NotNull String identifier, @NotNull Material material, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, @Nullable Color rgb, @Nullable Color color, @Nullable List<PotionEffect> effects) {
            //if not potion
            if (!PotionItemStack.isPotion(material)) {
                LogUtil.log(LogUtil.Level.DEV, "Error while loading item " + identifier + " because material is not an instance of a potion!");
                throw new IllegalArgumentException("Illegal material provided in CustomItemPotion constructor");
            }

            ItemStack item = CustomItem.generateItem(identifier, material, itemName, lore, enchantments, flags, attributes, customModelData, false, 0);
            PotionMeta meta = (PotionMeta) item.getItemMeta();

            if (rgb != null) {
                meta.setColor(rgb);
            }

            if (color != null && rgb == null) {
                meta.setColor(color);
            }

            if (effects != null) {
                for (PotionEffect effect : effects) {
                    meta.addCustomEffect(effect, true);
                }
            }

            item.setItemMeta(meta);
            return item;
        }

        /**
         * Util method to check if a material is a potion
         *
         * @param material the material you want to check
         * @return a boolean that is true if the material provided is a potion
         */
        public static boolean isPotion(Material material) {
            return (material == Material.POTION ||
                    material == Material.LINGERING_POTION ||
                    material == Material.SPLASH_POTION);
        }
    }

    /**
     * A class that is used to create the custom shield item
     */
    private static final class ShieldItemStack {
        /**
         * A method used to generate an itemstack based on this items internal values
         *
         * @param identifier      the internal name of the custom item
         * @param itemName        the display name of the itemstack
         * @param lore            the lore of the itemstack
         * @param enchantments    the enchantments on the itemstack
         * @param flags           the item flags on the itemstack
         * @param attributes      all attributes for this item
         * @param customModelData the custom model data on the itemstack
         * @param durability      the max durability of the item
         * @param patterns        the patterns to be applied on the banner itemstack
         * @param baseColor       the base color of the shield
         * @return the itemstack
         */
        public static ItemStack generateItem(@NotNull String identifier, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, int durability, @Nullable List<Pattern> patterns, @Nullable DyeColor baseColor) {
            ItemStack item = CustomItem.generateItem(identifier, Material.SHIELD, itemName, lore, enchantments, flags, attributes, customModelData, false, durability);
            BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
            Banner banner = (Banner) meta.getBlockState();

            if (baseColor != null) {
                banner.setBaseColor(baseColor);
            }

            if (patterns != null) {
                banner.setPatterns(patterns);
            }

            banner.update();

            meta.setBlockState(banner);
            item.setItemMeta(meta);
            return item;
        }
    }

    /**
     * A class that is used to create the custom skull item
     */
    private static final class SkullItemStack {
        /**
         * A method used to generate an itemstack based on this items internal values
         *
         * @param identifier      the internal name of the custom item
         * @param itemName        the display name of the itemstack
         * @param lore            the lore of the itemstack
         * @param enchantments    the enchantments on the itemstack
         * @param flags           the item flags on the itemstack
         * @param attributes      all attributes for this item
         * @param customModelData the custom model data on the itemstack
         * @param skullName       the name of the player whose skin you wish to place on a player skull (if applicable)
         * @param base64          a base64 encoded string of the skin you wish to place on a player skull (if applicable)
         * @return the itemstack
         */
        public static ItemStack generateItem(@NotNull String identifier, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, @Nullable String skullName, @Nullable String base64) {
            ItemStack item = CustomItem.generateItem(identifier, Material.PLAYER_HEAD, itemName, lore, enchantments, flags, attributes, customModelData, false, 0);
            SkullMeta meta = (SkullMeta) item.getItemMeta();

            if (base64 != null) {
                SkullItemStack.setSkinViaBase64(meta, base64);
            }

            if (skullName != null && base64 == null) {
                meta.setOwner(skullName);
            }

            item.setItemMeta(meta);
            return item;
        }

        /**
         * A method used to set the skin of a player skull via a base64 encoded string
         *
         * @param meta   the skull meta to modify
         * @param base64 the base64 encoded string
         */
        private static void setSkinViaBase64(SkullMeta meta, String base64) {
            try {
                Method setProfile = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                setProfile.setAccessible(true);

                GameProfile profile = new GameProfile(UUID.randomUUID(), "skull-texture");
                profile.getProperties().put("textures", new Property("textures", base64));

                setProfile.invoke(meta, profile);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                LogUtil.log(LogUtil.Level.SEVERE, "There was a severe internal reflection error when attempting to set the skin of a player skull via base64!");
                e.printStackTrace();
            }
        }
    }
}