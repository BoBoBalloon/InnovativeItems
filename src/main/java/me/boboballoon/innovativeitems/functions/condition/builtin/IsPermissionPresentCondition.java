package me.boboballoon.innovativeitems.functions.condition.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedValues;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.DamageContext;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import org.bukkit.permissions.Permissible;

/**
 * Class that represents a condition in an ability config file that checks if the target has a permission
 */
public class IsPermissionPresentCondition extends Condition {
    public IsPermissionPresentCondition() {
        super("ispermissionpresent",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY),
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.STRING));
    }

    @Override
    protected Boolean call(ImmutableList<Object> arguments, RuntimeContext context) {
        Permissible target = null;
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);

        if (targeter == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (targeter == FunctionTargeter.ENTITY && context instanceof DamageContext) {
            DamageContext damageContext = (DamageContext) context;
            target = damageContext.getEntity();
        }

        String permission = (String) arguments.get(1);

        return target.hasPermission(permission);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
