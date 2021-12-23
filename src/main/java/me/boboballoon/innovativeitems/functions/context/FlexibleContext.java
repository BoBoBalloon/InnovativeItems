package me.boboballoon.innovativeitems.functions.context;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.context.interfaces.BlockContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.ItemContext;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * A class that represents a runtime context where no field can be null
 */
public final class FlexibleContext extends RuntimeContext implements EntityContext, BlockContext, ItemContext {
    private final RuntimeContext context;

    public FlexibleContext(@NotNull RuntimeContext context) {
        super(context.getPlayer(), context.getAbility());

        if (context instanceof FlexibleContext) {
            throw new IllegalArgumentException("You have passed an instance of FlexibleContext into the constructor of another instance of FlexibleContext!");
        }

        this.context = context;
    }

    public FlexibleContext(@NotNull Player player, @NotNull Ability ability) {
        super(player, ability);
        this.context = new RuntimeContext(player, ability);
    }

    @Override
    @NotNull
    public Block getBlock() {
        if (this.context instanceof BlockContext) {
            BlockContext blockContext = (BlockContext) this.context;
            return blockContext.getBlock();
        }

        return this.context.getPlayer().getLocation().getBlock();
    }

    @Override
    @NotNull
    public LivingEntity getEntity() {
        if (this.context instanceof EntityContext) {
            EntityContext entityContext = (EntityContext) this.context;
            return entityContext.getEntity();
        }

        Player player = this.context.getPlayer();

        Iterator<Entity> entities = player.getNearbyEntities(5, 5, 5).stream().filter(e -> e instanceof LivingEntity).iterator();

        if (entities.hasNext()) {
            return (LivingEntity) entities.next();
        }

        return player;
    }

    @Override
    @NotNull
    public CustomItem getItem() {
        if (this.context instanceof ItemContext) {
            ItemContext itemContext = (ItemContext) this.context;
            return itemContext.getItem();
        }

        InnovativeCache cache = InnovativeItems.getInstance().getItemCache();

        //check if player has an item in their inventory
        CustomItem item = this.getCustomItemFromInventory(cache);
        if (item != null) {
            return item;
        }

        //get the first item in the cache
        if (cache.getItemAmount() > 0) {
            String identifier = cache.getItemIdentifiers().iterator().next();
            return cache.getItem(identifier);
        }

        //no items exist to make a new dummy item that only exists in memory and register it
        CustomItem dummyItem = new CustomItem("dummy-item", null, Material.DIRT, null, null, null, null, null, null, false, false, false);
        cache.registerItem(dummyItem);
        LogUtil.logUnblocked(LogUtil.Level.INFO, "The player " + this.getPlayer().getName() + " (" + this.getPlayer().getUniqueId() + ") used the ability " + this.getAbility().getIdentifier() + ", which relied on a custom item targeter that had no fallback, so a new dummy fallback item was registered (will only exist in memory).");
        return dummyItem;
    }

    /**
     * A method used to get a custom item in the players main hand if they have one
     *
     * @param cache the cache to get the custom item from
     * @return the custom item in the players hand
     */
    @Nullable
    private CustomItem getCustomItemFromInventory(InnovativeCache cache) {
        PlayerInventory inventory = this.context.getPlayer().getInventory();

        CustomItem mainHand = cache.fromItemStack(inventory.getItemInMainHand());
        if (mainHand != null) {
            return mainHand;
        }

        for (ItemStack item : inventory) {
            CustomItem customItem = cache.fromItemStack(item);

            if (customItem == null) {
                continue;
            }

            return customItem;
        }

        return null;
    }
}