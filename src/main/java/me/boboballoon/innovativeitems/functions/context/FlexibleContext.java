package me.boboballoon.innovativeitems.functions.context;

import com.google.common.collect.ImmutableList;
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
    private final LivingEntity entity;
    private final Block block;
    private final CustomItem item;

    public FlexibleContext(@NotNull Player player, @NotNull Ability ability, @Nullable LivingEntity entity, @Nullable Block block, @Nullable CustomItem item) {
        super(player, ability);
        this.entity = entity;
        this.block = block;
        this.item = item;
    }

    public FlexibleContext(@NotNull Player player, @NotNull Ability ability, @NotNull RuntimeContext delegate) {
        this(player, ability,
                delegate instanceof EntityContext ? ((EntityContext) delegate).getEntity() : null,
                delegate instanceof BlockContext ? ((BlockContext) delegate).getBlock() : null,
                delegate instanceof ItemContext ? ((ItemContext) delegate).getItem() : null);
    }

    public FlexibleContext(@NotNull Player player, @NotNull Ability ability) {
        this(player, ability, null, null, null);
    }

    @Override
    @NotNull
    public LivingEntity getEntity() {
        if (this.entity != null) {
            return this.entity;
        }

        Player player = this.getPlayer();

        Iterator<Entity> entities = player.getNearbyEntities(5, 5, 5).stream().filter(e -> e instanceof LivingEntity).iterator();

        if (entities.hasNext()) {
            return (LivingEntity) entities.next();
        }

        return player;
    }

    @Override
    @NotNull
    public Block getBlock() {
        if (this.block != null) {
            return this.block;
        }

        return this.getPlayer().getLocation().getBlock();
    }

    @Override
    @NotNull
    public CustomItem getItem() {
        if (this.item != null) {
            return this.item;
        }

        InnovativeCache cache = InnovativeItems.getInstance().getItemCache();

        //check if player has an item in their inventory
        CustomItem item = this.getCustomItemFromInventory(cache);
        if (item != null) {
            return item;
        }

        //get the first item in the cache
        ImmutableList<CustomItem> items = cache.getItems();
        if (items.size() > 0) {
            return items.stream().findAny().get();
        }

        //no items exist to make a new dummy item that only exists in memory and register it
        CustomItem dummyItem = new CustomItem("dummy-item", null, Material.DIRT, null, null, null, null, null, null, false, false, false, false, 0, false, null);
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
        PlayerInventory inventory = this.getPlayer().getInventory();

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

    /**
     * A method used to wrap a runtime context and convert it into an instance of the flexible context class
     *
     * @param context the context to wrap
     * @return an instance of the flexible context class
     */
    @NotNull
    public static FlexibleContext wrap(@NotNull RuntimeContext context) {
        return context instanceof FlexibleContext ? (FlexibleContext) context : new FlexibleContext(context.getPlayer(), context.getAbility(), context);
    }
}