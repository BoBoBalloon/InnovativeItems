package me.boboballoon.innovativeitems.items.item;

import com.google.common.collect.Multimap;
import me.boboballoon.innovativeitems.items.ability.Ability;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * A class that represents a custom item that is a firework rocket
 */
public class CustomItemFirework implements CustomItem {
    private final String name;
    private final Ability ability;
    private final ItemStack itemStack;

    public CustomItemFirework(@NotNull String name, @Nullable Ability ability, @Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, @Nullable List<FireworkEffect> fireworkEffects, @Nullable Integer power) {
        this.name = name;
        this.ability = ability;
        this.itemStack = this.generateItem(itemName, lore, enchantments, flags, attributes, customModelData, fireworkEffects, power);
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
     * @param itemName        the display name of the itemstack
     * @param lore            the lore of the itemstack
     * @param enchantments    the enchantments on the itemstack
     * @param flags           the item flags on the itemstack
     * @param attributes      all attributes for this item
     * @param customModelData the custom model data on the itemstack
     * @param fireworkEffects the effects of the firework
     * @param power           the amount of time until it is launched until it fires the provided effects
     * @return the itemstack
     */
    private ItemStack generateItem(@Nullable String itemName, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchantments, @Nullable List<ItemFlag> flags, @Nullable Multimap<Attribute, AttributeModifier> attributes, @Nullable Integer customModelData, @Nullable List<FireworkEffect> fireworkEffects, @Nullable Integer power) {
        ItemStack item = CustomItem.generateItem(this.name, Material.FIREWORK_ROCKET, itemName, lore, enchantments, flags, attributes, customModelData, false, true);
        FireworkMeta meta = (FireworkMeta) item.getItemMeta();

        if (fireworkEffects != null) {
            meta.addEffects(fireworkEffects);
        }

        if (power != null) {
            meta.setPower(power);
        }

        item.setItemMeta(meta);
        return item;
    }
}
