package me.boboballoon.innovativeitems.items.item;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * A class that represents a custom item that is a player skull
 */
public class CustomItemSkull implements CustomItem {
    private final String name;
    private final Ability ability;
    private final ItemStack itemStack;

    public CustomItemSkull(@NotNull String name, @Nullable Ability ability, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, boolean unbreakable, @Nullable String skullName, @Nullable String base64) {
        this.name = name;
        this.ability = ability;
        this.itemStack = this.generateItem(itemName, lore, enchantments, flags, attributes, customModelData, unbreakable, skullName, base64);
    }

    /**
     * A method used to get the internal name of the custom item
     *
     * @return the internal name of the custom item
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * A method used to get the ability associated with this custom item (can be null if nothing is present)
     *
     * @return the ability associated with this custom item (can be null if nothing is present)
     */
    @Nullable
    @Override
    public Ability getAbility() {
        return this.ability;
    }

    /**
     * A method used to get the itemstack that represents this custom item
     *
     * @return an itemstack that represents this custom item
     */
    @Override
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    /**
     * A method used to generate an itemstack based on this items internal values
     *
     * @param itemName the display name of the itemstack
     * @param lore the lore of the itemstack
     * @param enchantments the enchantments on the itemstack
     * @param flags the item flags on the itemstack
     * @param attributes all attributes for this item
     * @param customModelData the custom model data on the itemstack
     * @param unbreakable if the custom item is unbreakable
     * @param skullName the name of the player whose skin you wish to place on a player skull (if applicable)
     * @param base64 a base64 encoded string of the skin you wish to place on a player skull (if applicable)
     * @return the itemstack
     */
    private ItemStack generateItem(@Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, boolean unbreakable, @Nullable String skullName, @Nullable String base64) {
        ItemStack item = CustomItem.generateItem(this.name, Material.PLAYER_HEAD, itemName, lore, enchantments, flags, attributes, customModelData, unbreakable);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        if (base64 != null) {
            this.setSkinViaBase64(meta, base64);
        }

        if (skullName != null && base64 == null) {
            meta.setOwner(skullName);
        }

        item.setItemMeta(meta);
        return item;
    }

    /**
     * A method used to set the skin of a player skull via a base64 encoded string
     *
     * @param meta the skull meta to modify
     * @param base64 the base64 encoded string
     */
    private void setSkinViaBase64(SkullMeta meta, String base64) {
        try {
            Method setProfile = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            setProfile.setAccessible(true);

            UUID uuid = new UUID(base64.substring(base64.length() - 20).hashCode(), base64.substring(base64.length() - 10).hashCode());

            GameProfile profile = new GameProfile(uuid, "skull-texture");
            profile.getProperties().put("textures", new Property("textures", base64));

            setProfile.invoke(meta, profile);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LogUtil.log(Level.SEVERE, "There was a severe internal reflection error when attempting to set the skin of a player skull via base64!");
            e.printStackTrace();
        }
    }
}
