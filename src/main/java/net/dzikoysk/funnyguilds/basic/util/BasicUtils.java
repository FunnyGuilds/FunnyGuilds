package net.dzikoysk.funnyguilds.basic.util;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.FunnyLogger;

import java.util.List;

public class BasicUtils {

    public static void checkObjects() {
        if (!Settings.getConfig().regionsEnabled) {
            return;
        }
        
        List<String> guilds = GuildUtils.getNames(GuildUtils.getGuilds());
        List<String> regions = RegionUtils.getNames(RegionUtils.getRegions());
        
        int i = 0;
        for (Guild guild : GuildUtils.getGuilds()) {
            if (guild.getName() != null && regions.contains(guild.getName())) {
                guilds.remove(guild.getName());
                continue;
            }
            
            GuildUtils.deleteGuild(guild);
            i++;
        }

        guilds = GuildUtils.getNames(GuildUtils.getGuilds());
        regions = RegionUtils.getNames(RegionUtils.getRegions());

        for (Region region : RegionUtils.getRegions()) {
            if (region.getName() != null && guilds.contains(region.getName())) {
                regions.remove(region.getName());
                continue;
            }
            
            RegionUtils.delete(region);
            i++;
        }
        
        if (i > 0) {
            FunnyLogger.warning("Repaired conflicts: " + i);
        }
    }
}
