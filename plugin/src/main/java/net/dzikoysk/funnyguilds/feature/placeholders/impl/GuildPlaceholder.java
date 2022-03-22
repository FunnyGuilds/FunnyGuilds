package net.dzikoysk.funnyguilds.feature.placeholders.impl;

import net.dzikoysk.funnyguilds.feature.placeholders.Placeholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.GuildResolver;
import net.dzikoysk.funnyguilds.guild.Guild;

public class GuildPlaceholder implements Placeholder<Guild> {

    private final GuildResolver guildResolver;

    public GuildPlaceholder(GuildResolver guildResolver) {
        this.guildResolver = guildResolver;
    }

    @Override
    public Object getRaw(Guild guild) {
        return this.guildResolver.resolve(guild);
    }

}
