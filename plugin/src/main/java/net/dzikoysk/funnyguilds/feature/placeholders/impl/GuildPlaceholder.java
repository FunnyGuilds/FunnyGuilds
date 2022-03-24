package net.dzikoysk.funnyguilds.feature.placeholders.impl;

import java.util.function.Supplier;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.guild.Guild;
import org.jetbrains.annotations.Nullable;

public class GuildPlaceholder implements Placeholder<Guild>, FallbackPlaceholder<Guild> {

    private final MonoResolver<Guild> guildResolver;
    private final Supplier<Object> fallbackSupplier;

    public GuildPlaceholder(MonoResolver<Guild> guildResolver, Supplier<Object> fallbackSupplier) {
        this.guildResolver = guildResolver;
        this.fallbackSupplier = fallbackSupplier;
    }

    @Override
    public Object getRaw(Guild guild) {
        return guild != null
                ? guildResolver.resolve(guild)
                : fallbackSupplier.get();
    }

    @Override
    public Object getRawFallback(@Nullable Guild guild) {
        return fallbackSupplier.get();
    }

}
