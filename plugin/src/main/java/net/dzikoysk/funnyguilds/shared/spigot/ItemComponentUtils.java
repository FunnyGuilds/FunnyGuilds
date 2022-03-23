package net.dzikoysk.funnyguilds.shared.spigot;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.nms.Reflections;
import net.dzikoysk.funnyguilds.shared.bukkit.MaterialUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

public final class ItemComponentUtils {

    private static final Constructor<?> NBT_TAG_COMPOUND_CONSTRUCTOR;

    private static final Method AS_NMS_COPY;
    private static final Method SAVE;

    static {
        Class<?> craftItemStack = Reflections.getCraftBukkitClass("inventory.CraftItemStack");
        AS_NMS_COPY = Reflections.getMethod(craftItemStack, "asNMSCopy", ItemStack.class);

        Class<?> nmsItemStack = Reflections.getNMSClass("ItemStack");
        Class<?> nbtTagCompound = Reflections.getNMSClass("NBTTagCompound");

        NBT_TAG_COMPOUND_CONSTRUCTOR = Reflections.getConstructor(nbtTagCompound);
        SAVE = Reflections.getMethod(nmsItemStack, "save", nbtTagCompound);
    }

    private ItemComponentUtils() {
    }

    public static TextComponent translateComponentPlaceholder(String message, List<ItemStack> items, ItemStack item) {
        TextComponent translatedMessage = new TextComponent();
        StringBuilder messagePart = new StringBuilder();
        String messageColor = "";
        char[] messageChars = message.toCharArray();

        for (int index = 0; index < messageChars.length; index++) {
            char symbol = messageChars[index];

            if (symbol != '{') {
                messagePart.append(symbol);

                if (symbol == ChatColor.COLOR_CHAR) {
                    messageColor += symbol;

                    if (index + 1 >= messageChars.length) {
                        FunnyGuilds.getPluginLogger().warning("Invalid placeholder: " + message + " (exceeds array limit at + " + index + ")");
                        continue;
                    }

                    messageColor += messageChars[index + 1];
                }

                continue;
            }

            String subItem = message.substring(index, Math.min(message.length(), index + 6));

            if (subItem.equals("{ITEM}")) {
                for (BaseComponent extra : TextComponent.fromLegacyText(messagePart.toString())) {
                    translatedMessage.addExtra(extra);
                }

                messagePart = new StringBuilder();
                translatedMessage.addExtra(getItemComponent(item, messageColor));
                index += 5;
                continue;
            }

            String subItems = message.substring(index, Math.min(message.length(), index + 7));

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

                index += 6;
                continue;
            }

            messagePart.append(symbol);
        }

        for (BaseComponent extra : TextComponent.fromLegacyText(messagePart.toString())) {
            translatedMessage.addExtra(extra);
        }

        return translatedMessage;
    }

    public static TextComponent getItemComponent(ItemStack item, String messageColor) {
        TextComponent itemComponent = new TextComponent();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        for (BaseComponent extra : TextComponent.fromLegacyText(messageColor + item.getAmount() + config.itemAmountSuffix + " " + MaterialUtils.getMaterialName(item.getType()))) {
            itemComponent.addExtra(extra);
        }

        try {
            String jsonItem = SAVE.invoke(AS_NMS_COPY.invoke(null, item), NBT_TAG_COMPOUND_CONSTRUCTOR.newInstance()).toString();
            itemComponent.setHoverEvent(new HoverEvent(Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(jsonItem)}));
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException exception) {
            FunnyGuilds.getPluginLogger().error("Could not get item component", exception);
        }

        return itemComponent;
    }

}
