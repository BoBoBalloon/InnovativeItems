package me.boboballoon.innovativeitems.config;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.AbilityTimerManager;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.AbilityTrigger;
import me.boboballoon.innovativeitems.keywords.keyword.ActiveKeyword;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordContext;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordTargeter;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedArguments;
import me.boboballoon.innovativeitems.keywords.keyword.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.RegexUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class built for parsing configuration sections and convert into Ability objects
 */
public final class AbilityParser {
    /**
     * Constructor to prevent people from using this util class in an object oriented way
     */
    private AbilityParser() {}

    /**
     * A util method used to not only parse an ability but to register it in both the cache and ability timer registry if necessary
     *
     * @param section the configuration section of the ability
     * @param cache the cache to register the ability in
     */
    public static void buildAbility(ConfigurationSection section, InnovativeCache cache) {
        String abilityName = section.getName();

        if (cache.contains(abilityName)) {
            LogUtil.log(LogUtil.Level.WARNING, "Element with the name of " + abilityName + ", is already registered! Skipping ability...");
            return;
        }

        Ability ability = AbilityParser.parseAbility(section);

        if (ability == null) {
            //error message was already sent from parseAbility method, no need to put in here
            return;
        }

        cache.registerAbility(ability);

        //if it is not present in the cache something went wrong, do not register an ability timer
        if (!cache.contains(abilityName)) {
            return;
        }

        AbilityParser.registerAbilityTimer(ability, section);
    }

    /**
     * A util method used to parse a custom ability from a config section
     *
     * @param section the configuration section of the ability
     * @return the ability (null if an error occurred)
     */
    @Nullable
    public static Ability parseAbility(ConfigurationSection section) {
        AbilityTrigger trigger = AbilityParser.getAbilityTrigger(section);

        if (trigger == null) {
            //errors are already sent via AbilityParser.getAbilityTrigger() no need to send more
            return null;
        }

        List<ActiveKeyword> keywords = AbilityParser.getAbilityKeywords(section, trigger);

        if (keywords == null) {
            //errors are already sent via AbilityParser.getAbilityKeywords() no need to send more
            return null;
        }

        return new Ability(section.getName(), keywords, trigger);
    }

    /**
     * A util method to check if the ability requires a timer and if so to register one
     *
     * @param ability the ability to check
     * @param section the ability's config section
     * @throws IllegalArgumentException when the section argument cannot possibility match the provided ability
     */
    public static void registerAbilityTimer(Ability ability, ConfigurationSection section) {
        if (ability.getTrigger() != AbilityTrigger.TIMER) {
            return;
        }

        String triggerName;
        if (section.isString("trigger")) {
            triggerName = section.getString("trigger");
        } else {
            LogUtil.log(LogUtil.Level.DEV, "There was an error parsing the ability trigger for the provided config section, are you sure the provided section matches the ability?");
            throw new IllegalArgumentException("The provided config section cannot reasonably match the provided ability due to lack of trigger argument!");
        }

        if (!triggerName.matches(AbilityTrigger.TIMER.getRegex())) {
            LogUtil.log(LogUtil.Level.DEV, "The ability trigger provided for " + ability.getName() + " was timer but the trigger name in the config section does not match the required syntax!");
            throw new IllegalArgumentException("The provided config section cannot reasonably match the provided ability due to the trigger argument not meeting the syntax requirements!");
        }

        long timer;
        try {
            timer = Long.parseLong(triggerName.split(":")[1]);
        } catch (NumberFormatException ignored) {
            LogUtil.log(LogUtil.Level.DEV, "There was an error trying to parse the trigger delay for the " + ability.getName() + " ability!");
            throw new IllegalArgumentException("The provided config section cannot reasonably match the provided ability due to the delay not matching the long data type!");
        }

        AbilityTimerManager manager = InnovativeItems.getInstance().getAbilityTimerManager();

        manager.registerTimer(ability, timer);
    }

    /**
     * A util method used to get the ability trigger from an ability config section
     */
    private static AbilityTrigger getAbilityTrigger(ConfigurationSection section) {
        String abilityName = section.getName();

        String triggerName;
        if (section.isString("trigger")) {
            triggerName = section.getString("trigger");
        } else {
            LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing the ability trigger for " + abilityName + ", are you sure the trigger field is present?");
            return null;
        }

        AbilityTrigger trigger = AbilityTrigger.getFromIdentifier(triggerName);

        if (trigger == null) {
            LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing the ability trigger for " + abilityName + ", are you sure " + triggerName + " is a correct trigger?");
            return null;
        }

        return trigger;
    }

