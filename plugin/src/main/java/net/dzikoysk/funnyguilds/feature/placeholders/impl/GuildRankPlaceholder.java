package net.dzikoysk.funnyguilds.feature.placeholders.impl;

import java.util.function.Supplier;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.GuildResolver.RankResolver;

public class GuildRankPlaceholder extends GuildPlaceholder{

    public GuildRankPlaceholder(RankResolver guildResolver, Supplier<Object> fallbackSupplier) {
        super(guildResolver, fallbackSupplier);
    }

    public GuildRankPlaceholder(RankResolver  guildResolver) {
        super(guildResolver);
    }

}
