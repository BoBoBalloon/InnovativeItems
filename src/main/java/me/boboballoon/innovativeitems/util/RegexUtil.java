package me.boboballoon.innovativeitems.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * A class used to store util methods regarding regular expressions
 */
public final class RegexUtil {
    /**
     * Constructor to prevent people from using this util class in an object oriented way
     */
    private RegexUtil() {}

    /**
     * A method that returns a regular expression that targets a literal with support to escape it with the "\" character
     *
     * @param literal the literal you wish to target
     * @return a regular expression that targets the provided literal with support to escape it with the "\" character
     */
    @NotNull
    private static String literalWithEscape(@NotNull String literal) {
        return "(?<!\\\\)\\" + literal;
    }

    /**
     * A method that returns an array of strings that were split using the literal with escape and removes the escape characters
     *
     * @param text the text you wish to split
     * @param literal the literal you wish to target
     * @return an array of strings that were split using the provided literal with escape and removes the escape characters
     */
    @NotNull
    public static String[] splitLiteralWithEscape(@NotNull String text, @NotNull String literal) {
        return RegexUtil.splitLiteralWithEscape(text, literal, 0);
    }

    /**
     * A method that returns an array of strings that were split using the literal with escape and removes the escape characters
     *
     * @param text the text you wish to split
     * @param literal the literal you wish to target
     * @param limit the max amount of elements in the split array
     * @return an array of strings that were split using the provided literal with escape and removes the escape characters
     */
    @NotNull
    public static String[] splitLiteralWithEscape(@NotNull String text, @NotNull String literal, int limit) {
        String regex = RegexUtil.literalWithEscape(literal);

        String[] split = text.split(regex, limit);

        return Arrays.stream(split).map(value -> value.replace("\\" + literal, literal)).toArray(String[]::new);
    }

    /**
     * A method that returns a string replacement that escapes
     *
     * @param text the text you wish to split
     * @param replace the string to replace it with
     * @param literal the literal you wish to target
     * @return A method that returns a string replacement that escapes
     */
    @NotNull
    public static String replaceLiteralWithEscape(@NotNull String text, @NotNull String replace, @NotNull String literal) {
        String regex = RegexUtil.literalWithEscape(literal);

        return text.replaceAll(regex, replace);
    }
}
