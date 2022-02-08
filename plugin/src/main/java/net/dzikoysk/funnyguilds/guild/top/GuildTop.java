package net.dzikoysk.funnyguilds.guild.top;

import java.util.Comparator;
import java.util.NavigableSet;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRank;
import net.dzikoysk.funnyguilds.rank.Top;

public class GuildTop extends Top<GuildRank> {

    public GuildTop(Comparator<GuildRank> comparator, Function<Comparator<GuildRank>, NavigableSet<GuildRank>> recalculateFunction) {
        super(comparator, recalculateFunction);
    }

    public Guild getGuild(int place) {
        return this.get(place)
                .map(GuildRank::getGuild)
                .getOrNull();
    }

}
