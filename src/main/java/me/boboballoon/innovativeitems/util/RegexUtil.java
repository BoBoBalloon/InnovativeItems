package me.boboballoon.innovativeitems.util;

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
    public static String literalWithEscape(char literal) {
        return "(?<!\\\\)\\" + literal;
    }

    /**
     * A method that returns an array of strings that were split using the literal with escape and removes the escape characters
     *
     * @param text the text you wish to split
     * @param literal the literal you wish to target
     * @return an array of strings that were split using the provided literal with escape and removes the escape characters
     */
    public static String[] splitLiteralWithEscape(String text, char literal) {
        String regex = RegexUtil.literalWithEscape(literal);

        String[] split = text.split(regex);

        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].replace("\\" + literal, "" + literal);
        }

        return split;
    }
}
