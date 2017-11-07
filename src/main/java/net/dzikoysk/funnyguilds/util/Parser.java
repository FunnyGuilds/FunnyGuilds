package net.dzikoysk.funnyguilds.util;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Parser {

//    @Deprecated
//    public static ItemStack parseItem(String string) {
//        Integer amount = Integer.parseInt(string.substring(0, string.indexOf(' ')));
//        String type = string.substring(string.indexOf(' ') + 1);
//
//        type = type.toUpperCase();
//        type = type.replaceAll(" ", "_");
//        Material material = Material.getMaterial(type);
//        ItemStack itemstack;
//
//        if (material == null) {
//            if (type.equalsIgnoreCase("Enchanted_Golden_Apple")) {
//                itemstack = new ItemStack(322, 1);
//            } else {
//                FunnyGuilds.parser("Unknown item: " + string);
//                return new ItemStack(Material.AIR);
//            }
//        } else {
//            itemstack = new ItemStack(material);
//        }
//
//        itemstack.setAmount(amount);
//        return itemstack;
//    }

    public static ItemStack parseItem(String string) {
        String[] split = string.split(" ");
        String[] typeSplit = split[1].split(":");
        String type = typeSplit[0];
        String subtype = typeSplit.length > 1 ? typeSplit[1] : "0";
        type = type.toUpperCase();
        type = type.replaceAll(" ", "_");
        Material mat = Material.matchMaterial(type);
        int stack;
        int data;

        try {
            stack = Integer.parseInt(split[0]);
            data = Integer.parseInt(subtype);
        }
        catch (NumberFormatException ex) {
            FunnyGuilds.parser("Unknown size: " + split[0]);
            stack = 1;
            data = 0;
        }
        if (mat == null) {
            FunnyGuilds.parser("Unknown item: " + type);
        }

        ItemBuilder item = new ItemBuilder(mat, stack, data);

        for (int i = 2; i < split.length; i++) {
            String str = split[i];

            if (str.contains("name")) {
                String[] split0 = str.split(":");
                String itemName = StringUtils.replace(split0[1], "_", " ");
                item.setName(itemName, true);
            }
            else if (str.contains("lore")) {
                String[] split0 = str.split(":");
                String loreArgs = String.join("", Arrays.copyOfRange(split0, 1, split0.length));
                String[] lores = loreArgs.split("#");
                List<String> lore = new ArrayList<>();

                for (String s : lores) {
                    lore.add(StringUtils.replace(StringUtils.colored(s), "_", " "));
                }
                item.setLore(lore);
            }
            else if (str.contains("enchant")) {
                String[] parse = str.split(":");
                String enchantName = parse[1];
                int level;

                try {
                    level = Integer.parseInt(parse[2]);
                }
                catch (NumberFormatException ex) {
                    FunnyGuilds.parser("Unknown enchant level: " + split[2]);
                    level = 1;
                }

                Enchantment enchant = Enchantment.getByName(enchantName.toUpperCase());

                if (enchant == null) {
                    FunnyGuilds.parser("Unknown enchant: " + parse[1]);
                }

                item.addEnchant(enchant, level);
            }
        }

        return item.getItem();
    }

    public static Material parseMaterial(String string) {
        if (string == null) {
            FunnyGuilds.parser("Unknown material: null");
            return Material.AIR;
        }
        String m = string;
        m = m.toUpperCase();
        m = m.replaceAll(" ", "_");
        Material material = Material.getMaterial(m);
        if (material == null) {
            if (!string.equalsIgnoreCase("ender crystal")) {
                FunnyGuilds.parser("Unknown material: " + string);
            }
            return Material.AIR;
        }
        return material;
    }

    public static Location parseLocation(String string) {
        if (string == null) {
            return null;
        }

        String[] shs = string.split(",");

        if (shs.length < 4) {
            return null;
        }

        World world = Bukkit.getWorld(shs[0]);

        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }

        return new Location(world, Integer.valueOf(shs[1]), Integer.valueOf(shs[2]), Integer.valueOf(shs[3]));
    }

    public static String parseRank(String string) {
        if (!string.contains("TOP-")) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean open = false;
        boolean start = false;

        for (char c : string.toCharArray()) {
            boolean end = false;

            switch (c) {
                case '{':
                    open = true;
                    break;
                case '-':
                    start = true;
                    break;
                case '}':
                    end = true;
                    break;
                default:
                    if (open && start) {
                        sb.append(c);
                    }
            }

            if (end) {
                break;
            }
        }

        try {
            int i = Integer.valueOf(sb.toString());

            PluginConfig c = Settings.getConfig();
            List<Guild> rankedGuilds = new ArrayList<>();

            for (int in = 1; in <= RankManager.getInstance().guilds(); in++) {
                Guild guild = RankManager.getInstance().getGuild(in);

                if (guild != null && guild.getMembers().size() >= c.minMembersToInclude) {
                    rankedGuilds.add(guild);
                }
            }

            if (string.contains("GTOP")) {
                if (rankedGuilds.isEmpty()) {
                    return StringUtils
                            .replace(string, "{GTOP-" + Integer.toString(i) + '}', "Brak");
                } else if (i - 1 >= rankedGuilds.size()) {
                    return StringUtils
                            .replace(string, "{GTOP-" + Integer.toString(i) + '}', "Brak");
                } else {
                    Guild guild = rankedGuilds.get(i - 1);

                    return StringUtils
                            .replace(string, "{GTOP-" + Integer.toString(i) + '}',
                                    guild.getTag() + " " +
                                            StringUtils.replace(Settings.getConfig().playerlistPoints,
                                                    "{POINTS}", Integer.toString(guild.getRank().getPoints()))
                            );
                }
            }
            else if (string.contains("PTOP")) {
                User user = RankManager.getInstance().getUser(i);

                if (user != null) {
                    return StringUtils.replace(string, "{PTOP-" + Integer.toString(i) + '}', user.getName());
                }
                else {
                    return StringUtils.replace(string, "{PTOP-" + Integer.toString(i) + '}', "Brak");
                }
            }
        }
        catch (NumberFormatException e) {
            FunnyGuilds.parser("Unknown number: " + sb.toString());
        }

        return null;
    }

    public static long parseTime(String string) {
        if (string == null || string.isEmpty()) {
            return 0;
        }

        Stack<Character> type = new Stack<>();
        StringBuilder value = new StringBuilder();

        boolean calc = false;
        long time = 0;

        for (char c : string.toCharArray()) {
            switch (c) {
                case 'd':
                case 'h':
                case 'm':
                case 's':
                    if (!calc) {
                        type.push(c);
                    }

                    try {
                        long i = Integer.valueOf(value.toString());
                        switch (type.pop()) {
                            case 'd':
                                time += i * 86400000L;
                                break;
                            case 'h':
                                time += i * 3600000L;
                                break;
                            case 'm':
                                time += i * 60000L;
                                break;
                            case 's':
                                time += i * 1000L;
                                break;
                        }
                    }
                    catch (NumberFormatException e) {
                        FunnyGuilds.parser("Unknown number: " + value.toString());
                        return time;
                    }

                    type.push(c);
                    calc = true;
                    break;
                default:
                    value.append(c);
                    break;
            }
        }

        return time;
    }

    public static String toString(Location loc) {
        if (loc == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(loc.getWorld().getName());
        sb.append(",");
        sb.append(loc.getBlockX());
        sb.append(",");
        sb.append(loc.getBlockY());
        sb.append(",");
        sb.append(loc.getBlockZ());

        return sb.toString();
    }

}
