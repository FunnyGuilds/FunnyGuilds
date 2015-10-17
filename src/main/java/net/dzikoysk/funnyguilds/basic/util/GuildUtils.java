package net.dzikoysk.funnyguilds.basic.util;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.util.reflect.EntityUtil;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GuildUtils {

    private static List<Guild> guilds = new ArrayList<>();

    public static void deleteGuild(final Guild guild) {
        Bukkit.getScheduler().runTask(FunnyGuilds.getInstance(), new Runnable() {

            @Override
            public void run() {
                if (guild == null)
                    return;
                Manager.getInstance().stop();
                final Region region = guild.getRegion();
                if (region != null) {
                    if (Settings.getInstance().createStringMaterial.equalsIgnoreCase("ender crystal"))
                        EntityUtil.despawn(guild);
                    else if (Settings.getInstance().createMaterial != Material.AIR) {
                        Block block = region.getCenter().getBlock().getRelative(BlockFace.DOWN);
                        if (block.getLocation().getBlockY() > 1)
                            block.setType(Material.AIR);
                    }
                }
                IndependentThread.action(ActionType.PREFIX_GLOBAL_REMOVE_GUILD, guild);
                UserUtils.removeGuild(guild.getMembers());
                RankManager.getInstance().remove(guild);
                RegionUtils.delete(guild.getRegion());
                for (Guild g : guild.getAllies())
                    g.removeAlly(guild);
                for (Guild g : guild.getEnemies())
                    g.removeEnemy(guild);
                // TODO
                //if(Settings.getInstance().flat) Flat.getGuildFile(guild).delete();
                //if(Settings.getInstance().mysql) new DatabaseGuild(guild).delete();
                guild.delete();
                Manager.getInstance().start();
            }
        });
    }

    public static Guild get(String name) {
        for (Guild guild : guilds)
            if (guild.getName() != null && guild.getName().equalsIgnoreCase(name))
                return guild;
        return null;
    }

    public static Guild byTag(String tag) {
        for (Guild guild : guilds)
            if (guild.getTag() != null && guild.getTag().equalsIgnoreCase(tag.toLowerCase()))
                return guild;
        return null;
    }

    public static boolean isExists(String name) {
        for (Guild guild : guilds)
            if (guild.getName() != null && guild.getName().equalsIgnoreCase(name))
                return true;
        return false;
    }

    public static boolean tagExists(String tag) {
        for (Guild guild : guilds)
            if (guild.getTag() != null && guild.getTag().equalsIgnoreCase(tag))
                return true;
        return false;
    }

    public static Collection<String> getTags(Collection<Guild> c) {
        if (c == null)
            return new ArrayList<>(0);
        Collection<String> list = new ArrayList<>(c.size());
        for (Guild guild : c)
            if (guild.getTag() != null)
                list.add(guild.getTag());
        return list;
    }

    public static Collection<Guild> getGuilds(Collection<String> c) {
        if (c == null)
            return new ArrayList<>(0);
        List<Guild> list = new ArrayList<>(0);
        for (String s : c)
            if (get(s) != null)
                list.add(get(s));
        return list;
    }

    public static List<Guild> getGuilds() {
        return new ArrayList<Guild>(guilds);
    }

    public static void addGuild(Guild guild) {
        if (guild == null || guild.getName() == null)
            return;
        if (get(guild.getName()) == null)
            guilds.add(guild);
    }

    public static void removeGuild(Guild guild) {
        guilds.remove(guild);
    }

}
