package me.boboballoon.innovativeitems.config;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.AbilityTimerManager;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.AbilityTrigger;
import me.boboballoon.innovativeitems.keywords.keyword.ActiveKeyword;
import me.boboballoon.innovativeitems.keywords.keyword.Keyword;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordContext;
import me.boboballoon.innovativeitems.keywords.keyword.KeywordTargeter;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
     * A util method used to parse a custom ability from a config section
     *
     * @param section the config section
     * @param name the name of the ability
     * @return the ability (null if an error occurred)
     */
    @Nullable
    public static Ability parseAbility(ConfigurationSection section, String name) {
        String triggerName;
        if (section.contains("trigger")) {
            triggerName = section.getString("trigger");
        } else {
            LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing the ability trigger for " + name + ", are you sure the trigger field is present?");
            return null;
        }

        AbilityTrigger trigger = AbilityTrigger.getFromIdentifier(triggerName);

        if (trigger == null) {
            LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing the ability trigger for " + name + ", are you sure " + triggerName + " is a correct trigger?");
            return null;
        }

        List<String> raw;
        if (section.contains("keywords")) {
            raw = section.getStringList("keywords");
        } else {
            LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing the ability keywords for " + name + ", are you sure the keyword field is present?");
            return null;
        }

        List<ActiveKeyword> keywords = new ArrayList<>();

        for (int i = 0; i < raw.size(); i++) {
            String line = raw.get(i);

            if (!line.matches("\\w+\\(.*\\)")) { //regex = ^\w+\(.*\)$ (^ and $ are already put in inside of the match method)
                LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing line " + (i + 1) + " on ability " + name + "! Did you format it correctly?");
                continue;
            }

            String[] split = line.split("\\("); //regex = \(

            Keyword keyword = InnovativeItems.getInstance().getKeywordManager().getKeyword(split[0]);

            if (keyword == null) {
                LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing line " + (i + 1) + " on ability " + name + "! Did you use a valid keyword?");
                continue;
            }

            String[] rawArguments = split[1].substring(0, split[1].length() - 1).split(",");

            AbilityParser.trimArray(rawArguments);

            //in the case of no arguments provided
            if (rawArguments.length == 1 && rawArguments[0].equals("")) {
                rawArguments = new String[]{};
            }

            ImmutableList<Boolean> arguments = keyword.getArguments();

            if (rawArguments.length != arguments.size()) {
                LogUtil.log(LogUtil.Level.WARNING, "There are currently invalid arguments provided on the " + keyword.getIdentifier() + " keyword on line " + (i + 1) + " of the " + name + " ability!");
                continue;
            }

            if (!AbilityParser.hasValidTargeters(rawArguments, arguments, keyword.getValidTargeters(), keyword.getIdentifier(), trigger, i + 1, name)) {
                continue;
            }

            KeywordContext context = new KeywordContext(rawArguments, name, trigger);

            keywords.add(new ActiveKeyword(keyword, context));
        }

        return new Ability(name, keywords, trigger);
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
        if (section.contains("trigger")) {
            triggerName = section.getString("trigger");
        } else {
            LogUtil.log(LogUtil.Level.SEVERE, "(Dev warning) There was an error parsing the ability trigger for the provided config section, are you sure the provided section matches the ability?");
            throw new IllegalArgumentException("The provided config section cannot reasonably match the provided ability due to lack of trigger argument!");
        }

        if (!triggerName.matches(AbilityTrigger.TIMER.getRegex())) {
            LogUtil.log(LogUtil.Level.SEVERE, "(Dev warning) The ability trigger provided for " + ability.getName() + " was timer but the trigger name in the config section does not match the required syntax!");
            throw new IllegalArgumentException("The provided config section cannot reasonably match the provided ability due to the trigger argument not meeting the syntax requirements!");
        }

        long timer;
        try {
            timer = Long.parseLong(triggerName.split(":")[1]);
        } catch (NumberFormatException ignored) {
            LogUtil.log(LogUtil.Level.SEVERE, "(Dev warning) There was an error trying to parse the trigger delay for the " + ability.getName() + " ability!");
            throw new IllegalArgumentException("The provided config section cannot reasonably match the provided ability due to the delay not matching the long data type!");
        }

        AbilityTimerManager manager = InnovativeItems.getInstance().getAbilityTimerManager();

        manager.registerTimer(ability, timer);
    }

    /**
     * A util method used to trim all elements in an array
     */
    private static void trimArray(String[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i].trim();
        }
    }

    /**
     * A util method that checks the positions of targeters inside a keyword
     */
    private static boolean hasValidTargeters(String[] args, ImmutableList<Boolean> arguments, ImmutableList<String> allowedTargeters, String keywordName, AbilityTrigger trigger, int line, String abilityName) {
        for (int i = 0; i < args.length; i++) {
            if (!arguments.get(i)) {
                continue;
            }

            String argument = args[i];

            if (!argument.startsWith("?")) {
                LogUtil.log(LogUtil.Level.WARNING, "Argument number " + (i + 1) + " on line " + line + " on ability " + abilityName + " was expected a targeter but did not receive one!");
                return false;
            }

            if (KeywordTargeter.getFromIdentifier(argument) == null) {
                LogUtil.log(LogUtil.Level.WARNING, "Argument number " + (i + 1) + " on line " + line + " on ability " + abilityName + " is an invalid targeter because it does not exist!");
                return false;
            }

            if (!trigger.getAllowedTargeters().contains(argument)) {
                LogUtil.log(LogUtil.Level.WARNING, "Argument number " + (i + 1) + " on line " + line + " on ability " + abilityName + " is an invalid targeter for the trigger of " + trigger.getIdentifier() + "!");
                return false;
            }

            if (!allowedTargeters.contains(argument)) {
                LogUtil.log(LogUtil.Level.WARNING, "Argument number " + (i + 1) + " on line " + line + " on ability " + abilityName + " is an invalid targeter for the keyword of " + keywordName + "!");
                return false;
            }
        }

        return true;
    }
}
