package net.dzikoysk.funnyguilds.util;

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
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Parser {

    public static ItemStack parseItem(String string) {
        String[] split = string.split(" ");
        String[] typeSplit = split[1].split(":");
        String subtype = typeSplit.length > 1 ? typeSplit[1] : "0";

        Material mat = parseMaterial(typeSplit[0], false);
        
        int stack;
        int data;

        try {
            stack = Integer.parseInt(split[0]);
            data = Integer.parseInt(subtype);
        }
        catch (NumberFormatException ex) {
            FunnyLogger.parser("Unknown size: " + split[0]);
            stack = 1;
            data = 0;
        }

        ItemBuilder item = new ItemBuilder(mat, stack, data);

        for (int i = 2; i < split.length; i++) {
            String str = split[i];

            if (str.contains("name")) {
                String[] splitName = str.split(":");
                item.setName(StringUtils.replace(StringUtils.colored(String.join(":", Arrays.copyOfRange(splitName, 1, splitName.length))), "_", " "), true);
            } else if (str.contains("lore")) {
                String[] splitLore = str.split(":");
                String loreArgs = String.join(":", Arrays.copyOfRange(splitLore, 1, splitLore.length));
                String[] lores = loreArgs.split("#");
                List<String> lore = new ArrayList<>();

                for (String s : lores) {
                    lore.add(StringUtils.replace(StringUtils.replace(StringUtils.colored(s), "_", " "), "{HASH}", "#"));
                }
                
                item.setLore(lore);
            } else if (str.contains("enchant")) {
                String[] parse = str.split(":");
                String enchantName = parse[1];
                int level;

                try {
                    level = Integer.parseInt(parse[2]);
                } catch (NumberFormatException ex) {
                    FunnyLogger.parser("Unknown enchant level: " + split[2]);
                    level = 1;
                }

                Enchantment enchant = Enchantment.getByName(enchantName.toUpperCase());

                if (enchant == null) {
                    FunnyLogger.parser("Unknown enchant: " + parse[1]);
                }

                item.addEnchant(enchant, level);
            }
        }

        return item.getItem();
    }

    public static Material parseMaterial(String string, boolean allowNullReturn) {
        if (string == null) {
            FunnyLogger.parser("Unknown material: null");
            return allowNullReturn ? null : Material.AIR;
        }

        
        String materialName = string.toUpperCase().replaceAll(" ", "_");
        Material material = MaterialAliaser.getByAlias(materialName);
        
        if (material !=null) {
            return material;
        }

        material = Material.getMaterial(materialName);
        if (material == null) {
            FunnyLogger.parser("Unknown material: " + string);
            return allowNullReturn ? null : Material.AIR;
        }
        
        return material;
    }
    
    @SuppressWarnings("deprecation")
    public static MaterialData parseMaterialData(String string, boolean allowNullReturn) {
        if (string == null) {
            FunnyLogger.parser("Unknown materialdata: null");
            return allowNullReturn ? null : new MaterialData(Material.AIR);
        }
        
        String[] data = string.split(":");
        Material material = parseMaterial(data[0], allowNullReturn);
        
        if (material == null) {
            FunnyLogger.parser("Unknown material in materialdata: " + string);
            return allowNullReturn ? null : new MaterialData(Material.AIR);
        }
        
        return new MaterialData(material, data.length == 2 ? Byte.parseByte(data[1]) : 0);
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

    public static String parseRank(String var) {
        if (!var.contains("TOP-")) {
            return null;
        }

        int i = getIndex(var);
        if(i <= 0) {
            FunnyLogger.error("Index in TOP- must be greater or equal to 1!");
            return null;
        }

        PluginConfig c = Settings.getConfig();
        List<Guild> rankedGuilds = new ArrayList<>();

        for (int in = 1; in <= RankManager.getInstance().guilds(); in++) {
            Guild guild = RankManager.getInstance().getGuild(in);
            
            if (guild != null && guild.getMembers().size() >= c.minMembersToInclude) {
                rankedGuilds.add(guild);
            }
        }

        if (var.contains("GTOP")) {
            if (rankedGuilds.isEmpty() || i - 1 >= rankedGuilds.size()) {
                return StringUtils.replace(var, "{GTOP-" + i + '}', "Brak");
            } else {
                Guild guild = rankedGuilds.get(i - 1);
                int points = guild.getRank().getPoints();
                String pointsFormat = c.gtopPoints;
                
                if (!pointsFormat.isEmpty()) {
                    pointsFormat = pointsFormat.replace("{POINTS-FORMAT}", IntegerRange.inRange(points, c.pointsFormat));
                    pointsFormat = pointsFormat.replace("{POINTS}", String.valueOf(points));
                }

                return StringUtils.replace(var, "{GTOP-" + i + '}', guild.getTag() + pointsFormat);
            }
        } else if (var.contains("PTOP")) {
            User user = RankManager.getInstance().getUser(i);
            
            if (user != null) {
                int points = user.getRank().getPoints();
                String pointsFormat = c.ptopPoints;
                
                if (!pointsFormat.isEmpty()) {
                    pointsFormat = pointsFormat.replace("{POINTS-FORMAT}", IntegerRange.inRange(points, c.pointsFormat));
                    pointsFormat = pointsFormat.replace("{POINTS}", String.valueOf(points));
                }
                
                return StringUtils.replace(var, "{PTOP-" + i + '}', (user.isOnline() ? c.ptopOnline : c.ptopOffline) + user.getName() + pointsFormat);
            } else {
                return StringUtils.replace(var, "{PTOP-" + i + '}', "Brak");
            }
        }
        
        return null;
    }

    public static int getIndex(String var) {
        StringBuilder sb = new StringBuilder();
        boolean open = false;
        boolean start = false;
        int result = -1;

        for (char c : var.toCharArray()) {
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
            result = Integer.parseInt(sb.toString());
        } catch(NumberFormatException e) {
            FunnyLogger.parser(var + " contains an invalid number: " + sb.toString());
        }
        
        return result;
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
                    } catch (NumberFormatException e) {
                        FunnyLogger.parser("Unknown number: " + value.toString());
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
    
    public static Map<IntegerRange, String> parseIntegerRange(List<String> data, boolean color) {
        Map<IntegerRange, String> parsed = new HashMap<>();
        
        for (String s : data) {
            String[] split = s.split(" ");
            if (split.length < 2) {
                FunnyLogger.parser("\"" + s + "\" is not a valid range String!");
                continue;
            }

            String[] range = split[0].split("-");
            if (range.length < 2) {
                FunnyLogger.parser("\"" + s + "\" is not a valid integer range String!");
                continue;
            }

            int minRange = 0;
            int maxRange = 0;
            
            try {
                minRange = Integer.parseInt(range[0]);
            } catch (NumberFormatException e) {
                FunnyLogger.parser("\"" + range[0] + "\" of integer range String \"" + s + "\" is not a valid integer!");
                continue;
            }

            try {
                maxRange = range[1].equals("*") ? Integer.MAX_VALUE : Integer.parseInt(range[1]);
            } catch (NumberFormatException e) {
                FunnyLogger.parser("\"" + range[1] + "\" of integer range String \"" + s + "\" is not a valid integer!");
                continue;
            }

            parsed.put(new IntegerRange(minRange, maxRange), color ? StringUtils.colored(split[1]) : split[1]);
        }
        
        return parsed;
    }
    
    private Parser() {}
    
}
