package me.boboballoon.innovativeitems.ui;

import com.google.common.collect.ImmutableList;
import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.ui.base.InnovativeElement;
import me.boboballoon.innovativeitems.ui.base.elements.ConfirmElement;
import me.boboballoon.innovativeitems.ui.base.views.BorderedView;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.ResponseUtil;
import me.boboballoon.innovativeitems.util.RevisedEquipmentSlot;
import me.boboballoon.innovativeitems.util.TextUtil;
import org.bukkit.Bukkit;
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

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A class that represents a view used to create custom item configuration files
 */
public final class ItemBuilderView extends BorderedView {
    private static final int SIZE = 45;

    private final String identifier;
    private Material type;
    private String display;
    private final List<String> lore;
    private final List<EnchantingData> enchantments;
    private final List<ItemFlag> flags;
    private final Map<RevisedEquipmentSlot, List<AttributeData>> attributes;
    private Integer customModelData;
    private boolean unbreakable;
    private boolean placeable;
    private boolean soulbound;
    private boolean wearable;
    private Integer maxDurability;
    private boolean updateItem;

    /**
     * Constructor used to creating new item
     *
     * @param identifier id of item
     */
    public ItemBuilderView(@NotNull String identifier) {
        super(Material.GRAY_STAINED_GLASS_PANE, "&r&aCustom Item: &r&l" + identifier, ItemBuilderView.empty());

        this.identifier = identifier;

        this.type = Material.DIRT;
        this.display = null;
        this.lore = new ArrayList<>();
        this.enchantments = new ArrayList<>();
        this.flags = new ArrayList<>();
        this.attributes = new HashMap<>();
        for (RevisedEquipmentSlot slot : RevisedEquipmentSlot.values()) {
            this.attributes.put(slot, new ArrayList<>());
        }
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
        })); //calls setElements
    }

    @Override
    public final void setElements(@Nullable ImmutableList<InnovativeElement> elements) throws IllegalArgumentException {
        super.setElements(this.buildView());
    }

    @Override
    public final void open(@NotNull Player player) {
        player.openInventory(this.getInventory());
        this.setElements(null);
    }

    /**
     * A method called when the confirm button is hit and the class should write the item to disk
     */
    private void write() {
        File file = new File(InnovativeItems.getInstance().getDataFolder().getPath() + "/items", this.identifier + ".yml");
        Bukkit.broadcastMessage("path = " + file.getPath()); //remove
        Bukkit.broadcastMessage("identifier = " + this.identifier); //remove
        Bukkit.broadcastMessage("material = " + this.type.name()); //remove
        Bukkit.broadcastMessage("display = " + this.display); //remove
        Bukkit.broadcastMessage("lore = " + this.lore); //remove
        Bukkit.broadcastMessage("enchantments = " + this.enchantments); //remove
        Bukkit.broadcastMessage("flags = " + this.flags); //remove
        Bukkit.broadcastMessage("attributes = " + this.attributes); //remove
        Bukkit.broadcastMessage("custom model data = " + this.customModelData); //remove
        Bukkit.broadcastMessage("unbreakable = " + this.unbreakable); //remove
        Bukkit.broadcastMessage("placeable = " + this.placeable); //remove
        Bukkit.broadcastMessage("soulbound = " + this.soulbound); //remove
        Bukkit.broadcastMessage("wearable = " + this.wearable); //remove
        Bukkit.broadcastMessage("max durability = " + this.maxDurability); //remove
        Bukkit.broadcastMessage("update item = " + this.updateItem); //remove
    }

    /**
     * A method used to build an instance of this view's page
     *
     * @return an instance of this view's page
     */
    @NotNull
    private ImmutableList<InnovativeElement> buildView() {
        List<InnovativeElement> elements = new NullableList<>(ItemBuilderView.SIZE);

        for (int i = 0; i < ItemBuilderView.SIZE; i++) {
            elements.add(null);

            int row = i / 9;
            int col = i % 9;

            if (row == 0 || col == 0 || row == (ItemBuilderView.SIZE / 9) - 1 || col == 8) {
                elements.set(i, InnovativeElement.EMPTY);
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
                return;
            }

            this.type = material;

            if (this.type.getMaxDurability() <= 0) {
                this.maxDurability = null;
                this.unbreakable = false;
            }
        }, "Please enter the material name of " + this.identifier + "!"));

        //display name
        elements.add(this.build(Material.NAME_TAG, "Display Name", Collections.singletonList("&rRight click to reset the display name"), stack -> {
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(TextUtil.format(this.display != null ? "Display Name: " + this.display : "Display Name"));
            stack.setItemMeta(meta);
        }, () -> this.display = null, (response, player) -> this.display = response, "Please enter the display name of " + this.identifier + "!"));

        //lore
        elements.add(this.build(Material.WRITABLE_BOOK, "Lore", Collections.singletonList("&rRight click to reset the lore"), stack -> {
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
        elements.add(this.build(Material.ENCHANTING_TABLE, "Enchantments", Collections.singletonList("&rRight click to clear enchantments"), stack -> {
            ItemMeta meta = stack.getItemMeta();
            List<String> lore = new ArrayList<>(this.enchantments.size() + 1);
            lore.add("&rRight click to clear enchantments");
            lore.addAll(this.enchantments.stream().map(data -> data.getEnchantment().getName() + " " + data.getLevel()).collect(Collectors.toList()));

            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, TextUtil.format(lore.get(i)));
            }

            meta.setLore(lore);
            stack.setItemMeta(meta);
        }, this.enchantments::clear, (response, player) -> {
            String[] split = response.split(",");

            if (split.length != 2) {
                TextUtil.sendMessage(player, "&r&cYou have entered invalid information. Please follow the syntax in the previous message.");
                return;
            }

            Enchantment enchantment = Enchantment.getByName(split[0].toUpperCase());

            if (enchantment == null) {
                TextUtil.sendMessage(player, "&r&cYou have entered an invalid enchantment. Please refer to the documentation for assistance.");
                return;
            }

            int level;
            try {
                level = Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                TextUtil.sendMessage(player, "&r&cPlease enter an integer for the enchantment level.");
                return;
            }

            this.enchantments.add(new EnchantingData(enchantment, level));
        }, "Please enter the enchantment data you would like to add in the format: &r&aEnchantment&r, &r&aLevel&r."));

        //item flags
        elements.add(this.build(Material.WHITE_BANNER, "Item Flags", Collections.singletonList("&rRight click to reset the item flags"), stack -> {
            ItemMeta meta = stack.getItemMeta();
            List<String> lore = new ArrayList<>(this.flags.size() + 1);
            lore.add("&rRight click to reset the lore");
            lore.addAll(this.flags.stream().map(ItemFlag::name).collect(Collectors.toList()));

            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, TextUtil.format("&r" + lore.get(i)));
            }

            meta.setLore(lore);
            stack.setItemMeta(meta);
        }, this.flags::clear, (response, player) -> {
            ItemFlag flag;
            try {
                flag = ItemFlag.valueOf(response.toUpperCase());
            } catch (IllegalArgumentException e) {
                TextUtil.sendMessage(player, "&r&cPlease enter a valid item flag. Refer to the documentation for assistance.");
                return;
            }

            this.flags.add(flag);
        }, "Please enter an item flag you would like to be added to " + this.identifier + "!"));

        //attributes
        elements.add(this.build(Material.ENDER_EYE, "Attributes", Collections.singletonList("&rRight click to clear attributes"), stack -> {
            ItemMeta meta = stack.getItemMeta();
            List<String> lore = new ArrayList<>(this.enchantments.size() + 1);
            lore.add("&rRight click to clear attributes");

            for (RevisedEquipmentSlot slot : RevisedEquipmentSlot.values()) {
                List<AttributeData> attributes = this.attributes.get(slot);

                if (!attributes.isEmpty()) {
                    lore.add(slot.name().charAt(0) + slot.name().substring(1).toLowerCase() + ":");
                    for (AttributeData data : attributes) {
                        lore.add(data.getAttribute().name().charAt(0) + data.getAttribute().name().substring(1).toLowerCase() + ": " + data.getLevel());
                    }
                }
            }

            meta.setLore(lore);
            stack.setItemMeta(meta);
        }, () -> this.attributes.keySet().forEach(slot -> this.attributes.get(slot).clear()), (response, player) -> {
            String[] split = response.split(",");

            if (split.length != 3) {
                TextUtil.sendMessage(player, "&r&cYou have entered invalid information. Please follow the syntax in the previous message.");
                return;
            }

            RevisedEquipmentSlot slot;
            try {
                slot = RevisedEquipmentSlot.valueOf(split[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                TextUtil.sendMessage(player, "&r&cYou have entered an invalid equipment slot. The possible options are: &r&c" + Arrays.stream(RevisedEquipmentSlot.values()).map(value -> value.name().charAt(0) + value.name().substring(1).toLowerCase()).collect(Collectors.joining(", ")));
                return;
            }

            Attribute attribute;
            try {
                attribute = Attribute.valueOf(split[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                TextUtil.sendMessage(player, "&r&cYou have entered an invalid attributes. The possible options are: &r&c" + Arrays.stream(Attribute.values()).map(Attribute::name).collect(Collectors.joining(", ")));
                return;
            }

            int level;
            try {
                level = Integer.parseInt(split[2]);
            } catch (NumberFormatException e) {
                TextUtil.sendMessage(player, "&r&cPlease enter an integer for the attribute level.");
                return;
            }

            this.attributes.get(slot).add(new AttributeData(attribute, level));
        }, "Please enter a new attribute for " + this.identifier + " in the format: &r&aEquipment Slot&r, &r&aAttribute&r, &r&aLevel"));

        //custom model data
        elements.add(this.build(Material.CLAY_BALL, "Custom Model Data", null, stack -> {
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(TextUtil.format(this.customModelData != null ? "Custom Model Data: " + this.customModelData : "Custom Model Data"));
            stack.setItemMeta(meta);
        }, () -> this.customModelData = null, (response, player) -> {
            int value;
            try {
                value = Integer.parseInt(response);
            } catch (NumberFormatException e) {
                TextUtil.sendMessage(player, "&r&cPlease enter a valid integer!");
                return;
            }

            this.customModelData = value;
        }, "Please enter the custom model data value of " + this.identifier + "!"));

        //unbreakable (only show if damageable)
        if (this.type.getMaxDurability() > 0) {
            elements.add(InnovativeElement.build(Material.NETHER_STAR, "Unbreakable: " + this.unbreakable, (player, click) -> {
                this.unbreakable = !this.unbreakable;
                this.setElements(null);
            }, stack -> {
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(TextUtil.format("Unbreakable: " + this.unbreakable));
                stack.setItemMeta(meta);
            }));
        }

        //placeable
        elements.add(InnovativeElement.build(Material.OAK_LOG, "Placeable: " + this.placeable, (player, click) -> {
            this.placeable = !this.placeable;
            this.setElements(null);
        }, stack -> {
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(TextUtil.format("Placeable: " + this.placeable));
            stack.setItemMeta(meta);
        }));

        //soulbound
        elements.add(InnovativeElement.build(Material.BLAZE_POWDER, "Soulbound: " + this.soulbound, (player, click) -> {
            this.soulbound = !this.soulbound;
            this.setElements(null);
        }, stack -> {
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(TextUtil.format("Soulbound: " + this.soulbound));
            stack.setItemMeta(meta);
        }));

        //wearable
        elements.add(InnovativeElement.build(Material.IRON_HELMET, "Wearable: " + this.wearable, (player, click) -> {
            this.wearable = !this.wearable;
            this.setElements(null);
        }, stack -> {
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(TextUtil.format("Wearable: " + this.wearable));
            stack.setItemMeta(meta);
        }));

        //max durability (only show if damageable)
        if (this.type.getMaxDurability() > 0) {
            elements.add(this.build(Material.STICK, "Max Durability", null, stack -> {
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(TextUtil.format(this.maxDurability != null ? "Max Durability: " + this.maxDurability : "Max Durability"));
                stack.setItemMeta(meta);
            }, () -> this.maxDurability = null, (response, player) -> {
                int value;
                try {
                    value = Integer.parseInt(response);
                } catch (NumberFormatException e) {
                    TextUtil.sendMessage(player, "&r&cPlease enter a valid integer!");
                    return;
                }

                this.maxDurability = value;
            }, "Please enter the max durability value of " + this.identifier + "!"));
        }

        //update item
        elements.add(InnovativeElement.build(Material.CRAFTING_TABLE, "Update Item: " + this.updateItem, (player, click) -> {
            this.updateItem = !this.updateItem;
            this.setElements(null);
        }, stack -> {
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(TextUtil.format("Update Item: " + this.updateItem));
            stack.setItemMeta(meta);
        }));

        for (int i = 0; i < ItemBuilderView.SIZE; i++) {
            if (elements.get(i) == null) {
                elements.set(i, InnovativeElement.EMPTY);
            }
        }

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
            if (onRightClick != null && click == ClickType.RIGHT) {
                onRightClick.run();
                this.setElements(null);
                return;
            }

            player.closeInventory();

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
                TextUtil.sendMessage(player, "&r&cAn internal error occurred.");
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
            list.add(InnovativeElement.EMPTY);
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

    /**
     * A class used to store enchantment data
     */
    private static class EnchantingData {
        private final Enchantment enchantment;
        private final int level;

        public EnchantingData(@NotNull Enchantment enchantment, int level) {
            this.enchantment = enchantment;
            this.level = level;
        }

        @NotNull
        public Enchantment getEnchantment() {
            return this.enchantment;
        }

        public int getLevel() {
            return this.level;
        }
    }

    /**
     * An extension of an array list that when add is called will first check if a null element exists and if so place it there
     *
     * @param <E> the type of element in the array
     */
    private static class NullableList<E> extends ArrayList<E> {
        public NullableList(int initialCapacity) {
            super(initialCapacity);
        }

        @Override
        public boolean add(@Nullable E e) {
            int index = this.indexOf(null);
            if (index == -1 || e == null) {
                return super.add(e);
            }

            this.set(index, e);
            return true;
        }
    }
}