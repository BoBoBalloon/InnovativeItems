package me.boboballoon.innovativeitems.listeners;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.context.*;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.ability.AbilityTrigger;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * A class that contains all listeners for ability triggers
 */
public class AbilityTriggerListeners implements Listener {
    /**
     * An inner class used to eliminate repetitive code
     */
    @FunctionalInterface
    private interface AbilityExecutor<T extends Event> {
        /**
         * An abstract method used to build a runtime context
         *
         * @param event the provided event
         * @param ability the ability being executed
         * @param customItem the custom item with the ability
         * @return a runtime context that represents this event
         */
        RuntimeContext buildContext(T event, Ability ability, CustomItem customItem);

        /**
         * A method used to handle ability trigger logic for all main equipment slots
         *
         * @param player          the targeted player
         * @param expectedTrigger the trigger to fire each ability
         * @param event           the event that is executing the ability
         * @param executee        the runtime context fed to each ability
         * @param <T>             the class of the event that is executing the ability
         */
        static <T extends Event> void handle(Player player, AbilityTrigger expectedTrigger, T event, AbilityExecutor<T> executee) {
            ItemStack[] items = new ItemStack[6];
            PlayerInventory inventory = player.getInventory();
            for (int i = 0; i < 4; i++) {
                items[i] = inventory.getArmorContents()[i];
            }
            items[4] = inventory.getItemInMainHand();
            items[5] = inventory.getItemInOffHand();

            for (ItemStack itemstack : items) {
                AbilityExecutor.handleItem(itemstack, expectedTrigger, event, executee);
            }
        }

        /**
         * @param itemstack       the itemstack to check
         * @param expectedTrigger the trigger to fire each ability
         * @param event           the event that is executing the ability
         * @param executee        the runtime context fed to each ability
         * @param <T>             the class of the event that is executing the ability
         */
        static <T extends Event> void handleItem(ItemStack itemstack, AbilityTrigger expectedTrigger, T event, AbilityExecutor<T> executee) {
            CustomItem item = InnovativeItems.getInstance().getItemCache().fromItemStack(itemstack);

            if (item == null) {
                return;
            }

            Ability ability = item.getAbility();

            if (ability == null || ability.getTrigger() != expectedTrigger) {
                return;
            }

            RuntimeContext context = executee.buildContext(event, ability, item);

            ability.execute(context);
        }
    }

    /**
     * Listener used for damage dealt ability trigger
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntityDealt(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(InnovativeItems.getInstance(), () -> {
            if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof LivingEntity)) {
                return;
            }

            Player player = (Player) event.getDamager();
            LivingEntity entity = (LivingEntity) event.getEntity();

            AbilityExecutor.handle(player, AbilityTrigger.DAMAGE_DEALT, event, (e, ability, customItem) -> new DamageContext(player, ability, entity, true));
        });
    }

    /**
     * Listener used for damage taken ability trigger
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntityTaken(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(InnovativeItems.getInstance(), () -> {
            if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof LivingEntity)) {
                return;
            }

            Player player = (Player) event.getEntity();
            LivingEntity entity = (LivingEntity) event.getDamager();

            AbilityExecutor.handle(player, AbilityTrigger.DAMAGE_TAKEN, event, (e, ability, customItem) -> new DamageContext(player, ability, entity, false));
        });
    }

    /**
     * Listener used for item eating ability triggers
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(InnovativeItems.getInstance(), () -> AbilityExecutor.handleItem(event.getItem(), AbilityTrigger.CONSUME_ITEM, event, (e, ability, customItem) -> new ConsumeContext(event.getPlayer(), ability, customItem)));
    }

    /**
     * Listener used for item block break ability triggers
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(InnovativeItems.getInstance(), () -> AbilityExecutor.handle(event.getPlayer(), AbilityTrigger.BLOCK_BREAK, event, (e, ability, customItem) -> {
            Player player = event.getPlayer();
            return new BlockBreakContext(player, ability, event.getBlock(), customItem);
        }));
    }

    /**
     * Listener used for player crouching ability triggers
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (event.isCancelled() || !event.isSneaking()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(InnovativeItems.getInstance(), () -> AbilityExecutor.handle(event.getPlayer(), AbilityTrigger.CROUCH, event, (e, ability, customItem) -> new RuntimeContext(e.getPlayer(), ability)));
    }

    /**
     * Listener used for all item click triggers
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(InnovativeItems.getInstance(), () -> {
            CustomItem item = InnovativeItems.getInstance().getItemCache().fromItemStack(event.getItem());

            if (item == null) {
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

        if ((action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) || ability.getTrigger() != AbilityTrigger.LEFT_CLICK) {
            return;
        }

        InteractContext context = new InteractContext(event.getPlayer(), ability, action, event.getHand());

        ability.execute(context);
    }

    /**
     * Util method to handle when item fires on right click
     */
    private void checkRightClick(PlayerInteractEvent event, Ability ability) {
        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK || ability.getTrigger() != AbilityTrigger.RIGHT_CLICK) {
            return;
        }

        InteractContext context = new InteractContext(event.getPlayer(), ability, action, event.getHand());

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

        if (action != Action.LEFT_CLICK_BLOCK || ability.getTrigger() != AbilityTrigger.LEFT_CLICK_BLOCK) {
            return;
        }

        InteractContextBlock context = new InteractContextBlock(event.getPlayer(), ability, action, event.getHand(), event.getClickedBlock());

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

        if (action != Action.RIGHT_CLICK_BLOCK || ability.getTrigger() != AbilityTrigger.RIGHT_CLICK_BLOCK) {
            return;
        }

        InteractContextBlock context = new InteractContextBlock(event.getPlayer(), ability, action, event.getHand(), event.getClickedBlock());

        ability.execute(context);
    }
}