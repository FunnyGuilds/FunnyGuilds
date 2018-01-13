package net.dzikoysk.funnyguilds.basic.util;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.data.flat.Flat;
import net.dzikoysk.funnyguilds.util.reflect.EntityUtil;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

public class GuildUtils {

    private static List<Guild> guilds = new ArrayList<>();

    public static List<Guild> getGuilds() {
        return new ArrayList<>(guilds);
    }

    public static void deleteGuild(final Guild guild) {
        if (guild == null) {
            return;
        }

        Manager.getInstance().stop();
        final Region region = RegionUtils.get(guild.getRegion());

        if (region != null) {
            if (Settings.getConfig().createStringMaterial.equalsIgnoreCase("ender crystal")) {
                EntityUtil.despawn(guild);
            } else if (Settings.getConfig().createMaterial != Material.AIR) {
                Bukkit.getScheduler().runTask(FunnyGuilds.getInstance(), () -> {
                    Block block = region.getCenter().getBlock().getRelative(BlockFace.DOWN);
                    if (block.getLocation().getBlockY() > 1) {
                        block.setType(Material.AIR);
                    }
                });
            }
        }

        IndependentThread.action(ActionType.PREFIX_GLOBAL_REMOVE_GUILD, guild);

        for (String name : guild.getRegions()) {
            Region r = RegionUtils.get(name);

            if (r != null) {
                RegionUtils.delete(r);
            }
        }

        UserUtils.removeGuild(guild.getMembers());
        RankManager.getInstance().remove(guild);
        RegionUtils.delete(Region.get(guild.getRegion()));

        for (Guild g : guild.getAllies()) {
            g.removeAlly(guild);
        }

        for (Guild g : guild.getEnemies()) {
            g.removeEnemy(guild);
        }

        if (Settings.getConfig().dataType.flat) {
            Flat.getGuildFile(guild).delete();
        }

        if (Settings.getConfig().dataType.mysql) {
            new DatabaseGuild(guild).delete();
        }

        guild.delete();
        Manager.getInstance().start();
    }

    public static Guild get(String name) {
        for (Guild guild : guilds) {
            if (guild.getName() != null && guild.getName().equalsIgnoreCase(name)) {
                return guild;
            }
        }
        return null;
    }

    public static Guild byTag(String tag) {
        for (Guild guild : guilds) {
            if (guild.getTag() != null && guild.getTag().equalsIgnoreCase(tag.toLowerCase())) {
                return guild;
            }
        }
        return null;
    }

    public static boolean nameExists(String name) {
        for (Guild guild : guilds) {
            if (guild.getName() != null && guild.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean tagExists(String tag) {
        for (Guild guild : guilds) {
            if (guild.getTag() != null && guild.getTag().equalsIgnoreCase(tag)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getNames(List<Guild> lsg) {
        List<String> list = new ArrayList<>();
        if (lsg == null) {
            return list;
        }
        for (Guild g : lsg) {
            if (g == null) {
                continue;
            }
            if (g.getName() != null) {
                list.add(g.getName());
            }
        }
        return list;
    }

    public static List<String> getTags(List<Guild> lsg) {
        if (lsg == null) {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (Guild g : lsg) {
            if (g.getName() != null) {
                list.add(g.getTag());
            }
        }
        return list;
    }

    public static List<Guild> getGuilds(List<String> names) {
        if (names == null) {
            return null;
        }

        List<Guild> list = new ArrayList<>();

        for (String name : names) {
            Guild guild = Guild.get(name);

            if (guild != null) {
                list.add(guild);
            }
        }

        return list;
    }

    public static void addGuild(Guild guild) {
        if (guild == null || guild.getName() == null) {
            return;
        }
        if (get(guild.getName()) == null) {
            guilds.add(guild);
        }
    }

    public static void removeGuild(Guild guild) {
        guilds.remove(guild);
    }

}
