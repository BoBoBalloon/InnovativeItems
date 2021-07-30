package me.boboballoon.innovativeitems.items.ability;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.tr7zw.nbtapi.NBTItem;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.functions.context.RuntimeContext;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

/**
 * A class that represents a timer that will fire a set of abilities on a specified delay
 */
public class AbilityTimerTrigger extends BukkitRunnable {
    private final Set<Ability> abilities;

    /**
     * A constructor that builds and runs the timer runnable
     *
     * @param timer the delay in between each trigger
     * @param abilities the abilities that must be fired
     */
    public AbilityTimerTrigger(long timer, Set<Ability> abilities) {
        this.abilities = abilities;
        this.runTaskTimerAsynchronously(InnovativeItems.getInstance(), 0L, timer);
    }

    /**
     * A constructor that builds and runs the timer runnable
     *
     * @param timer the delay in between each trigger
     * @param abilities the abilities that must be fired
     */
    public AbilityTimerTrigger(long timer, Ability... abilities) {
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
                if (itemStack == null) {
                    continue;
                }

                if (itemStack.getType() == Material.AIR) {
                    continue;
                }

                NBTItem nbtItem = new NBTItem(itemStack);

                if (!nbtItem.hasKey("innovativeplugin-customitem")) {
                    continue;
                }

                String key = nbtItem.getString("innovativeplugin-customitem-id");

                CustomItem item = InnovativeItems.getInstance().getItemCache().getItem(key);

                if (item == null) {
                    LogUtil.log(LogUtil.Level.WARNING, "There was an error trying to identify the item by the name of " + key + " please report this issue to the developer of this plugin!");
                    continue;
                }

                Ability ability = item.getAbility();

                if (ability == null) {
                    continue;
                }

                if (!this.abilities.contains(ability)) {
                    continue;
                }

                AbilityTrigger trigger = ability.getTrigger();

                if (trigger != AbilityTrigger.TIMER) {
                    continue;
                }

                RuntimeContext context = new RuntimeContext(player, ability.getName(), ability.getTrigger());

                ability.execute(context);
            }
        }
    }

    /**
     * A method that returns the abilities that must be fired on a timer
     *
     * @return the ability that must be fired on a timer
     */
    public ImmutableSet<Ability> getAbilities() {
        return ImmutableSet.copyOf(this.abilities);
    }

    /**
     * A method that adds an ability to the abilities that must be fired
     *
     * @param ability the ability you wish to add
     */
    public void addAbility(Ability ability) {
        this.abilities.add(ability);
    }
}
