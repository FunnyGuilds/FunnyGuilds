package net.dzikoysk.funnyguilds.util.commons.bukkit;

import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.nms.EggTypeChanger;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class ItemUtils {

    public static String translateTextPlaceholder(String message, Collection<ItemStack> items, ItemStack item) {
        StringBuilder contentBuilder = new StringBuilder();

        if (message.contains("{ITEM}")) {
            contentBuilder.append(item.getAmount());
            contentBuilder.append(" ");
            contentBuilder.append(item.getType().toString().toLowerCase());

            message = StringUtils.replace(message, "{ITEM}", contentBuilder.toString());
        }

        if (message.contains("{ITEMS}")) {
            Collection<String> translatedItems = new ArrayList<>();

            for (ItemStack itemStack : items) {
                contentBuilder.setLength(0);

                contentBuilder.append(itemStack.getAmount());
                contentBuilder.append(" ");
                contentBuilder.append(itemStack.getType().toString().toLowerCase());

                translatedItems.add(contentBuilder.toString());
            }

            message = StringUtils.replace(message, "{ITEMS}", ChatUtils.toString(translatedItems, true));
        }

        return message;
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
                item.setName(StringUtils.replace(ChatUtils.colored(String.join(":", Arrays.copyOfRange(splitName, 1, splitName.length))), "_", " "), true);
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
                    FunnyGuildsLogger.info("This MC version supports metadata for spawn egg type, no need to use eggtype in item creation!");
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
