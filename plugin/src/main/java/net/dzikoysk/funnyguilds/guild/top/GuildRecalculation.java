package net.dzikoysk.funnyguilds.guild.top;

import java.util.Collections;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.GuildRank;

public class GuildRecalculation implements Function<Comparator<GuildRank>, NavigableSet<GuildRank>> {

    private final GuildManager guildManager;

    public GuildRecalculation(GuildManager guildManager) {
        this.guildManager = guildManager;
    }

    @Override
    public NavigableSet<GuildRank> apply(Comparator<GuildRank> userRankComparator) {
        NavigableSet<GuildRank> guildsRank = new TreeSet<>(Collections.reverseOrder(userRankComparator));

        for (Guild guild : guildManager.getGuilds()) {
            GuildRank guildRank = guild.getRank();

            if (!guild.isRanked()) {
                continue;
            }

            guildsRank.add(guildRank);
        }

        int position = 0;

        for (GuildRank guildRank : guildsRank) {
            guildRank.setPosition(++position);
        }

        return guildsRank;
    }

}