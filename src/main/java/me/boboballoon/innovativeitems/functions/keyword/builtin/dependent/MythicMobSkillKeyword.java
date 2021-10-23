package me.boboballoon.innovativeitems.functions.keyword.builtin.dependent;

import com.google.common.collect.ImmutableList;
import io.lumine.xikage.mythicmobs.MythicMobs;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedValues;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Class that represents a keyword in an ability config file that executes a mythicmob skill
 */
public class MythicMobSkillKeyword extends Keyword {
    public MythicMobSkillKeyword() {
        super("mythicmobsskill",
                new ExpectedValues(ExpectedValues.ExpectedPrimitives.STRING),
                new ExpectedTargeters(FunctionTargeter.ENTITY));
    }

    @Override
    protected void calling(ImmutableList<Object> arguments, RuntimeContext context) {
        EntityContext entityContext = (EntityContext) context;

        String skillName = (String) arguments.get(0);
        LivingEntity target = entityContext.getEntity();

        if (target instanceof Player) {
            return;
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
