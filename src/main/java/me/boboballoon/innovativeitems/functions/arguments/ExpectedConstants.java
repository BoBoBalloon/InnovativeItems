package me.boboballoon.innovativeitems.functions.arguments;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.util.TextUtil;

/**
 * A util class that contains some commonly used arguments and util methods to easily build more complex ones
 */
public class ExpectedConstants {
    /**
     * Private constructor to prevent the class from being initialized
     */
    private ExpectedConstants() {}

    public static final ExpectedArguments COLOR_CODED_STRING = (rawValue, context) -> TextUtil.format(rawValue);

    public static final ExpectedArguments ABILITY = new ExpectedManual((rawValue, context) -> InnovativeItems.getInstance().getItemCache().getAbility(rawValue), "ability");

    public static final ExpectedArguments CUSTOM_ITEM = new ExpectedManual((rawValue, context) -> InnovativeItems.getInstance().getItemCache().getItem(rawValue), "custom item");
}
