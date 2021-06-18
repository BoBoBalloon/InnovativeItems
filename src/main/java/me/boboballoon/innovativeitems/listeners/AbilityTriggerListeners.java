package me.boboballoon.innovativeitems.listeners;

import de.tr7zw.nbtapi.NBTItem;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.AbilityTrigger;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.keywords.context.ConsumeContext;
import me.boboballoon.innovativeitems.keywords.context.DamageContext;
import me.boboballoon.innovativeitems.keywords.context.InteractContext;
import me.boboballoon.innovativeitems.keywords.context.InteractContextBlock;
import me.boboballoon.innovativeitems.keywords.context.RuntimeContext;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.logging.Level;

public class AbilityTriggerListeners implements Listener {
    /**
     * Listener used for all item click triggers
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(InnovativeItems.getInstance(), () -> {
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

            this.checkLeftClick(event, ability);
            this.checkLeftClickBlock(event, ability);
            this.checkRightClick(event, ability);
            this.checkRightClickBlock(event, ability);
        });
    }

    /**
     * Util method to handle when item fires on left click
     */
    private void checkLeftClick(PlayerInteractEvent event, Ability ability) {
        Action action = event.getAction();

        if (action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (ability.getTrigger() != AbilityTrigger.LEFT_CLICK) {
            return;
        }

        RuntimeContext context = new InteractContext(event.getPlayer(), ability.getName(), action, event.getHand());

        Bukkit.broadcastMessage("left click"); //remove

        ability.execute(context);
    }

    /**
     * Util method to handle when item fires on right click
     */
    private void checkRightClick(PlayerInteractEvent event, Ability ability) {
        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (ability.getTrigger() != AbilityTrigger.RIGHT_CLICK) {
            return;
        }

        RuntimeContext context = new InteractContext(event.getPlayer(), ability.getName(), action, event.getHand());

        Bukkit.broadcastMessage("right click"); //remove

        ability.execute(context);
    }

    /**
     * Util method to handle when item fires on left click block
     */
    private void checkLeftClickBlock(PlayerInteractEvent event, Ability ability) {
        if (!event.hasBlock()) {
            return;
        }

        Action action = event.getAction();

        if (action != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (ability.getTrigger() != AbilityTrigger.LEFT_CLICK_BLOCK) {
            return;
        }

        RuntimeContext context = new InteractContextBlock(event.getPlayer(), ability.getName(), action, event.getHand(), event.getClickedBlock());

        Bukkit.broadcastMessage("left click block"); //remove

        ability.execute(context);
    }

    /**
     * Util method to handle when item fires on right click block
     */
    private void checkRightClickBlock(PlayerInteractEvent event, Ability ability) {
        if (!event.hasBlock()) {
            return;
        }

        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (ability.getTrigger() != AbilityTrigger.RIGHT_CLICK_BLOCK) {
            return;
        }

        RuntimeContext context = new InteractContextBlock(event.getPlayer(), ability.getName(), action, event.getHand(), event.getClickedBlock());

        Bukkit.broadcastMessage("right click block"); //remove

        ability.execute(context);
    }

    /**
     * Listener used for damage dealt ability trigger
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntityDealt(EntityDamageByEntityEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(InnovativeItems.getInstance(), () -> {
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
        });
    }

    /**
     * Listener used for damage taken ability trigger
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntityTaken(EntityDamageByEntityEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(InnovativeItems.getInstance(), () -> {
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
        });
    }

    /**
     * Listener used for item eating ability triggers
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(InnovativeItems.getInstance(), () -> {
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
        });
    }
}