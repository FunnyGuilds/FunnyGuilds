package net.dzikoysk.funnyguilds.shared.bukkit;

import dev.peri.yetanothermessageslibrary.message.Sendable;
import dev.peri.yetanothermessageslibrary.replace.StringReplacer;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.shared.adventure.ItemComponentHelper;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import panda.std.Option;
import panda.std.Pair;
import panda.std.stream.PandaStream;

import java.util.*;
import java.util.function.Function;

public final class ItemUtils {

    private ItemUtils() { }

    public static boolean playerHasEnoughItems(Player player, List<ItemStack> requiredItems, Function<MessageConfiguration, Sendable> messageSupplier) {
        for (ItemStack requiredItem : requiredItems) {
            if (player.getInventory().containsAtLeast(requiredItem, requiredItem.getAmount())) {
                continue;
            }

            FunnyGuilds.getInstance().getMessageService().getMessage(messageSupplier)
                    .receiver(player)
                    .with(ItemComponentHelper.prepareItemReplacement(requiredItem))
                    .with(ItemComponentHelper.prepareItemsReplacement(requiredItems))
                    .send();

            return false;
        }
        return true;
    }

    public static String translateTextPlaceholder(String text, Collection<ItemStack> items, ItemStack item) {
        return StringReplacer.replace(text,
                ItemComponentHelper.prepareItemReplacement(item),
                ItemComponentHelper.prepareItemsReplacement(items));
    }

    public static String itemAsString(ItemStack item, boolean displayAmount) {
        String materialName = MaterialUtils.getMaterialName(item.getType());
        return displayAmount
                ? item.getAmount() + FunnyGuilds.getInstance().getPluginConfiguration().itemAmountSuffix.getValue() + materialName
                : materialName;
    }

    public static String toString(@NotNull ItemStack item) {

        StringBuilder sb = new StringBuilder();
        sb.append(item.getAmount())
                .append(" ")
                .append(item.getType().name());

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable damageable) {
            sb.append(":").append(damageable.getDamage());
        } else {
            sb.append(":0");
        }

        if (meta != null) {
            if (meta.hasDisplayName()) {
                sb.append(" name:").append(LegacyComponentSerializer.legacyAmpersand().serialize(meta.displayName()));
            }

            List<Component> lore = meta.lore();
            if (lore != null && !lore.isEmpty()) {
                sb.append(" lore:");
                lore.forEach(line -> sb.append(LegacyComponentSerializer.legacyAmpersand().serialize(line)).append(","));
                sb.setLength(sb.length() - 1);
            }

            if (!meta.getEnchants().isEmpty()) {
                sb.append(" enchants:");
                meta.getEnchants().forEach((enchant, level) ->
                        sb.append(enchant.getKey().getKey()).append(":").append(level).append(","));
                sb.setLength(sb.length() - 1);
            }
        }

