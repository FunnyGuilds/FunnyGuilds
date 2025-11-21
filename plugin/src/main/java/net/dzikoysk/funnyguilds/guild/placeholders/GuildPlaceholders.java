package net.dzikoysk.funnyguilds.guild.placeholders;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.config.FunnyTimeFormatter;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.feature.placeholders.Placeholders;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.FallbackPlaceholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.LocaleMonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.LocaleSimpleResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.SimpleResolver;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.rank.GuildStatAggregationType;
import net.dzikoysk.funnyguilds.rank.RankService;
import net.dzikoysk.funnyguilds.rank.StatKey;
import net.dzikoysk.funnyguilds.shared.TimeUtils;

public class GuildPlaceholders extends Placeholders<Guild, GuildPlaceholders> {

    public GuildPlaceholders property(
            String name,
            LocaleMonoResolver<Guild> resolver,
            LocaleSimpleResolver fallbackResolver
    ) {
        return this.property(
                name,
                new FallbackPlaceholder<>(
                        resolver,
                        fallbackResolver
                )
        );
    }

    public GuildPlaceholders property(
            String name,
            MonoResolver<Guild> resolver,
            LocaleSimpleResolver fallbackResolve
    ) {
        return this.property(
                name,
                (entity, data) -> resolver.resolve(data),
                fallbackResolve
        );
    }

    public <T extends Number, R extends Number> GuildPlaceholders rankProperty(
            String name,
            RankService rankService,
            StatKey<T> statKey,
            GuildStatAggregationType<R> aggregationType,
            LocaleSimpleResolver fallbackTopResolver
    ) {
        return this.statProperty(
                name,
                rankService,
                statKey,
                aggregationType
        ).topPositionProperty(
                name + "-position",
                rankService,
                statKey,
                aggregationType,
                fallbackTopResolver
        );
    }

    public <T extends Number, R extends Number> GuildPlaceholders statProperty(
            String name,
            RankService rankService,
            StatKey<T> statKey,
            GuildStatAggregationType<R> aggregationType
    ) {
        return this.property(
                name,
                (entity, guild) -> rankService.getGuildStatValue(
                                guild,
                                statKey,
                                aggregationType
                        )
                        .<Object>map(GuildPlaceholders::formatNumber)
                        .orNull(),
                entity -> 0
        );
    }

    public <T extends Number, R extends Number> GuildPlaceholders topPositionProperty(
            String name,
            RankService rankService,
            StatKey<T> statKey,
            GuildStatAggregationType<R> aggregationType,
            LocaleSimpleResolver fallbackResolver
    ) {
        return this.property(
                name,
                (entity, guild) -> rankService.getGuildTopPosition(
                                guild,
                                statKey,
                                aggregationType
                        )
                        .map(Object::toString)
                        .orNull(),
                fallbackResolver
        );
    }

    public GuildPlaceholders timeProperty(
            String name,
            Function<Guild, Instant> timeSupplier,
            MessageService messages,
            Function<MessageConfiguration, String> fallbackSupplier
    ) {
        String noValue = Objects.toString(
                messages.get(fallbackSupplier),
                ""
        );
        SimpleResolver fallbackResolver = () -> noValue;
        return this.property(
                        name,
                        (entity, guild) -> formatDate(
                                guild,
                                timeSupplier,
                                messages.get(
                                        entity,
                                        config -> config.dateFormat
                                ),
                                noValue
                        ),
                        fallbackResolver
                )
                .property(
                        name + "-time",
                        (entity, guild) -> formatTime(
                                guild,
                                timeSupplier,
                                noValue
                        ),
                        fallbackResolver
                );
    }

    private static String formatDate(
            Guild guild,
            Function<Guild, Instant> timeSupplier,
            FunnyTimeFormatter formatter,
            String noValue
    ) {
        Instant endTime = timeSupplier.apply(guild);
        return endTime.isBefore(Instant.now())
                ? noValue
                : formatter.format(endTime);
    }

    private static String formatTime(
            Guild guild,
            Function<Guild, Instant> timeSupplier,
            String noValue
    ) {
        Instant endTime = timeSupplier.apply(guild);
        return endTime.isBefore(Instant.now())
                ? noValue
                : TimeUtils.formatTime(Duration.between(
                        Instant.now(),
                        endTime
                ));
    }

    private static String formatNumber(Number number) {
        if (number instanceof Float || number instanceof Double) {
            return String.format(
                    "%.2f",
                    number.floatValue()
            );
        }
        return Objects.toString(number);
    }

    @Override
    public GuildPlaceholders create() {
        return new GuildPlaceholders();
    }

}
