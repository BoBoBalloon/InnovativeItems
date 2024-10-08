package me.boboballoon.innovativeitems.config;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.FunctionContext;
import me.boboballoon.innovativeitems.functions.InnovativeFunction;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedArguments;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedVarArg;
import me.boboballoon.innovativeitems.functions.condition.ActiveCondition;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.keyword.ActiveKeyword;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.AbilityCooldown;
import me.boboballoon.innovativeitems.items.ability.trigger.AbilityTrigger;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.RegexUtil;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
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
     * @return a boolean that is true if the ability was registered
     */
    public static boolean buildAbility(@NotNull ConfigurationSection section, @NotNull InnovativeCache cache) {
        String abilityName = section.getName();

        if (cache.contains(abilityName)) {
            LogUtil.log(LogUtil.Level.WARNING, "Element with the name of " + abilityName + ", is already registered! Skipping ability...");
            return false;
        }

        Ability ability = AbilityParser.parseAbility(section);

        if (ability == null) {
            //error message was already sent from parseAbility method, no need to put in here
            return false;
        }

        cache.registerAbility(ability);
        return true;
    }

    /**
     * A util method used to parse a custom ability from a config section
     *
     * @param section the configuration section of the ability
     * @param name the name of the ability
     * @return the ability (null if an error occurred)
     */
    @Nullable
    public static Ability parseAbility(@NotNull ConfigurationSection section, @NotNull String name) {
        AbilityTrigger<?, ?> trigger = AbilityParser.getAbilityTrigger(section);

        if (trigger == null) {
            //errors are already sent via AbilityParser.getAbilityTrigger() no need to send more
            return null;
        }

        ImmutableList<ActiveKeyword> keywords = AbilityParser.getAbilityKeywords(section, trigger, name);

        if (keywords == null) {
            //errors are already sent via AbilityParser.getAbilityKeywords() no need to send more
            return null;
        }

        ImmutableList<ActiveCondition> conditions = AbilityParser.getAbilityConditions(section, trigger, name);

        int cooldown = section.getInt("cooldown"); //if it does not exist it will return 0
        if (cooldown > 0) { //if the cooldown is > 0 that means it must exist and it also is valid
            //if show-cooldown does not exist it will return false (the proper default) if it is true that means it was set explicitly to true
            return new AbilityCooldown(name, keywords, conditions, trigger, section.getString("trigger"), cooldown, AbilityParser.getCooldownMessage(section, name));
        }

        return new Ability(name, keywords, conditions, trigger, section.getString("trigger"));
    }

    /**
     * A util method used to parse a custom ability from a config section
     *
     * @param section the configuration section of the ability
     * @return the ability (null if an error occurred)
     */
    @Nullable
    public static Ability parseAbility(@NotNull ConfigurationSection section) {
        return AbilityParser.parseAbility(section, section.getName());
    }

    /**
     * A util method used to get the ability trigger from an ability config section
     */
    private static AbilityTrigger<?, ?> getAbilityTrigger(@NotNull ConfigurationSection section) {
        String abilityName = section.getName();

        String triggerName;
        if (section.isString("trigger")) {
            triggerName = section.getString("trigger");
        } else {
            LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing the ability trigger for " + abilityName + ", are you sure the trigger field is present?");
            return null;
        }

        AbilityTrigger<?, ?> trigger = InnovativeItems.getInstance().getFunctionManager().getAbilityTrigger(triggerName);

        if (trigger == null) {
            LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing the ability trigger for " + abilityName + ", are you sure " + triggerName + " is a correct trigger?");
            return null;
        }

        return trigger;
    }

    /**
     * A util method used to get the active keywords from an ability config section
     */
    @NotNull
    private static ImmutableList<ActiveKeyword> getAbilityKeywords(@NotNull ConfigurationSection section, @NotNull AbilityTrigger<?, ?> trigger, @NotNull String abilityName) {
        List<String> raw = AbilityParser.getLines(section, abilityName, true);

        if (raw == null) {
            return ImmutableList.of();
        }

        List<ActiveKeyword> keywords = new ArrayList<>();

        for (int i = 0; i < raw.size(); i++) {
            String line = raw.get(i);

            if (!line.matches("\\w+\\(.*\\)")) { //regex = ^\w+\(.*\)$ (^ and $ are already put in inside of the match method)
                LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing line " + (i + 1) + " on keywords on ability " + abilityName + "! Did you format it correctly?");
                continue;
            }

            String[] split = RegexUtil.splitLiteralWithEscape(line, "(");

            if (split.length != 2) {
                LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing line " + (i + 1) + " on keywords on ability " + abilityName + "! Did you format it correctly?");
                continue;
            }

            Keyword keyword = InnovativeItems.getInstance().getFunctionManager().getKeyword(split[0]);

            ImmutableList<Object> parsedArguments = AbilityParser.checkAndParse(keyword, i, abilityName, split, trigger, true);

            if (parsedArguments == null) {
                //already sent error message in the checkAndParse() method
                continue;
            }

            keywords.add(new ActiveKeyword(keyword, parsedArguments));
        }

        return ImmutableList.copyOf(keywords);
    }

    /**
     * A util method used to get the active keywords from an ability config section
     */
    @NotNull
    private static ImmutableList<ActiveCondition> getAbilityConditions(@NotNull ConfigurationSection section, AbilityTrigger<?, ?> trigger, @NotNull String abilityName) {
        List<String> raw = AbilityParser.getLines(section, abilityName, false);

        if (raw == null) {
            return ImmutableList.of();
        }

        List<ActiveCondition> conditions = new ArrayList<>();

        for (int i = 0; i < raw.size(); i++) {
            String line = raw.get(i);

            boolean inverted;
            if (line.matches("\\w+\\(.*\\)")) { //regex = ^\w+\(.*\)$
                inverted = false;
            } else if (line.matches("!\\w+\\(.*\\)")) { //regex = ^!\w+\(.*\)$
                inverted = true;
            } else {
                LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing line " + (i + 1) + " on conditions on ability " + abilityName + "! Did you format it correctly?");
                continue;
            }

            String[] split = RegexUtil.splitLiteralWithEscape(line, "(");

            if (split.length != 2) {
                LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing line " + (i + 1) + " on conditions on ability " + abilityName + "! Did you format it correctly?");
                continue;
            }

            String conditionName = !inverted ? split[0] : split[0].substring(1);

            Condition condition = InnovativeItems.getInstance().getFunctionManager().getCondition(conditionName);

            ImmutableList<Object> parsedArguments = AbilityParser.checkAndParse(condition, i, abilityName, split, trigger, false);

            if (parsedArguments == null) {
                //already sent error message in the checkAndParse() method
                continue;
            }

            conditions.add(new ActiveCondition(condition, parsedArguments, inverted));
        }

        return ImmutableList.copyOf(conditions);
    }

    /**
     * A utility method used to get the raw string lines of an ability
     */
    @Nullable
    private static List<String> getLines(@NotNull ConfigurationSection section, @NotNull String abilityName, boolean keyword) {
        String type = keyword ? "keyword" : "condition";
        String types = type + "s";

        if (section.isList(types)) {
            return section.getStringList(types);
        }

        if (keyword) {
            LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing the ability keywords for " + abilityName + ", are you sure the keyword field is present?");
        }

        return null;
    }

    /**
     * A utility method used to get the cooldown message
     */
    @Nullable
    private static AbilityCooldown.CooldownMessage getCooldownMessage(@NotNull ConfigurationSection section, @NotNull String abilityName) {
        if (section.isBoolean("show-cooldown")) {
            LogUtil.log(LogUtil.Level.INFO, "While loading " + abilityName + " the usage of the legacy syntax for a cooldown message was detected... It is not recommended to use this syntax and should be removed as soon as possible!");
            return section.getBoolean("show-cooldown") ? new AbilityCooldown.CooldownMessage("&r&cYou have {cooldown} time left until you can use " + abilityName + " again!", ChatMessageType.ACTION_BAR) : null;
        }

        if (!section.contains("show-cooldown")) {
            return null;
        }

        String message = section.getString("show-cooldown");
        ChatMessageType messageType = ChatMessageType.CHAT;

        try {
            if (section.contains("show-cooldown-type")) {
                messageType = ChatMessageType.valueOf(section.getString("show-cooldown-type").toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing the ability cooldown message type for " + abilityName + ", are you sure that you provided a valid message type?");
        }

        return new AbilityCooldown.CooldownMessage(message, messageType);
    }

    /**
     * A utility method used to clean up and centralize the parsing process
     */
    @Nullable
    private static ImmutableList<Object> checkAndParse(@Nullable InnovativeFunction<?> function, int i, @NotNull String abilityName, @NotNull String[] split, @NotNull AbilityTrigger<?, ?> trigger, boolean keyword) {
        String type = keyword ? "keyword" : "condition";
        String types = type + "s";

        if (function == null) {
            LogUtil.log(LogUtil.Level.WARNING, "There was an error parsing line " + (i + 1) + " on " + types + " on ability " + abilityName + "! Did you use a valid " + type + "?");
            return null;
        }

        if (function.getClass().isAnnotationPresent(Deprecated.class)) {
            LogUtil.log(LogUtil.Level.WARNING, "While loading " + abilityName + " the usage of the " + type + " by the name of " + function.getIdentifier() + " was detected... It is not recommended to use this " + type + " and should be removed as soon as possible!");
        }

        boolean hasVararg = function.getArguments().size() != 0 && function.getArguments().get(function.getArguments().size() - 1) instanceof ExpectedVarArg;

        String[] rawArguments = RegexUtil.splitLiteralWithEscape(split[1].substring(0, split[1].length() - 1), ",", hasVararg ? function.getArguments().size() : 0);

        rawArguments = Arrays.stream(rawArguments).map(String::trim).toArray(String[]::new);

        //in the case of no arguments provided
        if (rawArguments.length == 1 && rawArguments[0].equals("")) {
            rawArguments = new String[0];
        }

        if (rawArguments.length != function.getArguments().size() && !hasVararg) {
            LogUtil.log(LogUtil.Level.WARNING, "There are currently an invalid amount of arguments provided on the " + function.getIdentifier() + " " + type + " on line " + (i + 1) + " of the " + abilityName + " ability!");
            return null;
        }

        return AbilityParser.parseArguments(rawArguments, new FunctionContext(function, rawArguments, abilityName, trigger, i + 1));
    }

    /**
     * A util method that parses and initializes the rest of the arguments
     */
    @NotNull
    private static ImmutableList<Object> parseArguments(@NotNull String[] rawArguments, @NotNull FunctionContext context) {
        List<Object> parsedArguments = new ArrayList<>();

        for (int i = 0; i < rawArguments.length; i++) {
            Object parsedValue = null;
            String rawArgument = rawArguments[i];

            ExpectedArguments expectedArgument = context.getFunction().getArguments().get(i);

            try {
                parsedValue = expectedArgument.getValue(rawArgument, context);
            } catch (Exception ignored) {}

            if (parsedValue == null) {
                expectedArgument.getOnError().accept(context);
                return null;
            }

            parsedArguments.add(parsedValue);
        }

        return ImmutableList.copyOf(parsedArguments);
    }
}