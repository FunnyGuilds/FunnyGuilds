package net.dzikoysk.funnyguilds.guild.placeholders;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import net.dzikoysk.funnyguilds.config.FunnyTimeFormatter;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.feature.placeholders.Placeholders;
import net.dzikoysk.funnyguilds.feature.placeholders.formatting.NumericValueFormatter;
import net.dzikoysk.funnyguilds.feature.placeholders.formatting.ValueFormatting;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.FallbackPlaceholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.LocaleMonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.LocalePairResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.LocaleSimpleResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRank;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.shared.adventure.ComponentUtil;

public class GuildPlaceholders extends Placeholders<Guild, GuildPlaceholders> {

    public GuildPlaceholders property(String name, LocaleMonoResolver<Guild> resolver, LocaleSimpleResolver fallbackResolver) {
        return this.property(name, new FallbackPlaceholder<>(resolver, fallbackResolver));
    }

    public GuildPlaceholders property(String name, MonoResolver<Guild> resolver, LocaleSimpleResolver fallbackResolve) {
        return this.property(name, (entity, data) -> resolver.resolve(data), fallbackResolve);
    }

    public GuildPlaceholders rankProperty(String name, LocalePairResolver<Guild, GuildRank> resolver, LocaleSimpleResolver fallbackResolver) {
        return this.property(name, (entity, guild) -> resolver.resolve(entity, guild, guild.getRank()), fallbackResolver);
    }

    public GuildPlaceholders rankProperty(String name, MonoResolver<GuildRank> resolver, Number fallbackValue) {
        return this.property(name,
                (entity, guild) -> {
                    Object value = resolver.resolve(guild.getRank());
                    if (value instanceof Float || value instanceof Double) {
                        return String.format(Locale.ROOT, "%.2f", ((Number) value).floatValue());
                    }
                    return Objects.toString(value);
                },
                entity -> fallbackValue
        );
    }

    public GuildPlaceholders timeProperty(String name, Function<Guild, Instant> timeSupplier, MessageService messages, LocaleSimpleResolver fallbackResolver) {
        return this
                .property(
                        name,
                        (entity, guild) -> formatDate(entity, guild, timeSupplier, messages.get(entity, config -> config.dateFormat), fallbackResolver),
                        fallbackResolver
                )
                .property(
                        name + "-time", 
                        (entity, guild) -> formatTime(entity, guild, timeSupplier, fallbackResolver),
                        fallbackResolver
                );
    }

    public GuildPlaceholders numericRankProperty(String name, String metric, Function<GuildRank, Number> resolver, Number fallback, Supplier<ValueFormatting> formatting) {
        return this.rankProperty(name, rank -> resolver.apply(rank), fallback)
                .property(name + "-format", guild -> formatting.get().guild(metric, resolver.apply(guild.getRank())),
                        entity -> NumericValueFormatter.plain(fallback));
    }

    public GuildPlaceholders numericProperty(String name, String metric, Function<Guild, Number> resolver, Supplier<ValueFormatting> formatting) {
        return this.property(name, guild -> resolver.apply(guild), entity -> 0)
                .property(name + "-format", guild -> formatting.get().guild(metric, resolver.apply(guild)),
                        entity -> NumericValueFormatter.plain(0));
    }

    private static Object formatDate(Object entity, Guild guild, Function<Guild, Instant> timeSupplier, FunnyTimeFormatter formatter, LocaleSimpleResolver fallbackResolver) {
        Instant endTime = timeSupplier.apply(guild);
        return endTime.isBefore(Instant.now())
                ? fallbackResolver.resolve(entity)
                : ComponentUtil.toComponent(formatter.format(endTime));
    }

    private static Object formatTime(Object entity, Guild guild, Function<Guild, Instant> timeSupplier, LocaleSimpleResolver fallbackResolver) {
        Instant endTime = timeSupplier.apply(guild);
        return endTime.isBefore(Instant.now())
                ? fallbackResolver.resolve(entity)
                : ComponentUtil.toComponent(TimeUtils.formatTime(Duration.between(Instant.now(), endTime)));
    }

    @Override
    public GuildPlaceholders create() {
        return new GuildPlaceholders();
    }

}
