package net.dzikoysk.funnyguilds.rank;

import java.util.function.Function;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRankNameProvider;

final class GuildRankTracker<T extends Number> extends CommonRankTracker<Guild, T> {

    GuildRankTracker(
            StatKey<T> statKey,
            GuildRankNameProvider nameProvider,
            Function<Guild, Boolean> isRanked
    ) {
        super(
                statKey,
                nameProvider,
                isRanked
        );
    }

}
