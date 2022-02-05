package me.boboballoon.innovativeitems.functions.keyword.builtin;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.functions.arguments.ExpectedManual;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.functions.keyword.Keyword;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.NotNull;

/**
 * Class that represents a keyword in an ability config file that shoots a projectile at a target
 */
public class ShootProjectileKeyword extends Keyword {
    public ShootProjectileKeyword() {
        super("shootprojectile",
                new ExpectedManual((rawValue, context) -> {
                    Class<? extends Entity> entity = EntityType.valueOf(rawValue.toUpperCase()).getEntityClass();
                    return Projectile.class.isAssignableFrom(entity) ? entity : null;
                }, "projectile type"));
    }

    @Override
    protected void calling(@NotNull ImmutableList<Object> arguments, @NotNull RuntimeContext context) {
        Class<? extends Projectile> projectile = (Class<? extends Projectile>) arguments.get(0);

        Player player = context.getPlayer();
        Projectile entity = player.launchProjectile(projectile);

        Location location = entity.getLocation();
        Location playerLocation = player.getLocation();

        location.setPitch(playerLocation.getPitch());
        location.setYaw(playerLocation.getYaw());
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
