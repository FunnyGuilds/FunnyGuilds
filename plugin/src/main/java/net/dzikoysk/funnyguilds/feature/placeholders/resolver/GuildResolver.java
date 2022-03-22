package net.dzikoysk.funnyguilds.feature.placeholders.resolver;

import net.dzikoysk.funnyguilds.guild.Guild;

@FunctionalInterface
public interface GuildResolver {
    Object resolve(Guild guild);
}