    /**
     * A util method used to get the active keywords from an ability config section
     */
    private static List<ActiveKeyword> getAbilityKeywords(ConfigurationSection section, AbilityTrigger trigger) {
        String abilityName = section.getName();

        List<String> raw;
        if (section.isList("keywords")) {
            raw = section.getStringList("keywords");
        } else {
            LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing the ability keywords for " + abilityName + ", are you sure the keyword field is present?");
            return null;
        }

        List<ActiveKeyword> keywords = new ArrayList<>();

        for (int i = 0; i < raw.size(); i++) {
            String line = raw.get(i);

            if (!line.matches("\\w+\\(.*\\)")) { //regex = ^\w+\(.*\)$ (^ and $ are already put in inside of the match method)
                LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing line " + (i + 1) + " on ability " + abilityName + "! Did you format it correctly?");
                continue;
            }

            String[] split = line.split("\\("); //regex = \(

            Keyword keyword = InnovativeItems.getInstance().getKeywordManager().getKeyword(split[0]);

            if (keyword == null) {
                LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing line " + (i + 1) + " on ability " + abilityName + "! Did you use a valid keyword?");
                continue;
            }

            String[] rawArguments = RegexUtil.splitLiteralWithEscape(split[1].substring(0, split[1].length() - 1), ',');

            rawArguments = Arrays.stream(rawArguments).map(String::trim).toArray(String[]::new);

            //in the case of no arguments provided
            if (rawArguments.length == 1 && rawArguments[0].equals("")) {
                rawArguments = new String[]{};
            }

            int expectedSize = keyword.getArguments().size();

            if (rawArguments.length != expectedSize) {
                LogUtil.log(LogUtil.Level.WARNING, "There are currently an invalid amount of arguments provided on the " + keyword.getIdentifier() + " keyword on line " + (i + 1) + " of the " + abilityName + " ability!");
                continue;
            }

            List<Object> parsedArguments = new ArrayList<>(expectedSize);

            KeywordContext context = new KeywordContext(keyword, rawArguments, abilityName, trigger, (i + 1));

            if (!AbilityParser.parseTargeters(rawArguments, context, parsedArguments)) {
                continue;
            }

            if (!AbilityParser.parseArguments(parsedArguments, rawArguments, context)) {
                continue;
            }

            keywords.add(new ActiveKeyword(keyword, parsedArguments));
        }

        return keywords;
    }

    /**
     * A util method that checks the positions and parses the targeters inside a keyword
     */
    private static boolean parseTargeters(String[] rawArguments, KeywordContext context, List<Object> parsedArguments) {
        for (int i = 0; i < rawArguments.length; i++) {
            ExpectedArguments expectedValue = context.getKeyword().getArguments().get(i);

            if (!(expectedValue instanceof ExpectedTargeters)) {
                parsedArguments.add(null);
                continue;
            }

            String argument = rawArguments[i];

            if (!argument.startsWith("?")) {
                LogUtil.log(LogUtil.Level.WARNING, "Argument number " + (i + 1) + " on line " + context.getLineNumber() + " on ability " + context.getAbilityName() + " was expected a targeter but did not receive one!");
                return false;
            }

            KeywordTargeter targeter = KeywordTargeter.getFromIdentifier(argument);

            if (targeter == null) {
                LogUtil.log(LogUtil.Level.WARNING, "Argument number " + (i + 1) + " on line " + context.getLineNumber() + " on ability " + context.getAbilityName() + " is an invalid targeter because it does not exist!");
                return false;
            }

            if (!context.getAbilityTrigger().getAllowedTargeters().contains(argument)) {
                LogUtil.log(LogUtil.Level.WARNING, "Argument number " + (i + 1) + " on line " + context.getLineNumber() + " on ability " + context.getAbilityName() + " is an invalid targeter for the trigger of " + context.getAbilityTrigger().getIdentifier() + "!");
                return false;
            }

            ExpectedTargeters expectedTargeters = (ExpectedTargeters) expectedValue;

            if (!expectedTargeters.contains(targeter)) {
                LogUtil.log(LogUtil.Level.WARNING, "Argument number " + (i + 1) + " on line " + context.getLineNumber() + " on ability " + context.getAbilityName() + " is an invalid targeter for the keyword of " + context.getKeyword().getIdentifier() + "!");
                return false;
            }

            parsedArguments.add(targeter);
        }

        return true;
    }

    /**
     * A util method that parses and initializes the rest of the arguments
     */
    private static boolean parseArguments(List<Object> parsedArguments, String[] rawArguments, KeywordContext context) {
        for (int i = 0; i < parsedArguments.size(); i++) {
            Object argument = parsedArguments.get(i);

            if (argument != null) {
                continue;
            }

            ExpectedArguments expectedArgument = context.getKeyword().getArguments().get(i);

            if (!expectedArgument.shouldGetValue()) {
                LogUtil.log(LogUtil.Level.DEV, "There is an expected argument in the " + context.getKeyword().getIdentifier() + " keyword in which the getValue() method was attempted to be called on, but the shouldGetValue() method returned false!");
                return false;
            }

            Object parsedValue = null;

            String rawArgument = rawArguments[i];

            try {
                parsedValue = expectedArgument.getValue(rawArgument, context);
            } catch (Exception ignored) {}

            if (parsedValue == null && expectedArgument.getOnError() == null) {
                LogUtil.log(LogUtil.Level.WARNING, "Argument number " + (i + 1) + " on keyword " + context.getKeyword().getIdentifier() + " on ability " + context.getAbilityName() + " was unable to be parsed... Are you sure you provided the correct data type?");
                return false;
            }

            if (parsedValue == null) {
                return false;
            }

            parsedArguments.set(i, parsedValue);
        }

        return true;
    }
}
