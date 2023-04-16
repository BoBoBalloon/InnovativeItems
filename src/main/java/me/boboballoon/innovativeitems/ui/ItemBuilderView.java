package me.boboballoon.innovativeitems.ui;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.ui.base.InnovativeElement;
import me.boboballoon.innovativeitems.ui.base.elements.ConfirmElement;
import me.boboballoon.innovativeitems.ui.base.views.BorderedView;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.ResponseUtil;
import me.boboballoon.innovativeitems.util.RevisedEquipmentSlot;
import me.boboballoon.innovativeitems.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A class that represents a view used to create custom item configuration files
 */
public final class ItemBuilderView extends BorderedView {
    private static final int SIZE = 45;

    private final String identifier;
    private Material type;
    private String display;
    private final List<String> lore;
    private final Map<Enchantment, Integer> enchantments;
    private final List<ItemFlag> flags;
    private final Map<RevisedEquipmentSlot, List<AttributeData>> attributes;
    private Integer customModelData;
    private boolean unbreakable;
    private boolean placeable;
    private boolean soulbound;
    private boolean wearable;
    private Integer maxDurability;
    private boolean updateItem;

    public ItemBuilderView(@NotNull String identifier) {
        super(Material.GRAY_STAINED_GLASS_PANE, "&r&aCustom Item: &r&l" + identifier, ItemBuilderView.empty());

        this.identifier = identifier;

        this.type = Material.DIRT;
        this.display = null;
        this.lore = new ArrayList<>();
        this.enchantments = new HashMap<>();
        this.flags = new ArrayList<>();
        this.attributes = new HashMap<>();
        this.customModelData = null;
        this.unbreakable = false;
        this.placeable = false;
        this.soulbound = false;
        this.wearable = true;
        this.maxDurability = null;
        this.updateItem = true;
        //add more item specific shit later

        this.setBottomRight(new ConfirmElement(player -> {
            player.closeInventory();
            this.write();
        })); //calls reload
    }

    @Override
    public final void setElements(@Nullable ImmutableList<InnovativeElement> elements) throws IllegalArgumentException {
        super.setElements(this.buildView());
    }

    /**
     * A method called when the confirm button is hit and the class should write the item to disk
     */
    private void write() {
        //placeholder
    }

