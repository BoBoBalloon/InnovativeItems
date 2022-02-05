package me.boboballoon.innovativeitems.functions.keyword.builtin.dependent;

import com.google.common.collect.ImmutableList;
import io.lumine.xikage.mythicmobs.MythicMobs;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a keyword in an ability config file that executes a mythicmob skill
 */
public class MythicMobSkillKeyword extends Keyword {
    public MythicMobSkillKeyword() {
        super("mythicmobsskill",
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING),
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        String skillName = (String) arguments.get(0);
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(1);
        LivingEntity target = null;

        if (targeter == FunctionTargeter.PLAYER) {
            target = context.getPlayer();
        }

        if (targeter == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;
            target = entityContext.getEntity();
        }

        if (!MythicMobs.inst().getAPIHelper().castSkill(target, skillName, target.getLocation())) {
            LogUtil.logFunctionError(LogUtil.Level.WARNING, "mythicmobs skill name", this.getIdentifier(), "keyword", context.getAbilityName());
        }
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
