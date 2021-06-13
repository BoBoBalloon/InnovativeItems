package me.boboballoon.innovativeitems.listeners;

import de.tr7zw.nbtapi.NBTItem;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.AbilityTrigger;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.keywords.keyword.context.ConsumeContext;
import me.boboballoon.innovativeitems.keywords.keyword.context.DamageContext;
import me.boboballoon.innovativeitems.keywords.keyword.context.InteractContext;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.logging.Level;

public class AbilityTriggerListeners implements Listener {
    /**
     * Listener used for left click and right click ability triggers
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();

        if (itemStack == null) {
            return;
        }

        if (itemStack.getType() == Material.AIR) {
            return;
        }

        NBTItem nbtItem = new NBTItem(itemStack);

        if (!nbtItem.hasKey("innovativeplugin-customitem")) {
            return;
        }

        String key = nbtItem.getString("innovativeplugin-customitem-id");

        CustomItem item = InnovativeItems.getInstance().getCache().getItem(key);

        if (item == null) {
            LogUtil.log(Level.WARNING, "There was an error trying to identify the item by the name of " + key + " please report this issue to the developer of this plugin!");
            return;
        }

        Ability ability = item.getAbility();

        if (ability == null) {
            return;
        }

        AbilityTrigger trigger = ability.getTrigger();

        if (trigger != AbilityTrigger.LEFT_CLICK && trigger != AbilityTrigger.RIGHT_CLICK) {
            return;
        }

        InteractContext context = new InteractContext(event.getPlayer(), ability.getName(), event.getClickedBlock(), event.getAction(), event.getHand());

        ability.execute(context);
    }

    /**
     * Listener used for damage dealt ability trigger
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntityDealt(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        Player player = (Player) event.getDamager();
        LivingEntity entity = (LivingEntity) event.getEntity();

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
                return;
            }

            String key = nbtItem.getString("innovativeplugin-customitem-id");

            CustomItem item = InnovativeItems.getInstance().getCache().getItem(key);

            if (item == null) {
                LogUtil.log(Level.WARNING, "There was an error trying to identify the item by the name of " + key + " please report this issue to the developer of this plugin!");
                return;
            }

            Ability ability = item.getAbility();

            if (ability == null) {
                return;
            }

            AbilityTrigger trigger = ability.getTrigger();

            if (trigger != AbilityTrigger.DAMAGE_DEALT) {
                return;
            }

            DamageContext context = new DamageContext(player, ability.getName(), entity, true);

            ability.execute(context);
        }
    }

    /**
     * Listener used for damage taken ability trigger
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntityTaken(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof LivingEntity)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        LivingEntity entity = (LivingEntity) event.getDamager();

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
                return;
            }

            String key = nbtItem.getString("innovativeplugin-customitem-id");

            CustomItem item = InnovativeItems.getInstance().getCache().getItem(key);

            if (item == null) {
                LogUtil.log(Level.WARNING, "There was an error trying to identify the item by the name of " + key + " please report this issue to the developer of this plugin!");
                return;
            }

            Ability ability = item.getAbility();

            if (ability == null) {
                return;
            }

            AbilityTrigger trigger = ability.getTrigger();

            if (trigger != AbilityTrigger.DAMAGE_TAKEN) {
                return;
            }

            DamageContext context = new DamageContext(player, ability.getName(), entity, false);

            ability.execute(context);
        }
    }

    /**
     * Listener used for item eating ability triggers
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        ItemStack itemStack = event.getItem();
        NBTItem nbtItem = new NBTItem(itemStack);

        if (!nbtItem.hasKey("innovativeplugin-customitem")) {
            return;
        }

        String key = nbtItem.getString("innovativeplugin-customitem-id");

        CustomItem item = InnovativeItems.getInstance().getCache().getItem(key);

        if (item == null) {
            LogUtil.log(Level.WARNING, "There was an error trying to identify the item by the name of " + key + " please report this issue to the developer of this plugin!");
            return;
        }

        Ability ability = item.getAbility();

        if (ability == null) {
            return;
        }

        if (ability.getTrigger() != AbilityTrigger.CONSUME_ITEM) {
            return;
        }

        ConsumeContext context = new ConsumeContext(event.getPlayer(), ability.getName(), itemStack);

        ability.execute(context);
    }
}