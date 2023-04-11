package me.boboballoon.innovativeitems.items.ability.trigger.builtin.timer;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * A class that represents a timer that will fire a set of abilities on a specified delay
 */
public class AbilityTimerClock extends BukkitRunnable {
    private final Set<Ability> abilities;

    /**
     * A constructor that builds and runs the timer runnable
     *
     * @param timer the delay in between each trigger
     * @param abilities the abilities that must be fired
     */
    public AbilityTimerClock(long timer, @NotNull Set<Ability> abilities) {
        this.abilities = abilities;
        this.runTaskTimerAsynchronously(InnovativeItems.getInstance(), 0L, timer);
    }

    /**
     * A constructor that builds and runs the timer runnable
     *
     * @param timer the delay in between each trigger
     * @param abilities the abilities that must be fired
     */
    public AbilityTimerClock(long timer, @NotNull Ability... abilities) {
        this(timer, Sets.newHashSet(abilities));
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerInventory inventory = player.getInventory();

            ItemStack[] items = new ItemStack[6];
            for (int i = 0; i < 4; i++) {
                items[i] = inventory.getArmorContents()[i];
            }
            items[4] = inventory.getItemInMainHand();
            items[5] = inventory.getItemInOffHand();

            for (ItemStack itemStack : items) {
                CustomItem item = InnovativeItems.getInstance().getItemCache().fromItemStack(itemStack);

                if (item == null) {
                    continue;
                }

                Ability ability = item.getAbility();

                if (ability == null || !this.abilities.contains(ability)) {
                    continue;
                }

                if (ability.getTrigger() instanceof TimerTrigger) {
                    ability.execute(player);
                }
            }
        }
    }

    /**
     * A method that returns the abilities that must be fired on a timer
     *
     * @return the ability that must be fired on a timer
     */
    @NotNull
    public ImmutableSet<Ability> getAbilities() {
        return ImmutableSet.copyOf(this.abilities);
    }

    /**
     * A method that adds an ability to the abilities that must be fired
     *
     * @param ability the ability you wish to add
     */
    public void addAbility(@NotNull Ability ability) {
        if (this.abilities.contains(ability)) {
            return;
        }

        this.abilities.add(ability);
    }
}
