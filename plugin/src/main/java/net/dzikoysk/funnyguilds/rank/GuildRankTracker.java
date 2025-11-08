package net.dzikoysk.funnyguilds.rank;

import java.util.concurrent.Executor;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRankNameProvider;

final class GuildRankTracker<T extends Number> extends CommonRankTracker<Guild, T> {

    GuildRankTracker(
            Executor executor,
            StatKey<T> statKey,
            GuildRankNameProvider nameProvider,
            Function<Guild, Boolean> isRanked
    ) {
        super(
                executor,
                statKey,
                nameProvider,
                isRanked
        );
    }

}
