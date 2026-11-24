package net.dzikoysk.funnyguilds.shared.bukkit;

import dev.peri.yetanothermessageslibrary.message.Sendable;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet;
import net.dzikoysk.funnyguilds.config.sections.items.ItemsConfiguration;
import net.dzikoysk.funnyguilds.shared.Optionals;
import net.dzikoysk.funnyguilds.shared.adventure.ComponentUtil;
import net.dzikoysk.funnyguilds.shared.adventure.ItemComponentHelper;
import net.dzikoysk.funnyguilds.shared.adventure.MiniLegacyHelper;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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
import panda.std.Pair;
import panda.utilities.text.Joiner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        Material material = MaterialUtils.matchMaterial(typeSplit[0]).orElse(null);
        Optional<Integer> amount = Optionals.attempt(NumberFormatException.class, () -> Integer.parseInt(split[0]));
        if (amount.isEmpty()) {
            FunnyGuilds.getPluginLogger().parser("Unknown amount: " + split[0]);
        }

        Optional<Integer> data = Optionals.attempt(NumberFormatException.class, () -> Integer.parseInt(subtype));
        if (data.isEmpty()) {
            FunnyGuilds.getPluginLogger().parser("Unknown data: " + subtype);
        }

        ItemBuilder item = new ItemBuilder(material, amount.orElse(1), data.orElse(0));
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
                    Component coloredName = coloredWithoutItalics(attributeValue);
                    Component formattedName = formatter.replace(coloredName);
                    item.setName(formattedName);
                    continue;
                case "lore":
                    List<Component> lore = Arrays.stream(attributeValue.split("#"))
                            .map(ItemUtils::coloredWithoutItalics)
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
                    Arrays.stream(attributeValue.split(","))
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
                    Arrays.stream(attributeValue.split(","))
                            .map(String::trim)
                            .map(ItemUtils::matchItemFlag)
                            .flatMap(Optional::stream)
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
        return itemStrings.stream().map(ItemUtils::parseItem).collect(Collectors.toList());
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
            List<String> lore = meta.lore().stream()
                    .map(line -> {
                        Component replacedLine = formatter.replace(line);
                        return MiniLegacyHelper.miniMessage().serialize(replacedLine);
                    })
                    .toList();

            itemString.append(" lore:").append(Joiner.on("#").join(lore));
        }

        if (meta.hasEnchants()) {
            List<String> enchants = meta.getEnchants().entrySet().stream()
                    .map(entry -> getEnchantName(entry.getKey()).toLowerCase(Locale.ROOT) + ":" + entry.getValue())
                    .toList();

            itemString.append(" enchants:").append(Joiner.on(",").join(enchants));
        }

        if (!meta.getItemFlags().isEmpty()) {
            List<String> flags = meta.getItemFlags().stream()
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

    static Enchantment matchEnchant(String enchantName) {
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

        Optional<Integer> level = Optionals.attempt(NumberFormatException.class, () -> Integer.parseInt(split[1]));
        if (level.isEmpty()) {
            FunnyGuilds.getPluginLogger().parser("Unknown enchant level: " + split[1]);
        }

        return Pair.of(enchant, level.orElse(1));
    }

    static Optional<ItemFlag> matchItemFlag(String flagName) {
        Optional<ItemFlag> itemFlag = Optionals.attempt(IllegalArgumentException.class, () -> {
            return ItemFlag.valueOf(flagName.toUpperCase(Locale.ROOT));
        });

        if (itemFlag.isEmpty()) {
            FunnyGuilds.getPluginLogger().parser("Unknown item flag: " + flagName);
        }

        return itemFlag;
    }

    public static int getItemAmount(ItemStack item, Inventory inv) {
        return Arrays.stream(inv.getContents())
                .filter(item::isSimilar)
                .mapToInt(ItemStack::getAmount)
                .sum();
    }

    public static ItemStack[] toArray(Collection<ItemStack> collection) {
        return collection.toArray(new ItemStack[0]);
    }

    public static List<ItemStack> buildRequiredItems(GuildItemSet set, ItemsConfiguration config) {
        List<ItemStack> items = new ArrayList<>();
        if (!set.requirements.itemsEnabled) {
            return items;
        }
        for (Map.Entry<String, Integer> entry : set.getItems().entrySet()) {
            config.getLibraryItem(entry.getKey())
                    .ifPresent(def -> items.add(toItemStack(def, entry.getValue())));
        }
        return items;
    }

    public static ItemStack toItemStack(GuildItemDefinition def, int amount) {
        if (def == null) return new ItemStack(Material.STONE, amount);

        return def.getResolved().toItemStack(amount);
    }

    /** Parses item text with italics disabled unless explicitly configured. */
    public static Component coloredWithoutItalics(String text) {
        // Merge inherited styles into text nodes so Bukkit does not insert legacy resets between them.
        return ComponentUtil.colored(text)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .compact();
    }

}
