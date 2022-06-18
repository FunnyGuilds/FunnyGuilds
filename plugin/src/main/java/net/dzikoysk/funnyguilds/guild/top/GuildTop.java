package net.dzikoysk.funnyguilds.guild.top;

import java.util.NavigableSet;
import java.util.function.BiFunction;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRank;
import net.dzikoysk.funnyguilds.rank.Top;
import net.dzikoysk.funnyguilds.rank.TopComparator;
import panda.std.Option;

public class GuildTop extends Top<GuildRank> {

    public GuildTop(TopComparator<GuildRank> comparator, BiFunction<String, TopComparator<GuildRank>, NavigableSet<GuildRank>> recalculateFunction) {
        super(comparator, recalculateFunction);
    }

    public Option<Guild> getGuild(int place) {
        return this.get(place).map(GuildRank::getGuild);
    }

}
