package me.boboballoon.innovativeitems.functions.condition;

import me.boboballoon.innovativeitems.functions.ActiveFunction;

import java.util.List;

/**
 * A class that represents a condition after being parsed
 */
public class ActiveCondition extends ActiveFunction<Boolean> {
    private final boolean inverted;

    /**
     * A constructor used to build a condition after being parsed
     *
     * @param base the base condition being used
     * @param arguments the list of arguments with targeters already parsed
     */
    public ActiveCondition(Condition base, List<Object> arguments, boolean inverted) {
        super(base, arguments);
        this.inverted = inverted;
    }

    @Override
    public Condition getBase() {
        return (Condition) super.getBase();
    }

    /**
     * A method that returns a boolean whether this condition is inverted
     *
     * @return a boolean whether this condition is inverted
     */
    public boolean isInverted() {
        return this.inverted;
    }
}
