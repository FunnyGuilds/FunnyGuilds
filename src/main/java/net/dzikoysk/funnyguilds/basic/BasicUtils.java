package net.dzikoysk.funnyguilds.basic;

import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.data.Settings;

import java.util.List;
import java.util.Set;

public class BasicUtils {

    public static void checkObjects() {
        if (!Settings.getConfig().regionsEnabled) {
            return;
        }
        
        Set<String> guilds = GuildUtils.getNames(GuildUtils.getGuilds());
        List<String> regions = RegionUtils.getNames(RegionUtils.getRegions());
        
        int repaired = 0;

        for (Guild guild : GuildUtils.getGuilds()) {
            if (guild.getName() != null && regions.contains(guild.getName())) {
                guilds.remove(guild.getName());
                continue;
            }
            
            GuildUtils.deleteGuild(guild);
            repaired++;
        }

        guilds = GuildUtils.getNames(GuildUtils.getGuilds());
        regions = RegionUtils.getNames(RegionUtils.getRegions());

        for (Region region : RegionUtils.getRegions()) {
            if (region.getName() != null && guilds.contains(region.getName())) {
                regions.remove(region.getName());
                continue;
            }
            
            RegionUtils.delete(region);
            repaired++;
        }
        
        if (repaired > 0) {
            FunnyGuildsLogger.warning("Repaired conflicts: " + repaired);
        }
    }

}
