package me.boboballoon.innovativeitems.functions.context;

import de.tr7zw.nbtapi.NBTItem;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.functions.context.interfaces.BlockContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.EntityContext;
import me.boboballoon.innovativeitems.functions.context.interfaces.ItemContext;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.items.item.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

        CustomItem mainHand = this.getCustomItemFromMainHand();
        if (mainHand != null) {
            return mainHand;
        }

        InnovativeCache cache = InnovativeItems.getInstance().getItemCache();

        if (cache.getItemAmount() > 0) {
            String identifier = cache.getItemIdentifiers().iterator().next();
            return cache.getItem(identifier);
        }

        throw new NullPointerException("There is no custom item in the cache to fallback to...");
    }

    /**
     * A method used to get a custom item in the players main hand if they have one
     *
     * @return the custom item in the players hand
     */
    @Nullable
    private CustomItem getCustomItemFromMainHand() {
        ItemStack item = this.context.getPlayer().getInventory().getItemInMainHand();

        if (item == null) {
            return null;
        }

        NBTItem nbtItem = new NBTItem(item);

        if (!nbtItem.hasKey("innovativeplugin-customitem")) {
            return null;
        }

        return InnovativeItems.getInstance().getItemCache().getItem(nbtItem.getString("innovativeplugin-customitem-id"));
    }
}
