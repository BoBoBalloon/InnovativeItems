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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class AbilityTriggerListeners implements Listener {
    /**
     * Listener used for left click and right click ability triggers
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();

        if (itemStack == null) {
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
     * Listener used for damage taken and damage dealt ability triggers
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) && !(event.getDamager() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof LivingEntity)) {
            return;
        }

        Player player;
        LivingEntity entity;
        boolean wasPlayerDamager;

        if (event.getEntity() instanceof Player) {
            player = (Player) event.getEntity();
            entity = (LivingEntity) event.getDamager();
            wasPlayerDamager = false;
        } else if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
            entity = (LivingEntity) event.getEntity();
            wasPlayerDamager = true;
        } else {
            return;
        }

        PlayerInventory inventory = player.getInventory();

        List<ItemStack> items = Arrays.asList(inventory.getArmorContents());
        items.add(inventory.getItemInMainHand());
        items.add(inventory.getItemInOffHand());

        for (ItemStack itemstack : items) {
            if (itemstack == null) {
                continue;
            }

            NBTItem nbtItem = new NBTItem(itemstack);

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

            if (trigger != AbilityTrigger.DAMAGE_DEALT && trigger != AbilityTrigger.DAMAGE_TAKEN) {
                return;
            }

            DamageContext context = new DamageContext(player, ability.getName(), entity, wasPlayerDamager);

            ability.execute(context);
        }
    }

    /**
     * Listener used for item eating ability triggers
     */
    @EventHandler
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
