package net.dzikoysk.funnyguilds.shared.bukkit;

import dev.peri.yetanothermessageslibrary.message.Sendable;
import dev.peri.yetanothermessageslibrary.replace.StringReplacer;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.shared.adventure.ItemComponentHelper;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import panda.std.Option;
import panda.std.Pair;
import panda.std.stream.PandaStream;

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
        return StringReplacer.replace(
                text,
                ItemComponentHelper.prepareItemReplacement(item),
                ItemComponentHelper.prepareItemsReplacement(items)
        );
    }

    public static String itemAsString(ItemStack item, boolean displayAmount) {
        String materialName = MaterialUtils.getMaterialName(item.getType());
        if (!displayAmount) {
            return materialName;
        }
        return item.getAmount() + FunnyGuilds.getInstance().getPluginConfiguration().itemAmountSuffix.getValue() + materialName;
    }

    public static ItemStack parseItem(String itemString) {
        String[] split = itemString.split(" ");
        String[] typeSplit = split[1].split(":");
        String subtype = typeSplit.length > 1 ? typeSplit[1] : "0";

        Material material = Material.matchMaterial(typeSplit[0]);
        Option<Integer> amount = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(split[0])).onEmpty(() -> {
            FunnyGuilds.getPluginLogger().parser("Unknown amount: " + split[0]);
        });

        Option<Integer> data = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(subtype)).onEmpty(() -> {
            FunnyGuilds.getPluginLogger().parser("Unknown data: " + subtype);
        });

        ItemBuilder item = new ItemBuilder(material, amount.orElseGet(1), data.orElseGet(0));
        FunnyFormatter formatter = new FunnyFormatter().register("_", " ").register("{HASH}", "#");

        for (int index = 2; index < split.length; index++) {
            String[] itemAttributes = split[index].split(":", 2);

            if (itemAttributes.length != 2) {
                FunnyGuilds.getPluginLogger().parser("Unknown item meta attribute: " + itemAttributes[0]);
                continue;
            }

            String attributeName = itemAttributes[0];
            String attributeValue = itemAttributes[1];

            switch (attributeName.toLowerCase(Locale.ROOT)) {
                case "name":
                case "displayname":
                    item.setName(formatter.replace(attributeValue), true);
                    continue;
                case "lore":
                    List<String> lore = PandaStream.of(attributeValue.split("#")).map(formatter::replace).toList();
                    item.setLore(lore, true);
                    continue;
                case "enchant":
                case "enchantment":
                    Pair<Enchantment, Integer> parsedEnchant = parseEnchant(attributeValue);
                    if (parsedEnchant.getFirst() == null) {
                        continue;
                    }

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
                    if (!(item.getMeta() instanceof SkullMeta)) {
                        FunnyGuilds.getPluginLogger().parser(
                                "Invalid item skull owner attribute (given item is not a skull!): " + split[index]
                        );
                        continue;
                    }
                    item.setSkullOwner(attributeValue);

                    continue;
                case "flags":
                case "itemflags":
                    PandaStream.of(attributeValue.split(","))
                            .map(String::trim)
                            .mapOpt(ItemUtils::matchItemFlag)
                            .forEach(item::setFlag);

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
            }
        }

        return item.getItem();
    }

    public static List<ItemStack> parseItems(List<String> itemStrings) {
        return PandaStream.of(itemStrings).map(ItemUtils::parseItem).toList();
    }

    public static List<ItemStack> parseItems(String... itemStrings) {
        return parseItems(Arrays.asList(itemStrings));
    }

    public static String toString(ItemStack item) {
        String material = item.getType().toString().toLowerCase(Locale.ROOT);
        int durability = 0;

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable damageable) {
            durability = damageable.getDamage();
        }

        int amount = item.getAmount();
        StringBuilder itemString = new StringBuilder(amount + " " + material + (durability > 0 ? ":" + durability : ""));
        FunnyFormatter formatter = new FunnyFormatter().register(" ", "_").register("#", "{HASH}");

        if (meta == null) {
            return itemString.toString();
        }

        Component displayName = meta.displayName();
        if (displayName != null) {
            String name = LegacyComponentSerializer.legacySection().serialize(displayName);
            itemString.append(" name:").append(formatter.replace(ChatUtils.decolor(name)));
        }

        List<Component> loreComponents = meta.lore();
        if (loreComponents != null && !loreComponents.isEmpty()) {
            List<String> lore = loreComponents.stream()
                    .map(component -> ChatUtils.decolor(LegacyComponentSerializer.legacySection().serialize(component)))
                    .map(formatter::replace)
                    .collect(Collectors.toList());

            itemString.append(" lore:").append(String.join("#", lore));
        }

        if (!meta.getEnchants().isEmpty()) {
            List<String> enchants = meta.getEnchants().entrySet().stream()
                    .map(entry -> getEnchantName(entry.getKey()).toLowerCase(Locale.ROOT) + ":" + entry.getValue())
                    .collect(Collectors.toList());

            itemString.append(" enchants:").append(String.join(",", enchants));
        }

        if (!meta.getItemFlags().isEmpty()) {
            List<String> flags = meta.getItemFlags().stream()
                    .map(ItemFlag::name)
                    .map(name -> name.toLowerCase(Locale.ROOT))
                    .collect(Collectors.toList());

            itemString.append(" flags:").append(String.join(",", flags));
        }

        if (meta instanceof SkullMeta skullMeta) {
            if (skullMeta.getOwningPlayer() != null) {
                itemString.append(" skullowner:").append(skullMeta.getOwningPlayer().getName());
            }
        }

        if (meta instanceof LeatherArmorMeta armorMeta) {
            Color color = armorMeta.getColor();
            String colorString = color.getRed() + "_" + color.getGreen() + "_" + color.getBlue();
            itemString.append(" armorcolor:").append(colorString);
        }

        return itemString.toString();
    }

    private static Enchantment matchEnchant(String enchantName) {
        Registry<Enchantment> enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        return enchantmentRegistry.get(NamespacedKey.minecraft(enchantName.toLowerCase(Locale.ROOT)));
    }

    private static String getEnchantName(Enchantment enchantment) {
        return enchantment.getKey().getKey();
    }

    private static Pair<Enchantment, Integer> parseEnchant(String enchantString) {
        String[] split = enchantString.split(":");

        Enchantment enchant = matchEnchant(split[0]);
        if (enchant == null) {
            FunnyGuilds.getPluginLogger().parser("Unknown enchant: " + split[0]);
        }

        Option<Integer> level = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(split[1]));
        if (level.isEmpty()) {
            FunnyGuilds.getPluginLogger().parser("Unknown enchant level: " + split[1]);
        }

        return Pair.of(enchant, level.orElseGet(1));
    }

    private static Option<ItemFlag> matchItemFlag(String flagName) {
        return Option.attempt(IllegalArgumentException.class, () -> {
            return ItemFlag.valueOf(flagName.toUpperCase(Locale.ROOT));
        }).onEmpty(() -> {
            FunnyGuilds.getPluginLogger().parser("Unknown item flag: " + flagName);
        });
    }

    public static int getItemAmount(ItemStack item, Inventory inv) {
        return PandaStream.of(inv.getContents())
                .filter(item::isSimilar)
                .toStream()
                .mapToInt(ItemStack::getAmount)
                .sum();
    }

    public static ItemStack[] toArray(Collection<ItemStack> collection) {
        return collection.toArray(new ItemStack[0]);
    }

}
