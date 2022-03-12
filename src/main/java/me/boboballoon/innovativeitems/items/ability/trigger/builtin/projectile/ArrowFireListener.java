package me.boboballoon.innovativeitems.items.ability.trigger.builtin.projectile;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A small class to managed custom item projectiles
 */
public final class ArrowFireListener implements Listener {
    private static final Map<UUID, CustomItem> PROJECTILES = new HashMap<>();

    /**
     * Event listener to check when a player launches a projectile
     */
    @EventHandler
    private void onEntityShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        CustomItem item = InnovativeItems.getInstance().getItemCache().fromItemStack(event.getBow());

        if (item == null || item.getAbility() == null || !(item.getAbility().getTrigger() instanceof ArrowHitEntityTrigger || item.getAbility().getTrigger() instanceof ArrowHitBlockTrigger)) {
            return;
        }

        PROJECTILES.put(event.getProjectile().getUniqueId(), item);
    }

    /**
     * Event listener to check when a projectile lands
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onProjectileHit(ProjectileHitEvent event) {
        if (contains(event.getEntity())) {
            Bukkit.getScheduler().runTaskLater(InnovativeItems.getInstance(), () -> PROJECTILES.remove(event.getEntity().getUniqueId()), 1);
        }
    }

    /**
     * A method used to check if the provided projectile is in the cache
     *
     * @param projectile the provided projectile
     * @return if the provided projectile is in the cache
     */
    public static boolean contains(@NotNull Projectile projectile) {
        return PROJECTILES.containsKey(projectile.getUniqueId());
    }

    /**
     * A method used to get the custom item that is related to the provided projectile
     *
     * @param projectile the provided projectile
     * @return the custom item that is related to the provided projectile
     */
    @Nullable
    public static CustomItem get(@NotNull Projectile projectile) {
        return PROJECTILES.get(projectile.getUniqueId());
    }
}
