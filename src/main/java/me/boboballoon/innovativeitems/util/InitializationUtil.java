package me.boboballoon.innovativeitems.util;

import org.apache.commons.lang.IllegalClassException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A class used to store util methods regarding the parsing/casting of variables
 */
public final class InitializationUtil {
    /**
     * Constructor to prevent people from using this util class in an object oriented way
     */
    private InitializationUtil() {}

    /**
     * A util method used to parse a string and convert it into a number
     *
     * @param text the provided text
     * @param clazz the class of the number
     * @return the parsed value of the string as the provided number type
     * @throws IllegalClassException when the provided clazz argument is not a supported number
     * @throws NumberFormatException when the provided text argument cannot be parsed into the provided clazz type
     */
    public static <T extends Number> T initNumber(@NotNull String text, @NotNull Class<T> clazz) throws IllegalClassException, NumberFormatException {
        try {
            Method method = clazz.getMethod("valueOf", String.class);
            return (T) method.invoke(null, text);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalClassException("Was expecting a class that extends number and was a primitive data type wrapper!");
        }
    }

    /**
     * A util method used to parse a string and convert it into a boolean
     *
     * @param text the provided text
     * @return a boolean that represents the provided text
     * @throws IllegalArgumentException when the provided text cannot be safely cast into a boolean
     */
    public static boolean initBoolean(@NotNull String text) throws IllegalArgumentException {
        if (text.equalsIgnoreCase("true")) {
            return true;
        }

        if (text.equalsIgnoreCase("false")) {
            return false;
        }

        throw new IllegalArgumentException("The string provided: " + text + " cannot be cast to a boolean safely!");
    }

    /**
     * A util method used to parse a string and convert it into a char
     *
     * @param text the provided text
     * @return a char that represents the provided text
     * @throws IllegalArgumentException when the provided text cannot be safely cast into a char
     */
    public static char initChar(@NotNull String text) throws IllegalArgumentException {
        if (text.length() != 1) {
            throw new IllegalArgumentException("The string provided: " + text + " cannot be cast to a char safely!");
        }

        return text.charAt(0);
    }
}
