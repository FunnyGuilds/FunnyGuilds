package net.dzikoysk.funnyguilds.basic.util;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Basic;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;

import java.util.ArrayList;
import java.util.Collection;

public class BasicUtils {

    public static void checkObjects() {
        Collection<String> guilds = BasicUtils.getNames(GuildUtils.getGuilds());
        Collection<String> regions = BasicUtils.getNames(RegionUtils.getRegions());
        int i = 0;
        for (Guild guild : GuildUtils.getGuilds()) {
            if (guild.getName() != null && regions.contains(guild.getName())) {
                guilds.remove(guild.getName());
                continue;
            }
            GuildUtils.deleteGuild(guild);
            i++;
        }

        guilds = BasicUtils.getNames(GuildUtils.getGuilds());
        regions = BasicUtils.getNames(RegionUtils.getRegions());

        for (Region region : RegionUtils.getRegions()) {
            if (region.getName() != null && guilds.contains(region.getName())) {
                regions.remove(region.getName());
                continue;
            }
            RegionUtils.delete(region);
            i++;
        }
        if (i > 0) FunnyGuilds.warning("Repaired conflicts: " + i);
    }

    public static Collection<String> getNames(Collection<? extends Basic> collection) {
        if (collection == null || collection.isEmpty()) return new ArrayList<>(0);
        Collection<String> list = new ArrayList<>(collection.size());
        for (Basic basic : collection) if (basic != null && basic.getName() != null) list.add(basic.getName());
        return list;
    }

}
