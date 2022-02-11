package net.dzikoysk.funnyguilds.guild.top;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.BiFunction;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.GuildRank;
import net.dzikoysk.funnyguilds.rank.TopComparator;

public class GuildRecalculation implements BiFunction<String, TopComparator<GuildRank>, NavigableSet<GuildRank>> {

    private final GuildManager guildManager;

    public GuildRecalculation(GuildManager guildManager) {
        this.guildManager = guildManager;
    }

    @Override
    public NavigableSet<GuildRank> apply(String id, TopComparator<GuildRank> topComparator) {
        NavigableSet<GuildRank> guildsRank = new TreeSet<>(topComparator);

        for (Guild guild : guildManager.getGuilds()) {
            GuildRank guildRank = guild.getRank();

            if (!guild.isRanked()) {
                continue;
            }

            guildsRank.add(guildRank);
        }

        int position = 0;

        for (GuildRank guildRank : guildsRank) {
            guildRank.setPosition(id, ++position);
        }

        return guildsRank;
    }

}