package net.dzikoysk.funnyguilds.feature.placeholders.impl;

import java.util.function.Supplier;
import net.dzikoysk.funnyguilds.feature.placeholders.Placeholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.GuildResolver;
import net.dzikoysk.funnyguilds.guild.Guild;

public class GuildPlaceholder implements Placeholder<Guild> {

    private final GuildResolver guildResolver;
    private final Supplier<Object> fallbackSupplier;

    public GuildPlaceholder(GuildResolver guildResolver, Supplier<Object> fallbackSupplier) {
        this.guildResolver = guildResolver;
        this.fallbackSupplier = fallbackSupplier;
    }

    public GuildPlaceholder(GuildResolver guildResolver) {
        this(guildResolver, () -> "");
    }

    @Override
    public Object getRaw(Guild guild) {
        return guild != null
                ? guildResolver.resolve(guild)
                : fallbackSupplier.get();
    }

}
