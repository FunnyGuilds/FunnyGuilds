package net.dzikoysk.funnyguilds.shared.bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.nms.EggTypeChanger;
import net.dzikoysk.funnyguilds.nms.Reflections;
import net.dzikoysk.funnyguilds.shared.spigot.ItemComponentUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import panda.std.Pair;
import panda.std.stream.PandaStream;
import panda.utilities.text.Joiner;

public final class ItemUtils {

    private static Method BY_IN_GAME_NAME_ENCHANT;
    private static Method CREATE_NAMESPACED_KEY;

    private static Method GET_IN_GAME_NAME_ENCHANT;
    private static Method GET_NAMESPACED_KEY;

    static {
        if (!Reflections.USE_PRE_12_METHODS) {
            Class<?> namespacedKeyClass = Reflections.getBukkitClass("NamespacedKey");

            BY_IN_GAME_NAME_ENCHANT = Reflections.getMethod(Enchantment.class, "getByKey");
            CREATE_NAMESPACED_KEY = Reflections.getMethod(namespacedKeyClass, "minecraft", String.class);

            GET_IN_GAME_NAME_ENCHANT = Reflections.getMethod(Enchantment.class, "getKey");
            GET_NAMESPACED_KEY = Reflections.getMethod(namespacedKeyClass, "getKey");
        }
    }

    private ItemUtils() {
    }

    public static boolean playerHasEnoughItems(Player player, List<ItemStack> requiredItems) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();

        for (ItemStack requiredItem : requiredItems) {
            if (player.getInventory().containsAtLeast(requiredItem, requiredItem.getAmount())) {
                continue;
            }

            if (messages.createItems.isEmpty()) {
                return false;
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

    public static ItemStack parseItem(String itemString) {
        String[] split = itemString.split(" ");
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
            String[] itemAttribute = split[index].split(":", 2);

            String attributeName = itemAttribute[0];
            String attributeValue = itemAttribute[1];

            switch (attributeName.toLowerCase()) {
                case "name":
                case "displayname":
                    item.setName(attributeValue.replace("_", " "), true);
                    continue;
                case "lore":
                    String[] lores = String.join(":", attributeValue).split("#");

                    List<String> lore = PandaStream.of(lores)
                            .map(line -> line.replace("_", " "))
                            .map(line -> line.replace("{HASH}", "#"))
                            .toList();

                    item.setLore(lore, true);
                    continue;
                case "enchant":
                case "enchantment":
                    Pair<Enchantment, Integer> parsedEnchant = parseEnchant(attributeValue);
                    item.addEnchant(parsedEnchant.getFirst(), parsedEnchant.getSecond());
                    continue;
                case "enchants":
                case "enchantments":
                    PandaStream.of(attributeValue.split(","))
                            .map(ItemUtils::parseEnchant)
                            .filter(enchant -> enchant.getFirst() != null)
                            .forEach(enchant -> item.addEnchant(enchant.getFirst(), enchant.getSecond()));
                    continue;
                case "skullowner":
                    if (item.getMeta() instanceof SkullMeta) {
                        ((SkullMeta) item.getMeta()).setOwner(attributeValue);
                        item.refreshMeta();
                    }
                    continue;
                case "flags":
                case "itemflags":
                    String[] flags = attributeValue.split(",");

                    for (String flag : flags) {
                        flag = flag.trim();

                        ItemFlag matchedFlag = matchItemFlag(flag);
                        if (matchedFlag == null) {
                            FunnyGuilds.getPluginLogger().parser("Unknown item flag: " + flag);
                            continue;
                        }

                        item.setFlag(matchedFlag);
                    }

                    continue;
                case "armorcolor":
                    if (!(item.getMeta() instanceof LeatherArmorMeta)) {
                        FunnyGuilds.getPluginLogger().parser("Invalid item armor color attribute (given item is not a leather armor!): " + split[index]);
                        continue;
                    }

                    String[] colorSplit = attributeValue.split("_");

                    try {
                        Color color = Color.fromRGB(Integer.parseInt(colorSplit[0]), Integer.parseInt(colorSplit[1]), Integer.parseInt(colorSplit[2]));
                        ((LeatherArmorMeta) item.getMeta()).setColor(color);
                        item.refreshMeta();
                    }
                    catch (NumberFormatException numberFormatException) {
                        FunnyGuilds.getPluginLogger().parser("Invalid armor color: " + attributeValue);
                    }

                    continue;
                case "eggtype":
                    if (!EggTypeChanger.needsSpawnEggMeta()) {
                        FunnyGuilds.getPluginLogger().info("This MC version supports metadata for spawnGuildHeart egg type, no need to use eggtype in item creation!");
                        continue;
                    }

                    EntityType type = null;
                    String entityTypeName = attributeValue.toUpperCase();

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

                    continue;
            }
        }

        return item.getItem();
    }

    public static List<ItemStack> parseItems(List<String> itemStrings) {
        return PandaStream.of(itemStrings)
                .map(ItemUtils::parseItem)
                .toList();
    }

    public static List<ItemStack> parseItems(String... itemStrings) {
        return parseItems(Arrays.asList(itemStrings));
    }

    public static String toString(ItemStack item) {
        String material = item.getType().toString().toLowerCase();
        short durability = item.getDurability();
        int amount = item.getAmount();

        StringBuilder itemString = new StringBuilder(amount + " " + material + (durability > 0 ? ":" + durability : ""));

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return itemString.toString();
        }

        if (meta.hasDisplayName()) {
            itemString.append(" name:").append(ChatUtils.decolor(meta.getDisplayName()).replace(" ", "_"));
        }

        if (meta.hasLore()) {
            List<String> lore = PandaStream.of(meta.getLore())
                    .map(ChatUtils::decolor)
                    .map(line -> line.replace(" ", "_"))
                    .map(line -> line.replace("#", "{HASH}"))
                    .toList();

            itemString.append(" lore:").append(Joiner.on("#").join(lore));
        }

        if (meta.hasEnchants()) {
            List<String> enchants = PandaStream.of(meta.getEnchants().entrySet())
                    .map(entry -> getEnchantName(entry.getKey()).toLowerCase() + ":" + entry.getValue())
                    .toList();

            itemString.append(" enchants:").append(Joiner.on(",").join(enchants));
        }

        if (meta.getItemFlags().size() > 0) {
            List<String> flags = PandaStream.of(meta.getItemFlags())
                    .map(ItemFlag::name)
                    .map(String::toLowerCase)
                    .toList();

            itemString.append(" flags:").append(Joiner.on(",").join(flags));
        }

        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            if (skullMeta.hasOwner()) {
                itemString.append(" skullowner:").append(skullMeta.getOwner());
            }
        }