    /**
     * A method used to build an instance of this view's page
     *
     * @return an instance of this view's page
     */
    @NotNull
    private ImmutableList<InnovativeElement> buildView() {
        List<InnovativeElement> elements = new ArrayList<>(ItemBuilderView.SIZE);

        for (int i = 0; i < ItemBuilderView.SIZE; i++) {
            int row = i / 9;
            int col = i % 9;

            if (row == 0 || col == 0 || row == (ItemBuilderView.SIZE / 9) - 1 || col == (ItemBuilderView.SIZE / 9) - 1) {
                elements.set(i, null);
            }
        }

        //material
        elements.add(this.build(this.type, "Material: " + this.type.name(), null, stack -> {
            stack.setType(this.type);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(TextUtil.format("Material: " + this.type.name()));
            stack.setItemMeta(meta);
        }, null, (response, player) -> {
            Material material;
            try {
                material = Material.valueOf(response.toUpperCase());
            } catch (IllegalArgumentException e) {
                TextUtil.sendMessage(player, "&r&cYou have entered an invalid material name!");
                this.open(player);
                return;
            }

            this.type = material;

            if (this.type.getMaxDurability() <= 0) {
                this.maxDurability = null;
                this.unbreakable = false;
            }
        }, "Please enter the material name of " + this.identifier + "!"));

        //display name
        elements.add(this.build(this.type, "Display Name", Collections.singletonList("&rRight click to reset the display name"), stack -> {
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(TextUtil.format(this.display != null ? "Display Name: " + this.display : "Display Name"));
            stack.setItemMeta(meta);
        }, () -> this.display = null, (response, player) -> this.display = response, "Please enter the display name of " + this.identifier + "!"));

        //lore
        elements.add(this.build(this.type, "Lore", Collections.singletonList("&rRight click to reset the lore"), stack -> {
            ItemMeta meta = stack.getItemMeta();
            List<String> lore = new ArrayList<>(this.lore.size() + 1);
            lore.add("&rRight click to reset the lore");
            lore.addAll(this.lore);

            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, TextUtil.format(lore.get(i)));
            }

            meta.setLore(lore);
            stack.setItemMeta(meta);
        }, this.lore::clear, (response, player) -> this.lore.add(response), "Please enter a line of lore you would like " + this.identifier + " to have!"));

        //enchantments

        //item flags

        //attributes

        //custom model data

        //unbreakable (only show if damageable)
        if (this.type.getMaxDurability() > 0) {
            elements.add(InnovativeElement.build(Material.NETHER_STAR, "Unbreakable: " + this.unbreakable, (player, click) -> {
                this.unbreakable = !this.unbreakable;
                this.reload();
            }, stack -> {
                ItemMeta meta = stack.getItemMeta();
                String value = String.valueOf(this.unbreakable);
                meta.setDisplayName(TextUtil.format("Unbreakable: " + value.substring(0, 1).toUpperCase() + value.substring(1)));
                stack.setItemMeta(meta);
            }));
        }

        //placeable
        elements.add(InnovativeElement.build(Material.OAK_LOG, "Placeable: " + this.placeable, (player, click) -> {
            this.placeable = !this.placeable;
            this.reload();
        }, stack -> {
            ItemMeta meta = stack.getItemMeta();
            String value = String.valueOf(this.placeable);
            meta.setDisplayName(TextUtil.format("Placeable: " + value.substring(0, 1).toUpperCase() + value.substring(1)));
            stack.setItemMeta(meta);
        }));

        //soulbound
        elements.add(InnovativeElement.build(Material.BLAZE_POWDER, "Soulbound: " + this.soulbound, (player, click) -> {
            this.soulbound = !this.soulbound;
            this.reload();
        }, stack -> {
            ItemMeta meta = stack.getItemMeta();
            String value = String.valueOf(this.soulbound);
            meta.setDisplayName(TextUtil.format("Soulbound: " + value.substring(0, 1).toUpperCase() + value.substring(1)));
            stack.setItemMeta(meta);
        }));

        //wearable
        elements.add(InnovativeElement.build(Material.IRON_HELMET, "Wearable: " + this.wearable, (player, click) -> {
            this.wearable = !this.wearable;
            this.reload();
        }, stack -> {
            ItemMeta meta = stack.getItemMeta();
            String value = String.valueOf(this.wearable);
            meta.setDisplayName(TextUtil.format("Wearable: " + value.substring(0, 1).toUpperCase() + value.substring(1)));
            stack.setItemMeta(meta);
        }));

        //max durability (only show if damageable)
        if (this.type.getMaxDurability() > 0) {
            //get integer from chat
        }

        //update item
        elements.add(InnovativeElement.build(Material.CRAFTING_TABLE, "Update Item: " + this.updateItem, (player, click) -> {
            this.updateItem = !this.updateItem;
            this.reload();
        }, stack -> {
            ItemMeta meta = stack.getItemMeta();
            String value = String.valueOf(this.updateItem);
            meta.setDisplayName(TextUtil.format("Update Item: " + value.substring(0, 1).toUpperCase() + value.substring(1)));
            stack.setItemMeta(meta);
        }));

        return ImmutableList.copyOf(elements);
    }

    /**
     * A method used to prevent repetitive code
     *
     * @param material the material of the element
     * @param display the display name of the element
     * @param lore the lore of the
     * @param onLoad what code to execute when the item is loaded
     * @param onRightClick what code to execute when the item is right clicked
     * @param clickSuccess what code to execute when the variable should be changed
     * @param prompt What input to ask the user for
     * @return an instance of a view element
     */
    @NotNull
    private InnovativeElement build(@NotNull Material material, @Nullable String display, @Nullable List<String> lore, @Nullable Consumer<ItemStack> onLoad, @Nullable Runnable onRightClick, @NotNull BiConsumer<String, Player> clickSuccess, @NotNull String prompt) {
        return InnovativeElement.build(material, display, lore, (player, click) -> {
            player.closeInventory();

            if (onRightClick != null && click == ClickType.RIGHT) {
                onRightClick.run();
                this.open(player);
                return;
            }

            boolean success = ResponseUtil.input( prompt + " Type &r&ccancel&r to end the prompt.", player, response -> {
                if (response == null) {
                    this.open(player);
                    return;
                }

                clickSuccess.accept(response, player);
                this.open(player);
            });

            if (!success) {
                LogUtil.logUnblocked(LogUtil.Level.SEVERE, "An error occurred asking for user input for " + player.getName() +  ". Please contact the developer");
                this.open(player);
            }
        }, onLoad);
    }

    /**
     * A method that returns an list of elements size {@link ItemBuilderView} SIZE filled with null
     *
     * @return an list of elements size 36 filled with null
     */
    @NotNull
    private static ImmutableList<InnovativeElement> empty() {
        List<InnovativeElement> list = new ArrayList<>(ItemBuilderView.SIZE);

        for (int i = 0; i < ItemBuilderView.SIZE; i++) {
            list.add(null);
        }

        return ImmutableList.copyOf(list);
    }

    /**
     * A class used to store attribute data
     */
    private static class AttributeData {
        private final Attribute attribute;
        private final int level;

        public AttributeData(@NotNull Attribute attribute, int level) {
            this.attribute = attribute;
            this.level = level;
        }

        @NotNull
        public Attribute getAttribute() {
            return this.attribute;
        }

        public int getLevel() {
            return this.level;
        }
    }
}
