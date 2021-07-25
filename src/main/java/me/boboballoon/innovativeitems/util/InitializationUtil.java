package me.boboballoon.innovativeitems.util;

import org.jetbrains.annotations.NotNull;

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
     * @throws UnsupportedClassProvidedException when the provided clazz argument is not a supported number
     * @throws NumberFormatException when the provided text argument cannot be parsed into the provided clazz type
     */
    public static Number initNumber(@NotNull String text, @NotNull Class<? extends Number> clazz) throws UnsupportedClassProvidedException, NumberFormatException {
        Number number = null;

        //integers

        if (clazz == byte.class || clazz == Byte.class) {
            number = Byte.parseByte(text);
        }

        if (clazz == short.class || clazz == Short.class) {
            number = Short.parseShort(text);
        }

        if (clazz == int.class || clazz == Integer.class) {
            number = Integer.parseInt(text);
        }

        if (clazz == long.class || clazz == Long.class) {
            number = Long.parseLong(text);
        }

        //floating points

        if (clazz == float.class || clazz == Float.class) {
            number = Float.parseFloat(text);
        }

        if (clazz == double.class || clazz == Double.class) {
            number = Double.parseDouble(text);
        }

        //return or throw errors

        if (number == null) {
            throw new UnsupportedClassProvidedException(clazz, "InitializationUtil.initNumber()");
        }

        return number;
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

    /**
     * Inner util class used to tell the developer when a provided class is not supported
     */
    private static class UnsupportedClassProvidedException extends IllegalArgumentException {
        public UnsupportedClassProvidedException(Class<?> providedClass, String methodName) {
            super("The class " + providedClass.getName() + " is not supported in the " + methodName + " method!");
        }
    }
}
