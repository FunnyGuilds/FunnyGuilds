package net.dzikoysk.funnyguilds.data;

import java.sql.SQLException;
import java.util.List;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;

public interface DataModel {

    void load() throws SQLException;

    void save(boolean ignoreNotChanged);

    default void validateLoadedData() {
        if (!FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
            return;
        }

        List<String> guilds = Entity.names(GuildUtils.getGuilds());
        List<String> regions = Entity.names(RegionUtils.getRegions());

        int repaired = 0;

        for (Guild guild : GuildUtils.getGuilds()) {
            if (guild.getName() != null && regions.contains(guild.getName())) {
                guilds.remove(guild.getName());
                continue;
            }

            FunnyGuilds.getInstance().getGuildManager().deleteGuild(guild);
            repaired++;
        }

        guilds = Entity.names(GuildUtils.getGuilds());
        regions = Entity.names(RegionUtils.getRegions());

        for (Region region : RegionUtils.getRegions()) {
            if (region.getName() != null && guilds.contains(region.getName())) {
                regions.remove(region.getName());
                continue;
            }

            FunnyGuilds.getInstance().getRegionManager().deleteRegion(region);
            repaired++;
        }

        if (repaired > 0) {
            FunnyGuilds.getPluginLogger().warning("Repaired conflicts: " + repaired);
        }
    }

    static DataModel create(FunnyGuilds funnyGuilds, PluginConfiguration.DataModel dataModel) {
        if (dataModel == PluginConfiguration.DataModel.MYSQL) {
            return new SQLDataModel();
        }

        return new FlatDataModel(funnyGuilds);
    }
}
