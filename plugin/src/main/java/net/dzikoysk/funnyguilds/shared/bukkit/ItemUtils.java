package net.dzikoysk.funnyguilds.shared.bukkit;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.shared.spigot.ItemComponentUtils;
import net.dzikoysk.funnyguilds.nms.EggTypeChanger;
import net.dzikoysk.funnyguilds.nms.Reflections;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class ItemUtils {

    private static Method BY_IN_GAME_NAME_ENCHANT;
    private static Method CREATE_NAMESPACED_KEY;

    static {
        if (! Reflections.USE_PRE_12_METHODS) {
            Class<?> namespacedKeyClass = Reflections.getBukkitClass("NamespacedKey");

            BY_IN_GAME_NAME_ENCHANT = Reflections.getMethod(Enchantment.class, "getByKey");
            CREATE_NAMESPACED_KEY = Reflections.getMethod(namespacedKeyClass, "minecraft", String.class);
        }
    }

    private ItemUtils() {}

    public static boolean playerHasEnoughItems(Player player, List<ItemStack> requiredItems) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();

        for (ItemStack requiredItem : requiredItems) {
            if (player.getInventory().containsAtLeast(requiredItem, requiredItem.getAmount())) {
                continue;
            }

            if (config.enableItemComponent) {
                player.spigot().sendMessage(ItemComponentUtils.translateComponentPlaceholder(messages.createItems, requiredItems, requiredItem));
            }
            else {
                player.sendMessage(ItemUtils.translateTextPlaceholder(messages.createItems, requiredItems, requiredItem));
            }

            return false;
        }

        return true;
    }

    public static String translateTextPlaceholder(String message, Collection<ItemStack> items, ItemStack item) {
        StringBuilder contentBuilder = new StringBuilder();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        if (message.contains("{ITEM}")) {
            contentBuilder.append(item.getAmount());
            contentBuilder.append(config.itemAmountSuffix + " ");
            contentBuilder.append(MaterialUtils.getMaterialName(item.getType()));

            message = StringUtils.replace(message, "{ITEM}", contentBuilder.toString());
        }

        if (message.contains("{ITEMS}")) {
            Collection<String> translatedItems = new ArrayList<>();

            for (ItemStack itemStack : items) {
                contentBuilder.setLength(0);

                contentBuilder.append(itemStack.getAmount());
                contentBuilder.append(config.itemAmountSuffix + " ");
                contentBuilder.append(MaterialUtils.getMaterialName(itemStack.getType()));

                translatedItems.add(contentBuilder.toString());
            }

            message = StringUtils.replace(message, "{ITEMS}", ChatUtils.toString(translatedItems, true));
        }

        if (message.contains("{ITEM-NO-AMOUNT}")) {
            contentBuilder.setLength(0);
            contentBuilder.append(MaterialUtils.getMaterialName(item.getType()));

            message = StringUtils.replace(message, "{ITEM-NO-AMOUNT}", contentBuilder.toString());
        }

        return message;
    }

    public static ItemStack parseItem(String string) {
        String[] split = string.split(" ");
        String[] typeSplit = split[1].split(":");
        String subtype = typeSplit.length > 1 ? typeSplit[1] : "0";

        Material material = MaterialUtils.parseMaterial(typeSplit[0], false);

        int stack;
        int data;

        try {
            stack = Integer.parseInt(split[0]);
            data = Integer.parseInt(subtype);
        }
        catch (NumberFormatException e) {
            FunnyGuilds.getPluginLogger().parser("Unknown size: " + split[0]);
            stack = 1;
            data = 0;
        }

        ItemBuilder item = new ItemBuilder(material, stack, data);

        for (int index = 2; index < split.length; index++) {
            String[] itemAttribute = split[index].split(":");

            String attributeName = itemAttribute[0];
            String[] attributeValue = Arrays.copyOfRange(itemAttribute, 1, itemAttribute.length);

            if (attributeName.equalsIgnoreCase("name")) {
                item.setName(StringUtils.replace(ChatUtils.colored(String.join(":", attributeValue)), "_", " "), true);
            }
            else if (attributeName.equalsIgnoreCase("lore")) {
                String[] lores = String.join(":", attributeValue).split("#");
                List<String> lore = new ArrayList<>();

                for (String loreLine : lores) {
                    lore.add(StringUtils.replace(StringUtils.replace(ChatUtils.colored(loreLine), "_", " "), "{HASH}", "#"));
                }

                item.setLore(lore);
            }
            else if (attributeName.equalsIgnoreCase("enchant")) {
                int level;

                try {
                    level = Integer.parseInt(attributeValue[1]);
                }
                catch (NumberFormatException numberFormatException) {
                    FunnyGuilds.getPluginLogger().parser("Unknown enchant level: " + attributeValue[1]);
                    level = 1;
                }

                Enchantment enchant = matchEnchant(attributeValue[0]);

                if (enchant == null) {
                    FunnyGuilds.getPluginLogger().parser("Unknown enchant: " + attributeValue[0]);
                    continue;
                }

                item.addEnchant(enchant, level);
            }
            else if (attributeName.equalsIgnoreCase("skullowner")) {
                if (item.getMeta() instanceof SkullMeta) {
                    ((SkullMeta) item.getMeta()).setOwner(attributeValue[0]);
                    item.refreshMeta();
                }
            }
            else if (attributeName.equalsIgnoreCase("flags")) {
                String[] flags = attributeValue[0].split(",");

                for (String flag : flags) {
                    flag = flag.trim();
                    ItemFlag matchedFlag = matchItemFlag(flag);

                    if (matchedFlag == null) {
                        FunnyGuilds.getPluginLogger().parser("Unknown item flag: " + flag);
                        continue;
                    }

                    item.setFlag(matchedFlag);
                }

            }
            else if (attributeName.equalsIgnoreCase("armorcolor")) {
                if (! (item.getMeta() instanceof LeatherArmorMeta)) {
                    FunnyGuilds.getPluginLogger().parser("Invalid item armor color attribute (given item is not a leather armor!): " + split[index]);
                    continue;
                }

                String[] color = attributeValue[0].split("_");

                try {
                    ((LeatherArmorMeta) item.getMeta()).setColor(Color.fromRGB(Integer.parseInt(color[0]),
                            Integer.parseInt(color[1]), Integer.parseInt(color[2])));
                    item.refreshMeta();
                }
                catch (NumberFormatException numberFormatException) {
                    FunnyGuilds.getPluginLogger().parser("Invalid armor color: " + Arrays.toString(attributeValue));
                }
            }
            else if (attributeName.equalsIgnoreCase("eggtype")) {
                if (EggTypeChanger.needsSpawnEggMeta()) {
                    EntityType type = null;
                    String entityTypeName = attributeValue[0].toUpperCase();

                    try {
                        type = EntityType.valueOf(entityTypeName);
                    }
                    catch (Exception exception) {
                        FunnyGuilds.getPluginLogger().parser("Unknown entity type: " + entityTypeName);
                    }

                    if (type != null) {
                        EggTypeChanger.applyChanges(item.getMeta(), type);
                        item.refreshMeta();
                    }
                }
                else {
                    FunnyGuilds.getPluginLogger().info("This MC version supports metadata for spawnGuildHeart egg type, no need to use eggtype in item creation!");
                }
            }
        }

        return item.getItem();
    }

    private static Enchantment matchEnchant(String enchantName) {
        if (BY_IN_GAME_NAME_ENCHANT != null && CREATE_NAMESPACED_KEY != null) {
            try {
                Object namespacedKey = CREATE_NAMESPACED_KEY.invoke(null, enchantName.toLowerCase());
                Object enchantment = BY_IN_GAME_NAME_ENCHANT.invoke(null, namespacedKey);

                if (enchantment != null) {
                    return (Enchantment) enchantment;
                }
            }
            catch (IllegalAccessException | InvocationTargetException ignored) {
            }
        }

        return Enchantment.getByName(enchantName.toUpperCase());
    }

    private static ItemFlag matchItemFlag(String flagName) {
        for (ItemFlag flag : ItemFlag.values()) {
            if (flag.name().equalsIgnoreCase(flagName)) {
                return flag;
            }
        }

        return null;
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

}
