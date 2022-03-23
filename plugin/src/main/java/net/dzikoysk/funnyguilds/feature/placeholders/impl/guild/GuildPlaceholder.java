package net.dzikoysk.funnyguilds.feature.placeholders.impl.guild;

import java.util.function.Supplier;
import net.dzikoysk.funnyguilds.feature.placeholders.impl.FallbackPlaceholder;
import net.dzikoysk.funnyguilds.feature.placeholders.impl.Placeholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.GuildResolver;
import net.dzikoysk.funnyguilds.guild.Guild;
import org.jetbrains.annotations.Nullable;

public class GuildPlaceholder implements Placeholder<Guild>, FallbackPlaceholder<Guild> {

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

    @Override
    public Object getRawFallback(@Nullable Guild guild) {
        return fallbackSupplier.get();
    }

}
