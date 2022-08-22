package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a condition in an ability config file that checks if the entity is far enough away from the point 0,0,0
 */
public class IsCoordinateCondition extends Condition {
    public IsCoordinateCondition() {
        super("iscoordinate",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.CHAR, "coordinate type", object -> {
                    char character = (char) object;
                    return character == 'x' || character == 'y' || character == 'z';
                }),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.CHAR, "coordinate condition", object -> {
                    char character = (char) object;
                    return character == '>' || character == '<' || character == '=';
                }),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.DOUBLE, "coordinate value"));
    }

    @Override
    protected Boolean call(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);
        char type = (char) arguments.get(1);
        char condition = (char) arguments.get(2);
        double value = (double) arguments.get(3);

        LivingEntity entity = targeter == FunctionTargeter.PLAYER ? context.getPlayer() : ((EntityContext) context).getEntity();
        Location location = entity.getLocation();

        double coordinate = type == 'x' ? location.getX() : type == 'y' ? location.getY() : location.getZ();

        return (condition == '>' && coordinate > value) || (condition == '<' && coordinate < value) || (condition == '=' && coordinate == value);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
