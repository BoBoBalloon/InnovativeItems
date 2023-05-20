package me.boboballoon.innovativeitems.ui;

import me.boboballoon.innovativeitems.InnovativeItems;
import me.boboballoon.innovativeitems.config.ItemParser;
import me.boboballoon.innovativeitems.items.InnovativeCache;
import me.boboballoon.innovativeitems.items.ability.Ability;
import me.boboballoon.innovativeitems.ui.base.InnovativeElement;
import me.boboballoon.innovativeitems.ui.base.elements.ConfirmElement;
import me.boboballoon.innovativeitems.ui.base.views.BorderedView;
import me.boboballoon.innovativeitems.ui.base.views.DisplayView;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.ResponseUtil;
import me.boboballoon.innovativeitems.util.RevisedEquipmentSlot;
import me.boboballoon.innovativeitems.util.TextUtil;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

/**
 * A class that represents a view used to create custom item configuration files
 */
public final class ItemBuilderView extends BorderedView {
    private static final int SIZE = 45;
    private static final List<InnovativeElement> EMPTY = ItemBuilderView.empty();

    //general items
    private final String identifier;
    private Material type;
    private String ability;
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
    //add custom recipe support later

    //skulls
    private String playerName;
    private String base64;

    //leather armor & potions & shield
    private DyeColor color;

    //leather armor & potions
    private Color rgb;

    //potions
    private final List<PotionEffect> potionEffects;

    //banners & shields
    private final List<Pattern> bannerPatterns;

    //fireworks
    private Integer flightTime;
    private final List<FireworkEffect> fireworkEffects;

    /**
     * Constructor used to creating new item
     *
     * @param identifier id of item
     */
    public ItemBuilderView(@NotNull String identifier) {
        super(Material.GRAY_STAINED_GLASS_PANE, "&r&aCustom Item: &r&l" + identifier, ItemBuilderView.EMPTY);

        //general item stuff
        this.identifier = identifier;
        this.type = Material.DIRT;
        this.ability = null;
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

        //skulls
        this.playerName = null;
        this.base64 = null;

        //leather armor & potions & shield
        this.color = null;

        //leather armor & potions
        this.rgb = null;

        //potions
        this.potionEffects = new ArrayList<>();

        //banners & shields
        this.bannerPatterns = new ArrayList<>();

        //fireworks
        this.flightTime = null;
        this.fireworkEffects = new ArrayList<>();
        
        this.addSetElementsListener(page -> {
            page.clear();
            page.addAll(this.buildView());
        });
        this.addOpenListener(player -> this.setElements(ItemBuilderView.EMPTY));

        this.setBottomRight(new ConfirmElement(player -> {
            player.closeInventory();
            this.write();
            TextUtil.sendMessage(player, "&r&aStarting asynchronous reload in five seconds!");
            InnovativeItems.getInstance().getConfigManager().reload();
        })); //calls setElements
    }

    /**
     * A method called when the confirm button is hit and the class should write the item to disk
     */
    private void write() {
        File file = new File(InnovativeItems.getInstance().getDataFolder().getPath() + "/items", this.identifier + ".yml");

        //if file already exists save data and add more (FIND OUT HOW)

        Bukkit.broadcastMessage("path = " + file.getPath()); //remove
        Bukkit.broadcastMessage("identifier = " + this.identifier); //remove
        Bukkit.broadcastMessage("material = " + this.type.name()); //remove
        Bukkit.broadcastMessage("ability = " + this.ability); //remove
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

        Bukkit.broadcastMessage("skull player name = " + this.playerName); //remove
        Bukkit.broadcastMessage("skull base64 = " + this.base64); //remove
        Bukkit.broadcastMessage("color = " + this.color); //remove
        Bukkit.broadcastMessage("rgb = " + this.rgb); //remove
        Bukkit.broadcastMessage("potion effects = " + this.potionEffects); //remove
        Bukkit.broadcastMessage("banner pattern = " + this.bannerPatterns); //remove
        Bukkit.broadcastMessage("flightTime = " + this.flightTime); //remove
        Bukkit.broadcastMessage("firework effects = " + this.fireworkEffects); //remove
    }

