package net.dzikoysk.funnyguilds.feature.placeholders.resolver;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRank;

@FunctionalInterface
public interface GuildResolver {
    Object resolve(Guild guild);

    interface RankResolver extends GuildResolver {
        Object resolve(Guild guild, GuildRank rank);

        @Override
        default Object resolve(Guild guild) {
            return resolve(guild, guild.getRank());
        }
    }
}
