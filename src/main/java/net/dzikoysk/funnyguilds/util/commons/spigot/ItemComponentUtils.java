package net.dzikoysk.funnyguilds.util.commons.spigot;

import net.dzikoysk.funnyguilds.util.nms.Reflections;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public final class ItemComponentUtils {

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
    
    public static TextComponent translateComponentPlaceholder(String message, List<ItemStack> items, ItemStack item) {
        TextComponent translatedMessage = new TextComponent();
        StringBuilder messagePart = new StringBuilder();
        String messageColor = "";
        
        char[] messageChars = message.toCharArray();
        for (int i = 0; i < messageChars.length; i++) {
            char c = messageChars[i];
            if (c != '{') {
                messagePart.append(c);
                
                if (c == ChatColor.COLOR_CHAR) {
                    messageColor += c;
                    messageColor += messageChars[i + 1];
                }
                
                continue;
            }
            
            String subItem = message.substring(i, Math.min(message.length(), i + 6));
            if (subItem.equals("{ITEM}")) {
                for (BaseComponent extra : TextComponent.fromLegacyText(messagePart.toString())) {
                    translatedMessage.addExtra(extra);
                }
                
                messagePart = new StringBuilder();
                
                translatedMessage.addExtra(getItemComponent(item, messageColor));
                
                i += 5;
                continue;
            }

            String subItems = message.substring(i, Math.min(message.length(), i + 7));
            if (subItems.equals("{ITEMS}")) {
                for (BaseComponent extra : TextComponent.fromLegacyText(messagePart.toString())) {
                    translatedMessage.addExtra(extra);
                }

                messagePart = new StringBuilder();
                
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

            messagePart.append(c);
        }
        
        for (BaseComponent extra : TextComponent.fromLegacyText(messagePart.toString())) {
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
    
    private ItemComponentUtils() {}
    
}