    /**
     * A method used to build an instance of this view's page
     *
     * @return an instance of this view's page
     */
    @NotNull
    private List<InnovativeElement> buildView() {
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
        elements.add(this.build(this.type, null, null, stack -> {
            stack.setType(this.type);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(TextUtil.format("&r&fMaterial: " + this.type.name()));
            meta.setLore(Collections.singletonList(TextUtil.format("&r&fMiddle click to set the type to the item in your hand")));
            meta.addItemFlags(ItemFlag.values());
            stack.setItemMeta(meta);
        }, null, player -> {
            Material type = player.getInventory().getItemInMainHand().getType();

            if (type != Material.AIR) {
                this.setType(type);
            }
        }, (response, player) -> {
            Material material;
            try {
                material = Material.valueOf(response.toUpperCase());
            } catch (IllegalArgumentException e) {
                TextUtil.sendMessage(player, "&r&cYou have entered an invalid material name!");
                return;
            }

            this.setType(material);
        }, "Please enter the material name of " + this.identifier + "!"));

        //ability
        elements.add(InnovativeElement.build(Material.NETHER_STAR, null, null, (player, click) -> {
            if (click == ClickType.RIGHT) {
                this.ability = null;
                this.setElements(ItemBuilderView.EMPTY);
                return;
            }

            player.closeInventory();

            if (click != ClickType.MIDDLE) {
                InnovativeCache cache = InnovativeItems.getInstance().getItemCache();
                Collection<Ability> abilities = cache.getAbilityIdentifiers().stream().map(cache::getAbility).collect(Collectors.toSet());

                DisplayView<Ability> selector = new DisplayView<>("&r&aCustom Item: &r&l" + this.identifier, abilities, a -> {
                    ItemStack stack = new ItemStack(Material.NETHER_STAR);
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName(TextUtil.format("&r&f" + a.getIdentifier()));
                    stack.setItemMeta(meta);
                    return stack;
                }, (p, a) -> {
                    p.closeInventory();
                    this.ability = a.getIdentifier();
                    this.open(p);
                });
                selector.open(player);
                return;
            }

            boolean success = ResponseUtil.input("Please enter the ability name of the ability of " + this.identifier + "! Type &r&ccancel&r&f to end the prompt.", player, response -> {
                if (response == null) {
                    this.open(player);
                    return;
                }

                this.ability = response;
                this.open(player);
            });

            if (!success) {
                LogUtil.logUnblocked(LogUtil.Level.SEVERE, "An error occurred asking for user input for " + player.getName() +  ". Please contact the developer");
                TextUtil.sendMessage(player, "&r&cAn internal error occurred.");
                this.open(player);
            }
        }, stack -> {
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(TextUtil.format(this.ability != null ? "&r&fAbility: " + this.ability : "&r&fAbility"));
            meta.setLore(Arrays.asList(TextUtil.format("&r&fRight click to reset ability"), TextUtil.format("&r&fMiddle click to manually enter ability name")));
            stack.setItemMeta(meta);
        }));

        //display name
        elements.add(this.build(Material.NAME_TAG, null, Collections.singletonList("&r&fRight click to reset the display name"), stack -> {
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(TextUtil.format(this.display != null ? "&r&fDisplay Name: " + this.display : "&r&fDisplay Name"));
            stack.setItemMeta(meta);
        }, () -> this.display = null, (response, player) -> this.display = response, "Please enter the display name of " + this.identifier + "!"));

        //lore
        elements.add(this.build(Material.WRITABLE_BOOK, "&r&fLore", Collections.singletonList("&r&fRight click to reset the lore"), stack -> {
            ItemMeta meta = stack.getItemMeta();
            List<String> lore = new ArrayList<>(this.lore.size() + 1);
            lore.add(TextUtil.format("&r&fRight click to reset the lore"));
            lore.addAll(this.lore.stream().map(TextUtil::format).collect(Collectors.toList()));

            meta.setLore(lore);
            stack.setItemMeta(meta);
        }, this.lore::clear, (response, player) -> this.lore.add(response), "Please enter a line of lore you would like " + this.identifier + " to have!"));

        //enchantments
        elements.add(this.build(Material.ENCHANTING_TABLE, "&r&fEnchantments", Collections.singletonList("&r&fRight click to clear enchantments"), stack -> {
            ItemMeta meta = stack.getItemMeta();
            List<String> lore = new ArrayList<>(this.enchantments.size() + 1);
            lore.add(TextUtil.format("&r&fRight click to clear enchantments"));
            lore.addAll(this.enchantments.stream().map(data -> TextUtil.format("&r&f" + data.getEnchantment().getName() + " " + data.getLevel())).collect(Collectors.toList()));

            meta.setLore(lore);
            stack.setItemMeta(meta);
        }, this.enchantments::clear, (response, player) -> {
            String[] split = response.split(",");

            if (split.length != 2) {
                TextUtil.sendMessage(player, "&r&cYou have entered invalid information. Please follow the syntax in the previous message.");
                return;
            }

            Enchantment enchantment = Enchantment.getByName(split[0].toUpperCase()) != null ? Enchantment.getByName(split[0].toUpperCase()) : Enchantment.getByKey(NamespacedKey.minecraft(split[0].toLowerCase()));

            if (enchantment == null) {
                TextUtil.sendMessage(player, "&r&cYou have entered an invalid enchantment");
                return;
            }

            int level;
            try {
                level = Integer.parseInt(split[1].trim());
            } catch (NumberFormatException e) {
                TextUtil.sendMessage(player, "&r&cPlease enter an integer for the enchantment level.");
                return;
            }

            this.enchantments.add(new EnchantingData(enchantment, level));
        }, "Please enter the enchantment data you would like to add in the format: &r&f&aEnchantment&r&f, &r&f&aLevel&r&f."));

        //item flags
        elements.add(this.build(Material.WHITE_BANNER, "&r&fItem Flags", Collections.singletonList("&r&fRight click to reset the item flags"), stack -> {
            ItemMeta meta = stack.getItemMeta();
            List<String> lore = new ArrayList<>(this.flags.size() + 1);
            lore.add(TextUtil.format("&r&fRight click to reset the lore"));
            lore.addAll(this.flags.stream().map(flag -> TextUtil.format("&r&f" + flag.name())).collect(Collectors.toList()));

            meta.setLore(lore);
            stack.setItemMeta(meta);
        }, this.flags::clear, (response, player) -> {
            ItemFlag flag;
            try {
                flag = ItemFlag.valueOf(response.toUpperCase());
            } catch (IllegalArgumentException e) {
                TextUtil.sendMessage(player, "&r&cPlease enter a valid item flag. The possible options are: &r&c" + Arrays.stream(ItemFlag.values()).map(ItemFlag::name).collect(Collectors.joining(", ")));
                return;
            }

            this.flags.add(flag);
        }, "Please enter an item flag you would like to be added to " + this.identifier + "!"));

        //attributes
        elements.add(this.build(Material.ENDER_EYE, "&r&fAttributes", Collections.singletonList("&r&fRight click to clear attributes"), stack -> {
            ItemMeta meta = stack.getItemMeta();
            List<String> lore = new ArrayList<>(this.enchantments.size() + 1);
            lore.add(TextUtil.format("&r&fRight click to clear attributes"));

            for (RevisedEquipmentSlot slot : RevisedEquipmentSlot.values()) {
                List<AttributeData> attributes = this.attributes.get(slot);

                if (!attributes.isEmpty()) {
                    lore.add(TextUtil.format("&r&f" + slot.name().charAt(0) + slot.name().substring(1).toLowerCase() + ":"));
                    for (AttributeData data : attributes) {
                        lore.add(TextUtil.format("&r&f" + data.getAttribute().name().charAt(0) + data.getAttribute().name().substring(1).toLowerCase() + ": " + data.getLevel()));
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
        }, "Please enter a new attribute for " + this.identifier + " in the format: &r&f&aEquipment Slot&r&f, &r&f&aAttribute&r&f, &r&f&aLevel"));

        //custom model data
        elements.add(this.build(Material.CLAY_BALL, null, Collections.singletonList("&r&fRight click to reset custom model data"), stack -> {
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(TextUtil.format(this.customModelData != null ? "&r&fCustom Model Data: " + this.customModelData : "&r&fCustom Model Data"));
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
            elements.add(InnovativeElement.build(Material.ANVIL, null, (player, click) -> {
                this.unbreakable = !this.unbreakable;
                this.setElements(ItemBuilderView.EMPTY);
            }, stack -> {
                ItemMeta meta = stack.getItemMeta();
                String value = String.valueOf(this.unbreakable);
                meta.setDisplayName(TextUtil.format("&r&fUnbreakable: " + value.substring(0, 1).toUpperCase() + value.substring(1)));
                stack.setItemMeta(meta);
            }));
        }

        //placeable
        elements.add(InnovativeElement.build(Material.OAK_LOG, null, (player, click) -> {
            this.placeable = !this.placeable;
            this.setElements(ItemBuilderView.EMPTY);
        }, stack -> {
            ItemMeta meta = stack.getItemMeta();
            String value = String.valueOf(this.placeable);
            meta.setDisplayName(TextUtil.format("&r&fPlaceable: " + value.substring(0, 1).toUpperCase() + value.substring(1)));
            stack.setItemMeta(meta);
        }));

        //soulbound
        elements.add(InnovativeElement.build(Material.BLAZE_POWDER, null, (player, click) -> {
            this.soulbound = !this.soulbound;
            this.setElements(ItemBuilderView.EMPTY);
        }, stack -> {
            ItemMeta meta = stack.getItemMeta();
            String value = String.valueOf(this.soulbound);
            meta.setDisplayName(TextUtil.format("&r&fSoulbound: " + value.substring(0, 1).toUpperCase() + value.substring(1)));
            stack.setItemMeta(meta);
        }));

        //wearable
        elements.add(InnovativeElement.build(Material.IRON_HELMET, null, (player, click) -> {
            this.wearable = !this.wearable;
            this.setElements(ItemBuilderView.EMPTY);
        }, stack -> {
            ItemMeta meta = stack.getItemMeta();
            String value = String.valueOf(this.wearable);
            meta.setDisplayName(TextUtil.format("&r&fWearable: " + value.substring(0, 1).toUpperCase() + value.substring(1)));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            stack.setItemMeta(meta);
        }));

        //max durability (only show if damageable)
        if (this.type.getMaxDurability() > 0) {
            elements.add(this.build(Material.STICK, "&r&fMax Durability", Collections.singletonList("&r&fRight click to reset max durability"), stack -> {
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(TextUtil.format(this.maxDurability != null ? "&r&fMax Durability: " + this.maxDurability : "&r&fMax Durability"));
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
        elements.add(InnovativeElement.build(Material.CRAFTING_TABLE, null, (player, click) -> {
            this.updateItem = !this.updateItem;
            this.setElements(ItemBuilderView.EMPTY);
        }, stack -> {
            ItemMeta meta = stack.getItemMeta();
            String value = String.valueOf(this.updateItem);
            meta.setDisplayName(TextUtil.format("&r&fUpdate Item: " + value.substring(0, 1).toUpperCase() + value.substring(1)));
            stack.setItemMeta(meta);
        }));

        //only show if skull
        if (this.type == Material.PLAYER_HEAD) {
            elements.add(this.build(Material.PLAYER_HEAD, null, Collections.singletonList("&r&fRight click to reset the player name for the skull"), stack -> {
                SkullMeta meta = (SkullMeta) stack.getItemMeta();
                meta.setDisplayName(TextUtil.format(this.playerName != null ? "&r&fPlayer Name: " + this.playerName : "&r&fPlayer Name"));

                if (this.playerName != null) {
                    meta.setOwner(this.playerName);
                }

                stack.setItemMeta(meta);
            }, () -> this.playerName = null, (response, player) -> this.playerName = response, "Please enter the player name for the skull of " + this.identifier + "!"));

            elements.add(this.build(Material.PLAYER_HEAD, "&r&fBase 64", Collections.singletonList("&r&fRight click to reset the base64 for the skull"), stack -> {
                SkullMeta meta = (SkullMeta) stack.getItemMeta();
                List<String> lore = new ArrayList<>(2);
                lore.add(TextUtil.format("&r&fRight click to reset the base64 for the skull"));

                if (this.base64 != null) {
                    lore.add(TextUtil.format("&r&f&l" + this.base64));
                    ItemParser.SkullItem.setSkinViaBase64(meta, base64);
                }

                meta.setLore(lore);
                stack.setItemMeta(meta);
            }, () -> this.base64 = null, (response, player) -> this.base64 = response, "Please enter the base64 for the skull of " + this.identifier + "!"));
        }

        //only show if leather armor, potions, or shields
        if (this.type == Material.SHIELD || ItemParser.PotionItem.isPotion(this.type) || ItemParser.LeatherArmorItem.isLeatherArmor(this.type)) {
            elements.add(this.build(Material.BARRIER, null, Collections.singletonList("&r&fRight click to reset the color"), stack -> {
                stack.setType(this.color == null ? Material.BARRIER : ItemBuilderView.getDyeMaterial(this.color));
                ItemMeta meta = stack.getItemMeta();

                meta.setDisplayName(this.color != null ? TextUtil.format("&r&fColor: " + this.color.name().charAt(0) + this.color.name().substring(1).toLowerCase()) : TextUtil.format("&r&fColor"));

                stack.setItemMeta(meta);
            }, () -> this.color = null, (response, player) -> {
                DyeColor color;
                try {
                    color = DyeColor.valueOf(response.toUpperCase());
                } catch (IllegalArgumentException e) {
                    TextUtil.sendMessage(player, "&r&cPlease enter a valid color. The possible options are: &r&c" + Arrays.stream(DyeColor.values()).map(DyeColor::name).collect(Collectors.joining(", ")));
                    return;
                }

                this.color = color;
            }, "Please enter a valid color for " + this.identifier + "!"));
        }

        //only show if leather armor, potions
        if (ItemParser.PotionItem.isPotion(this.type) || ItemParser.LeatherArmorItem.isLeatherArmor(this.type)) {
            elements.add(this.build(Material.BARRIER, null, Collections.singletonList("&r&fRight click to reset the RGB"), stack -> {
                stack.setType(this.rgb == null ? Material.BARRIER : ItemBuilderView.getDyeMaterial(DyeColor.getByColor(this.rgb)));
                ItemMeta meta = stack.getItemMeta();

                meta.setDisplayName(this.rgb != null ? TextUtil.format("&r&fRGB: " + this.rgb.getRed() + ", " + this.rgb.getGreen() + ", " + this.rgb.getBlue()) : TextUtil.format("&r&fRGB"));

                stack.setItemMeta(meta);
            }, () -> this.rgb = null, (response, player) -> {
                String[] split = response.split(",");

                if (split.length != 3) {
                    TextUtil.sendMessage(player, "&r&cYou have entered invalid information. Please follow the syntax in the previous message.");
                    return;
                }

                int red;
                try {
                    red = Integer.parseInt(split[0].trim());
                } catch (NumberFormatException e) {
                    TextUtil.sendMessage(player, "&r&cPlease enter a valid integer for the Red field!");
                    return;
                }

                int green;
                try {
                    green = Integer.parseInt(split[1].trim());
                } catch (NumberFormatException e) {
                    TextUtil.sendMessage(player, "&r&cPlease enter a valid integer for the Green field!");
                    return;
                }

                int blue;
                try {
                    blue = Integer.parseInt(split[2].trim());
                } catch (NumberFormatException e) {
                    TextUtil.sendMessage(player, "&r&cPlease enter a valid integer for the Blue field!");
                    return;
                }

                this.rgb = Color.fromRGB(Math.max(Math.min(red, 255), 0), Math.max(Math.min(green, 255), 0), Math.max(Math.min(blue, 255), 0));
            }, "Please enter a valid RGB for " + this.identifier + " in the format: &r&f&aRed&r&f, &r&f&aGreen&r&f, &r&f&aBlue&r&f"));
        }

        //only show if potion
        if (ItemParser.PotionItem.isPotion(this.type)) {
            elements.add(this.build(Material.POTION, "&r&fPotion Effects", Collections.singletonList("&r&fRight click to clear potions effects"), stack -> {
                ItemMeta meta = stack.getItemMeta();
                List<String> lore = new ArrayList<>(this.potionEffects.size() + 1);
                lore.add(TextUtil.format("&r&fRight click to clear potion effects"));
                lore.addAll(this.potionEffects.stream().map(effect -> TextUtil.format("&r&f" + effect.getType().getName() + " " + effect.getAmplifier())).collect(Collectors.toList()));

                meta.setLore(lore);
                meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                stack.setItemMeta(meta);
            }, this.potionEffects::clear, (response, player) -> {
                String[] split = response.split(",");

                if (split.length != 3) {
                    TextUtil.sendMessage(player, "&r&cYou have entered invalid information. Please follow the syntax in the previous message.");
                    return;
                }

                PotionEffectType potionEffectType = PotionEffectType.getByName(split[0].trim());

                if (potionEffectType == null) {
                    TextUtil.sendMessage(player, "&r&cPlease enter a valid potion effect type. The possible options are: &r&c" + Arrays.stream(PotionEffectType.values()).map(PotionEffectType::getName).collect(Collectors.joining(", ")));
                    return;
                }

                int amplifier;
                try {
                    amplifier = Integer.parseInt(split[1].trim());
                } catch (NumberFormatException e) {
                    TextUtil.sendMessage(player, "&r&cPlease enter a valid integer for the amplifier field!");
                    return;
                }

                int duration;
                try {
                    duration = Integer.parseInt(split[2].trim());
                } catch (NumberFormatException e) {
                    TextUtil.sendMessage(player, "&r&cPlease enter a valid integer for the duration field!");
                    return;
                }

                this.potionEffects.add(new PotionEffect(potionEffectType, duration, amplifier));
            }, "Please enter the enchantment data you would like to add in the format: &r&f&aPotion Effect Type&r&f, &r&f&aAmplifier&r&f, &r&f&aDuration&r&f."));
        }

        //only show if shield or banner
        if (this.type == Material.SHIELD || ItemParser.BannerItem.isBanner(this.type)) {
            elements.add(this.build(Material.CREEPER_BANNER_PATTERN, "&r&fBanner Patterns", Collections.singletonList("&r&fRight click to clear potions effects"), stack -> {
                ItemMeta meta = stack.getItemMeta();
                List<String> lore = new ArrayList<>(this.bannerPatterns.size() + 1);
                lore.add(TextUtil.format("&r&fRight click to clear banner patterns"));
                lore.addAll(this.bannerPatterns.stream().map(pattern -> TextUtil.format("&r&f" + pattern.getPattern().name().charAt(0) + pattern.getPattern().name().substring(1).toLowerCase())).collect(Collectors.toList()));

                meta.setLore(lore);
                meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                stack.setItemMeta(meta);
            }, this.bannerPatterns::clear, (response, player) -> {
                String[] split = response.split(",");

                PatternType pattern;
                try {
                    pattern = PatternType.valueOf(split[0].trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    TextUtil.sendMessage(player, "&r&cPlease enter a valid pattern type. The possible options are: &r&c" + Arrays.stream(PatternType.values()).map(PatternType::name).collect(Collectors.joining(", ")));
                    return;
                }

                DyeColor color;
                try {
                    color = DyeColor.valueOf(split[1].trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    TextUtil.sendMessage(player, "&r&cPlease enter a valid dye color. The possible options are: &r&c" + Arrays.stream(DyeColor.values()).map(DyeColor::name).collect(Collectors.joining(", ")));
                    return;
                }

                this.bannerPatterns.add(new Pattern(color, pattern));
            }, "Please enter the banner pattern data you would like to add in the format: &r&f&aBanner Pattern&r&f, &r&f&aColor&r&f."));
        }

        //only show if firework
        if (this.type == Material.FIREWORK_ROCKET) {
            elements.add(this.build(Material.FEATHER, null, Collections.singletonList("&r&fRight click to reset flight time"), stack -> {
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(TextUtil.format(this.flightTime != null ? "&r&fFlight Time: " + this.flightTime : "&r&fFlight Time"));
                stack.setItemMeta(meta);
            }, () -> this.flightTime = null, (response, player) -> {
                int value;
                try {
                    value = Integer.parseInt(response);
                } catch (NumberFormatException e) {
                    TextUtil.sendMessage(player, "&r&cPlease enter a valid integer!");
                    return;
                }

                this.flightTime = value;
            }, "Please enter the flight time of " + this.identifier + "!"));

            elements.add(this.build(Material.TNT, "&r&fFirework Effects", Collections.singletonList("&r&fRight click to clear firework effects"), stack -> {
                ItemMeta meta = stack.getItemMeta();
                List<String> lore = new ArrayList<>(this.fireworkEffects.size() + 1);
                lore.add(TextUtil.format("&r&fRight click to clear firework effects"));
                lore.addAll(this.fireworkEffects.stream().map(effect -> TextUtil.format("&r&f" + effect.getType().name())).collect(Collectors.toList()));

                meta.setLore(lore);
                stack.setItemMeta(meta);
            }, this.fireworkEffects::clear, (response, player) -> {
                String[] split;

                try {
                    split = response.split(",", 3);
                } catch (PatternSyntaxException e) {
                    TextUtil.sendMessage(player, "&r&cYou have entered invalid information. Please follow the syntax in the previous message.");
                    return;
                }

                Boolean flicker = split[0].trim().equalsIgnoreCase("true") ? true : split[0].trim().equalsIgnoreCase("false") ? false : null;
                Boolean trail = split[1].trim().equalsIgnoreCase("true") ? true : split[1].trim().equalsIgnoreCase("false") ? false : null;

                if (flicker == null || trail == null) {
                    TextUtil.sendMessage(player, "&r&cYou must enter a true or false value. Please follow the syntax in the previous message.");
                    return;
                }

                FireworkEffect.Type effectType;
                try {
                    effectType = FireworkEffect.Type.valueOf(split[2].trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    TextUtil.sendMessage(player, "&r&cPlease enter a valid firework effect. The possible options are: &r&c" + Arrays.stream(FireworkEffect.Type.values()).map(FireworkEffect.Type::name).collect(Collectors.joining(", ")));
                    return;
                }

                List<Color> colors = new ArrayList<>();
                for (String raw : split[3].split(",")) {
                    DyeColor color;
                    try {
                        color = DyeColor.valueOf(raw.trim().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        TextUtil.sendMessage(player, "&r&c" + raw + " is an invalid color. The possible options are: &r&c" + Arrays.stream(DyeColor.values()).map(DyeColor::name).collect(Collectors.joining(", ")));
                        continue;
                    }

                    colors.add(color.getColor());
                }

                FireworkEffect.Builder builder = FireworkEffect.builder()
                        .flicker(flicker)
                        .trail(trail)
                        .with(effectType)
                        .withColor(colors.toArray(new Color[0]));

                this.fireworkEffects.add(builder.build());
            }, "Please enter the firework effects you would like to add in the format: &r&f&aHas Flicker&r&f, &r&f&aHas Trail&r&f, &r&f&aEffect Type&r&f, &r&f&aEffect Colors&r&f."));
        }

        return elements;
    }

    /**
     * A method that sets the type of material that this builder should create
     *
     * @param material the new material
     */
    private void setType(@NotNull Material material) {
        this.type = material;

        if (this.type.getMaxDurability() <= 0) {
            this.maxDurability = null;
            this.unbreakable = false;
        }

        //reset item specific fields
        this.playerName = null;
        this.base64 = null;
        this.rgb = null;
        this.color = null;
        this.potionEffects.clear();
        this.bannerPatterns.clear();
        this.flightTime = null;
        this.fireworkEffects.clear();
    }

    /**
     * A method used to prevent repetitive code
     *
     * @param material the material of the element
     * @param display the display name of the element
     * @param lore the lore of the
     * @param onLoad what code to execute when the item is loaded
     * @param onRightClick what code to execute when the item is right clicked
     * @param onMiddleClick what code to execute when the item is middle clicked
     * @param clickSuccess what code to execute when the variable should be changed
     * @param prompt What input to ask the user for
     * @return an instance of a view element
     */
    @NotNull
    private InnovativeElement build(@NotNull Material material, @Nullable String display, @Nullable List<String> lore, @Nullable Consumer<ItemStack> onLoad, @Nullable Runnable onRightClick, @Nullable Consumer<Player> onMiddleClick, @NotNull BiConsumer<String, Player> clickSuccess, @NotNull String prompt) {
        return InnovativeElement.build(material, display, lore, (player, click) -> {
            if (onRightClick != null && click == ClickType.RIGHT) {
                onRightClick.run();
                this.setElements(ItemBuilderView.EMPTY);
                return;
            }

            if (onMiddleClick != null && click == ClickType.MIDDLE) {
                onMiddleClick.accept(player);
                this.setElements(ItemBuilderView.EMPTY);
                return;
            }

            player.closeInventory();

            boolean success = ResponseUtil.input( prompt + " Type &r&ccancel&r&f to end the prompt.", player, response -> {
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
        return this.build(material, display, lore, onLoad, onRightClick, null, clickSuccess, prompt);
    }

    /**
     * A method that returns an list of elements size {@link ItemBuilderView} SIZE filled with null
     *
     * @return an list of elements size 36 filled with null
     */
    @NotNull
    private static List<InnovativeElement> empty() {
        List<InnovativeElement> list = new ArrayList<>(ItemBuilderView.SIZE);

        for (int i = 0; i < ItemBuilderView.SIZE; i++) {
            list.add(InnovativeElement.EMPTY);
        }

        return list;
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

        @Override
        public String toString() {
            return this.attribute.name() + ", " + this.level;
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

        @Override
        public String toString() {
            return this.enchantment.getKey().getKey() + ", " + this.level;
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

    /**
     * Returns a dye Material based on the given DyeColor.
     * (THANKS CHAT-GPT)
     *
     * @param dyeColor The DyeColor to get the Material for.
     * @return The Material for the given DyeColor.
     */
    @NotNull
    private static Material getDyeMaterial(@NotNull DyeColor dyeColor) {
        switch (dyeColor) {
            case WHITE:
                return Material.WHITE_DYE;
            case ORANGE:
                return Material.ORANGE_DYE;
            case MAGENTA:
                return Material.MAGENTA_DYE;
            case LIGHT_BLUE:
                return Material.LIGHT_BLUE_DYE;
            case YELLOW:
                return Material.YELLOW_DYE;
            case LIME:
                return Material.LIME_DYE;
            case PINK:
                return Material.PINK_DYE;
            case GRAY:
                return Material.GRAY_DYE;
            case LIGHT_GRAY:
                return Material.LIGHT_GRAY_DYE;
            case CYAN:
                return Material.CYAN_DYE;
            case PURPLE:
                return Material.PURPLE_DYE;
            case BLUE:
                return Material.BLUE_DYE;
            case BROWN:
                return Material.BROWN_DYE;
            case GREEN:
                return Material.GREEN_DYE;
            case RED:
                return Material.RED_DYE;
            case BLACK:
                return Material.BLACK_DYE;
            default:
                throw new IllegalStateException("Unknown DyeColor: " + dyeColor);
        }
    }
}