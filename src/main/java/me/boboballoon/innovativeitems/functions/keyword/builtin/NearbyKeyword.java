package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.context.GenericEntityContext;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.BlockContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a keyword in an ability config file that executes another ability on all nearby entities
 */
public class NearbyKeyword extends Keyword {
    public NearbyKeyword() {
        super("nearby",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY, FunctionTargeter.BLOCK),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.DOUBLE, "range"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);
        Location origin = targeter == FunctionTargeter.ENTITY && context instanceof EntityContext ? ((EntityContext) context).getEntity().getLocation() : targeter == FunctionTargeter.BLOCK && context instanceof BlockContext ? ((BlockContext) context).getBlock().getLocation() : context.getPlayer().getLocation();

        double range = (double) arguments.get(1);

        String rawAbility = (String) arguments.get(2);

        Ability ability = InnovativeItems.getInstance().getItemCache().getAbility(rawAbility);

        if (ability == null) {
            LogUtil.logFunctionError(LogUtil.Level.WARNING, "ability name", this.getIdentifier(), "keyword", context.getAbilityName());
            return;
        }

        if (ability.getIdentifier().equals(context.getAbilityName())) {
            LogUtil.log(LogUtil.Level.WARNING, "You cannot use the " + this.getIdentifier() + " keyword to recursively call the " + context.getAbilityName() + " ability!");
            return;
        }

        if (InnovativeItems.getInstance().getConfigManager().isStrict() && ability.getTrigger().getTargeters().stream().noneMatch(target -> target != FunctionTargeter.PLAYER && target != FunctionTargeter.ENTITY)) {
            LogUtil.log(LogUtil.Level.WARNING, "You cannot use the " + this.getIdentifier() + " keyword to execute an ability without the same targeters as the " + context.getAbilityName() + " ability!");
            return;
        }

        origin.getWorld().getNearbyEntities(origin, range / 2, 256, range / 2, entity -> entity instanceof LivingEntity)
                .stream()
                .map(entity -> (LivingEntity) entity)
                .forEach(entity -> {
                    GenericEntityContext entityContext = new GenericEntityContext(context.getPlayer(), ability, entity);
                    ability.execute(entityContext);
                });
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