        if (meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
            Color color = armorMeta.getColor();

            String colorString = color.getRed() + "_" + color.getGreen() + "_" + color.getBlue();

            itemString.append(" armorcolor:").append(colorString);
        }

        if (EggTypeChanger.needsSpawnEggMeta()) {
            if (meta instanceof SpawnEggMeta) {
                SpawnEggMeta eggMeta = (SpawnEggMeta) meta;

                String entityType = eggMeta.getSpawnedType().name().toLowerCase();

                itemString.append(" eggtype:").append(entityType);
            }
        }

        return itemString.toString();
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

    private static String getEnchantName(Enchantment enchantment) {
        if (GET_IN_GAME_NAME_ENCHANT != null && GET_NAMESPACED_KEY != null) {
            try {
                Object enchantmentName = GET_IN_GAME_NAME_ENCHANT.invoke(enchantment);
                Object namespacedKey = GET_NAMESPACED_KEY.invoke(enchantmentName);

                if (namespacedKey != null) {
                    return (String) namespacedKey;
                }
            }
            catch (InvocationTargetException | IllegalAccessException ignored) {
            }
        }
        return enchantment.getName();
    }

    private static Pair<Enchantment, Integer> parseEnchant(String enchantString) {
        String[] split = enchantString.split(":");

        Enchantment enchant = matchEnchant(split[0]);
        if (enchant == null) {
            FunnyGuilds.getPluginLogger().parser("Unknown enchant: " + split[0]);
        }

        int level;
        try {
            level = Integer.parseInt(split[1]);
        }
        catch (NumberFormatException numberFormatException) {
            FunnyGuilds.getPluginLogger().parser("Unknown enchant level: " + split[1]);
            level = 1;
        }

        return Pair.of(enchant, level);
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
