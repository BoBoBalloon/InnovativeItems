package me.boboballoon.innovativeitems.functions.condition.builtin.dependent;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.boboballoon.innovativeitems.functions.FunctionTargeter;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedPrimitive;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedTargeters;
import me.boboballoon.innovativeitems.functions.condition.Condition;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.BlockContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a condition in an ability config file that checks if the target location is inside a given WorldGuard region
 */
public class IsInRegionCondition extends Condition {
    public IsInRegionCondition() {
        super("isinregion",
                new ExpectedTargeters(FunctionTargeter.PLAYER, FunctionTargeter.ENTITY, FunctionTargeter.BLOCK),
                new ExpectedManual((rawValue, context) -> Bukkit.getWorld(rawValue), "world name"),
                new ExpectedPrimitive(ExpectedPrimitive.PrimitiveType.STRING));
    }

    @Override
    protected Boolean call(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        FunctionTargeter targeter = (FunctionTargeter) arguments.get(0);
        Location location = null;

        if (targeter == FunctionTargeter.PLAYER) {
            location = context.getPlayer().getLocation();
        }

        if (targeter == FunctionTargeter.ENTITY && context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) context;
            location = entityContext.getEntity().getLocation();
        }

        if (targeter == FunctionTargeter.BLOCK && context instanceof BlockContext) {
            BlockContext blockContext = (BlockContext) context;
            location = blockContext.getBlock().getLocation();
        }

        World world = (World) arguments.get(1);
        String regionName = (String) arguments.get(2);

        RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        ProtectedRegion region = manager.getRegion(regionName);

        if (region == null) {
            return false;
        }

        return region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
