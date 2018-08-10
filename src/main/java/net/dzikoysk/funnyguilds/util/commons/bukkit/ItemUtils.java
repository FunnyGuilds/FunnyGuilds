package net.dzikoysk.funnyguilds.util.commons.bukkit;

import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.nms.EggTypeChanger;
import net.dzikoysk.funnyguilds.util.nms.Reflections;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class ItemUtils {

    private static final Constructor<?> NBT_TAG_COMPOUND_CONSTRUCTOR;

    private static final Method AS_NMS_COPY;
    private static final Method SAVE;

    static {
        final Class<?> craftItemStack = Reflections.getCraftBukkitClass("inventory.CraftItemStack");
        AS_NMS_COPY = Reflections.getMethod(craftItemStack, "asNMSCopy", ItemStack.class);

        final Class<?> nmsItemStack = Reflections.getNMSClass("ItemStack");
        final Class<?> nbtTagCompound = Reflections.getNMSClass("NBTTagCompound");

        NBT_TAG_COMPOUND_CONSTRUCTOR = Reflections.getConstructor(nbtTagCompound);
        SAVE = Reflections.getMethod(nmsItemStack, "save", nbtTagCompound);
    }

    public static TextComponent translatePlaceholder(String message, List<ItemStack> items, ItemStack item) {
        TextComponent translatedMessage = new TextComponent();
        String messagePart = "";
        String messageColor = "";
        
        char[] messageChars = message.toCharArray();
        for (int i = 0; i < messageChars.length; i++) {
            char c = messageChars[i];
            if (c != '{') {
                messagePart += c;
                
                if (c == ChatColor.COLOR_CHAR) {
                    messageColor += c;
                    messageColor += messageChars[i + 1];
                }
                
                continue;
            }
            
            String subItem = message.substring(i, Math.min(message.length(), i + 6));
            if (subItem.equals("{ITEM}")) {
                for (BaseComponent extra : TextComponent.fromLegacyText(messagePart)) {
                    translatedMessage.addExtra(extra);
                }
                
                messagePart = "";
                
                translatedMessage.addExtra(getItemComponent(item, messageColor));
                
                i += 5;
                continue;
            }

            String subItems = message.substring(i, Math.min(message.length(), i + 7));
            if (subItems.equals("{ITEMS}")) {
                for (BaseComponent extra : TextComponent.fromLegacyText(messagePart)) {
                    translatedMessage.addExtra(extra);
                }

                messagePart = "";
                
                for (int itemNum = 0; itemNum < items.size(); itemNum++) {
                    translatedMessage.addExtra(getItemComponent(items.get(itemNum), messageColor));
                    
                    if (itemNum != items.size() - 1) {
                        for (BaseComponent extra : TextComponent.fromLegacyText(messageColor + ", ")) {
                            translatedMessage.addExtra(extra);
                        }
                    }
                }
                
                i += 6;
                continue;
            }

            messagePart += c;
        }
        
        for (BaseComponent extra : TextComponent.fromLegacyText(messagePart)) {
            translatedMessage.addExtra(extra);
        }
        
        return translatedMessage;
    }

    public static TextComponent getItemComponent(ItemStack item, String messageColor) {
        TextComponent itemComponent = new TextComponent();
        
        for (BaseComponent extra : TextComponent.fromLegacyText(messageColor + item.getAmount() + " " + item.getType().toString().toLowerCase())) {
            itemComponent.addExtra(extra);
        }
        
        try {
            String jsonItem = SAVE.invoke(AS_NMS_COPY.invoke(null, item), NBT_TAG_COMPOUND_CONSTRUCTOR.newInstance()).toString();
            itemComponent.setHoverEvent(new HoverEvent(Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(jsonItem)}));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        
        return itemComponent;
    }
    
    public static ItemStack parseItem(String string) {
        String[] split = string.split(" ");
        String[] typeSplit = split[1].split(":");
        String subtype = typeSplit.length > 1 ? typeSplit[1] : "0";

        Material mat = MaterialUtils.parseMaterial(typeSplit[0], false);

        int stack;
        int data;

        try {
            stack = Integer.parseInt(split[0]);
            data = Integer.parseInt(subtype);
        } catch (NumberFormatException e) {
            FunnyGuildsLogger.parser("Unknown size: " + split[0]);
            stack = 1;
            data = 0;
        }

        ItemBuilder item = new ItemBuilder(mat, stack, data);

        for (int i = 2; i < split.length; i++) {
            String str = split[i];

            if (str.contains("name")) {
                String[] splitName = str.split(":");
                item.setName(StringUtils.replace(
                                ChatUtils.colored(String.join(":", Arrays.copyOfRange(splitName, 1, splitName.length))), "_",
                                " "), true);
            } else if (str.contains("lore")) {
                String[] splitLore = str.split(":");
                String loreArgs = String.join(":", Arrays.copyOfRange(splitLore, 1, splitLore.length));
                String[] lores = loreArgs.split("#");
                List<String> lore = new ArrayList<>();

                for (String s : lores) {
                    lore.add(StringUtils.replace(StringUtils.replace(ChatUtils.colored(s), "_", " "), "{HASH}", "#"));
                }

                item.setLore(lore);
            } else if (str.contains("enchant")) {
                String[] parse = str.split(":");
                String enchantName = parse[1];
                int level;

                try {
                    level = Integer.parseInt(parse[2]);
                } catch (NumberFormatException e) {
                    FunnyGuildsLogger.parser("Unknown enchant level: " + split[2]);
                    level = 1;
                }

                Enchantment enchant = Enchantment.getByName(enchantName.toUpperCase());
                if (enchant == null) {
                    FunnyGuildsLogger.parser("Unknown enchant: " + parse[1]);
                }

                item.addEnchant(enchant, level);
            } else if (str.contains("skullowner")) {
                if (item.getMeta() instanceof SkullMeta) {
                    ((SkullMeta) item.getMeta()).setOwner(str.split(":")[1]);
                    item.refreshMeta();
                }
            } else if (str.contains("armorcolor")) {
                if (item.getMeta() instanceof LeatherArmorMeta) {
                    String[] color = str.split(":")[1].split("_");

                    try {
                        ((LeatherArmorMeta) item.getMeta()).setColor(Color.fromRGB(Integer.parseInt(color[0]),
                                        Integer.parseInt(color[1]), Integer.parseInt(color[2])));
                        item.refreshMeta();
                    } catch (NumberFormatException e) {
                        FunnyGuildsLogger.parser("Unknown armor color: " + str.split(":")[1]);
                    }
                }
            } else if (str.contains("eggtype")) {
                if (EggTypeChanger.needsSpawnEggMeta()) {
                    EntityType type = null;

                    try {
                        type = EntityType.valueOf(str.split(":")[1].toUpperCase());
                    } catch (Exception e) {
                        FunnyGuildsLogger.parser("Unknown entity type: " + str.split(":")[1].toUpperCase());
                    }

                    if (type != null) {
                        EggTypeChanger.applyChanges(item.getMeta(), type);
                        item.refreshMeta();
                    }
                } else {
                    FunnyGuildsLogger.info(
                                    "This MC version supports metadata for spawn egg type, no need to use eggtype in item creation!");
                }
            }
        }

        return item.getItem();
    }

    public static int getItemAmount(ItemStack item, Inventory inv) {
        int amount = 0;

        for (ItemStack is : inv.getContents()) {
            if (item.isSimilar(is)) {
                amount += is.getAmount();
            }
        }

        return amount;
    }

    public static ItemStack[] toArray(Collection<ItemStack> collection) {
        return collection.toArray(new ItemStack[0]);
    }

    private ItemUtils() {}

}
