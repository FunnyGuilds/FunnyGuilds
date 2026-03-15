package net.dzikoysk.funnyguilds.shared.bukkit;

import dev.peri.yetanothermessageslibrary.message.Sendable;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.shared.adventure.ComponentUtil;
import net.dzikoysk.funnyguilds.shared.adventure.ItemComponentHelper;
import net.dzikoysk.funnyguilds.shared.adventure.MiniLegacyHelper;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import panda.std.Option;
import panda.std.Pair;
import panda.std.stream.PandaStream;
import panda.utilities.text.Joiner;

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
                    Component coloredName = ComponentUtil.colored(attributeValue);
                    Component formattedName = formatter.replace(coloredName);
                    item.setName(formattedName);
                    continue;
                case "lore":
                    List<Component> lore = PandaStream.of(attributeValue.split("#"))
                            .map(ComponentUtil::colored)
                            .map(formatter::replace)
                            .toList();
                    item.setLore(lore);
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
                        FunnyGuilds.getPluginLogger().parser("Invalid item skull owner attribute (given item is not a skull!): " + split[index]);
                        continue;
                    }

                    ((SkullMeta) item.getMeta()).setOwner(attributeValue);
                    item.refreshMeta();
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
        short durability = item.getDurability();
        int amount = item.getAmount();

        StringBuilder itemString = new StringBuilder(amount + " " + material + (durability > 0 ? ":" + durability : ""));
        FunnyFormatter formatter = new FunnyFormatter().register(" ", "_").register("#", "{HASH}");

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return itemString.toString();
        }

        if (meta.hasDisplayName()) {
            Component replacedDisplayName = formatter.replace(meta.displayName());
            String displayName = MiniLegacyHelper.miniMessage().serialize(replacedDisplayName);
            itemString.append(" name:").append(displayName);
        }

        if (meta.hasLore()) {
            List<String> lore = PandaStream.of(meta.lore())
                    .map(line -> {
                        Component replacedLine = formatter.replace(line);
                        return MiniLegacyHelper.miniMessage().serialize(replacedLine);
                    })
                    .toList();

            itemString.append(" lore:").append(Joiner.on("#").join(lore));
        }

        if (meta.hasEnchants()) {
            List<String> enchants = PandaStream.of(meta.getEnchants().entrySet().stream())
                    .map(entry -> getEnchantName(entry.getKey()).toLowerCase(Locale.ROOT) + ":" + entry.getValue())
                    .toList();

            itemString.append(" enchants:").append(Joiner.on(",").join(enchants));
        }

        if (!meta.getItemFlags().isEmpty()) {
            List<String> flags = PandaStream.of(meta.getItemFlags())
                    .map(ItemFlag::name)
                    .map(name -> name.toLowerCase(Locale.ROOT))
                    .toList();

            itemString.append(" flags:").append(Joiner.on(",").join(flags));
        }

        if (meta instanceof SkullMeta skullMeta) {
            if (skullMeta.hasOwner()) {
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

    public static int getItemAmountByMaterial(Material material, Inventory inv) {
        int amount = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() == material) {
                amount += item.getAmount();
            }
        }
        return amount;
    }

    public static ItemStack[] toArray(Collection<ItemStack> collection) {
        return collection.toArray(new ItemStack[0]);
    }

    public static List<ItemStack> buildRequiredItems(
            net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet set,
            net.dzikoysk.funnyguilds.config.sections.items.ItemsConfiguration config
    ) {
        List<ItemStack> items = new java.util.ArrayList<>();
        if (!set.requirements.itemsEnabled) {
            return items;
        }
        for (java.util.Map.Entry<String, Integer> entry : set.getItems().entrySet()) {
            config.getLibraryItem(entry.getKey()).ifPresent(def -> {
                Material material = Material.matchMaterial(def.material);
                if (material != null) {
                    items.add(new ItemStack(material, entry.getValue()));
                }
            });
        }
        return items;
    }

}
