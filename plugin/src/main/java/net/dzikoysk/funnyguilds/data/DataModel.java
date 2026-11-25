package net.dzikoysk.funnyguilds.data;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionManager;

public interface DataModel {

    void load() throws SQLException;

    void save(boolean ignoreNotChanged);

    default void saveAndClose() {
        this.save(false);
    }

    void deleteGuild(Guild guild);

    default void queueGuildDeletion(Guild guild) {
        this.deleteGuild(guild);
    }

    default void flushDeletes() {
    }

    void deleteRegion(Region region);

    void renameGuild(Guild guild, String name);

    default void validateLoadedData() {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        GuildManager guildManager = plugin.getGuildManager();
        RegionManager regionManager = plugin.getRegionManager();

        if (!plugin.getPluginConfiguration().regionsEnabled) {
            return;
        }

        // Entity.names() returns an unmodifiable set, so collect into mutable sets for repair removals
        Set<String> guilds = new HashSet<>(Entity.names(guildManager.getGuilds()));
        Set<String> regions = new HashSet<>(Entity.names(regionManager.getRegions()));

        int repaired = 0;

        for (Guild guild : guildManager.getGuilds()) {
            if (regions.contains(guild.getName())) {
                guilds.remove(guild.getName());
                continue;
            }

            guildManager.deleteGuild(plugin, guild);
            repaired++;
        }

        guilds = new HashSet<>(Entity.names(guildManager.getGuilds()));
        regions = new HashSet<>(Entity.names(regionManager.getRegions()));

        for (Region region : regionManager.getRegions()) {
            if (guilds.contains(region.getName())) {
                regions.remove(region.getName());
                continue;
            }

            regionManager.deleteRegion(plugin.getDataModel(), region);
            repaired++;
        }

        if (repaired > 0) {
            FunnyGuilds.getPluginLogger().warning("Repaired conflicts: " + repaired);
        }
    }

    static DataModel create(FunnyGuilds plugin, PluginConfiguration.DataModel modelType) {
        if (modelType.isSQL()) {
            return new SQLDataModel(plugin);
        }

        return new FlatDataModel(plugin);
    }

}
