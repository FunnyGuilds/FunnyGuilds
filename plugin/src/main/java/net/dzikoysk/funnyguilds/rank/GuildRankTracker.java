package net.dzikoysk.funnyguilds.rank;

import java.util.function.Predicate;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;

final class GuildRankTracker<T extends Number> extends CommonRankTracker<Guild, T> {

    GuildRankTracker(
            StatKey<T> statKey,
            GuildManager guildManager,
            Predicate<Guild> isRanked
    ) {
        super(
                statKey,
                guildManager::findByUuid,
                isRanked
        );
    }

}