        return sb.toString();
    }

    public static ItemStack parseItem(String itemString) {
        String[] split = itemString.split(" ");
        String[] typeSplit = split[1].split(":");
        String subtype = typeSplit.length > 1 ? typeSplit[1] : "0";

        Material material = Material.matchMaterial(typeSplit[0]);
        int amount = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(split[0])).orElseGet(1);
        int data = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(subtype)).orElseGet(0);

        ItemBuilder itemBuilder = new ItemBuilder(material, amount, data);
        FunnyFormatter formatter = new FunnyFormatter()
                .register("_", " ")
                .register("{HASH}", "#");
        ItemMeta meta = itemBuilder.getMeta();

        for (int index = 2; index < split.length; index++) {
            String[] attrSplit = split[index].split(":", 2);
            if (attrSplit.length != 2) {
                FunnyGuilds.getPluginLogger().parser("Unknown item meta attribute: " + attrSplit[0]);
                continue;
            }

            String attrName = attrSplit[0].toLowerCase(Locale.ROOT);
            String attrValue = attrSplit[1];

            switch (attrName) {
                case "name", "displayname" -> meta.displayName(
                        LegacyComponentSerializer.legacyAmpersand().deserialize(formatter.replace(attrValue))
                );
                case "lore" -> {
                    List<Component> lore = new ArrayList<>();
                    for (String line : attrValue.split("#")) {
                        lore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(formatter.replace(line)));
                    }
                    meta.lore(lore);
                }
                case "enchant", "enchantment" -> {
                    Pair<Enchantment, Integer> ench = parseEnchant(attrValue);
                    if (ench.getFirst() != null) {
                        meta.addEnchant(ench.getFirst(), ench.getSecond(), true);
                    }
                }
                case "enchants", "enchantments" -> {
                    for (String e : attrValue.split(",")) {
                        Pair<Enchantment, Integer> ench = parseEnchant(e);
                        if (ench.getFirst() != null) {
                            meta.addEnchant(ench.getFirst(), ench.getSecond(), true);
                        }
                    }
                }
                case "skullowner" -> {
                    if (meta instanceof SkullMeta skullMeta) {
                        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(attrValue));
                        meta = skullMeta;
                    } else {
                        FunnyGuilds.getPluginLogger().parser("Invalid skull owner attribute, item is not a skull: " + split[index]);
                    }
                }
                case "flags", "itemflags" -> {
                    for (String f : attrValue.split(",")) {
                        matchItemFlag(f.trim()).peek(meta::addItemFlags);
                    }
                }
                case "armorcolor" -> {
                    if (meta instanceof LeatherArmorMeta leatherMeta) {
                        String[] rgb = attrValue.split("_");
                        try {
                            leatherMeta.setColor(Color.fromRGB(
                                    Integer.parseInt(rgb[0]),
                                    Integer.parseInt(rgb[1]),
                                    Integer.parseInt(rgb[2])
                            ));
                            meta = leatherMeta;
                        } catch (NumberFormatException ex) {
                            FunnyGuilds.getPluginLogger().parser("Invalid armor color: " + attrValue);
                        }
                    } else {
                        FunnyGuilds.getPluginLogger().parser("Invalid armor color attribute, item is not leather armor: " + split[index]);
                    }
                }
                default -> FunnyGuilds.getPluginLogger().parser("Unknown item meta attribute: " + attrName);
            }
        }

        itemBuilder.getItem().setItemMeta(meta);
        return itemBuilder.getItem();
    }

    public static List<ItemStack> parseItems(List<String> itemStrings) {
        return PandaStream.of(itemStrings)
                .map(ItemUtils::parseItem)
                .toList();
    }

    public static List<ItemStack> parseItems(String... itemStrings) {
        return parseItems(Arrays.asList(itemStrings));
    }

    public static int getItemAmount(ItemStack item, Inventory inv) {
        return PandaStream.of(inv.getContents())
                .filter(item::isSimilar)
                .toStream()
                .mapToInt(ItemStack::getAmount)
                .sum();
    }

    public static ItemStack[] toArray(List<ItemStack> items) {
        if (items == null || items.isEmpty()) {
            return new ItemStack[0];
        }
        return items.toArray(new ItemStack[0]);
    }

    private static Enchantment matchEnchant(String enchantName) {
        return RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT)
                .get(NamespacedKey.minecraft(enchantName.toLowerCase(Locale.ROOT)));
    }

    private static Pair<Enchantment, Integer> parseEnchant(String enchantString) {
        String[] split = enchantString.split(":");
        Enchantment enchant = matchEnchant(split[0]);
        int level = 1;
        if (split.length > 1) {
            level = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(split[1])).orElseGet(1);
        }
        if (enchant == null) {
            FunnyGuilds.getPluginLogger().parser("Unknown enchant: " + split[0]);
        }
        return Pair.of(enchant, level);
    }

    private static Option<ItemFlag> matchItemFlag(String flagName) {
        return Option.attempt(IllegalArgumentException.class,
                        () -> ItemFlag.valueOf(flagName.toUpperCase(Locale.ROOT)))
                .onEmpty(() -> FunnyGuilds.getPluginLogger().parser("Unknown item flag: " + flagName));
    }

}