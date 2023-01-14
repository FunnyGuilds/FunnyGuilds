package net.dzikoysk.funnyguilds.guild.placeholders;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.config.FunnyTimeFormatter;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.feature.placeholders.Placeholders;
import net.dzikoysk.funnyguilds.feature.placeholders.placeholder.FallbackPlaceholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.MonoResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.PairResolver;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.SimpleResolver;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRank;
import net.dzikoysk.funnyguilds.shared.TimeUtils;

public class GuildPlaceholders extends Placeholders<Guild, GuildPlaceholders> {

    public GuildPlaceholders property(String name, MonoResolver<Guild> resolver, SimpleResolver fallbackResolver) {
        return this.property(name, new FallbackPlaceholder<>(resolver, fallbackResolver));
    }

    public GuildPlaceholders property(String name, PairResolver<Guild, GuildRank> resolver, SimpleResolver fallbackResolver) {
        return this.property(name, guild -> resolver.resolve(guild, guild.getRank()), fallbackResolver);
    }

    public GuildPlaceholders timeProperty(String name, Function<Guild, Instant> timeSupplier, MessageConfiguration messages, SimpleResolver fallbackResolver) {
        String noValue = Objects.toString(fallbackResolver.get(), "");
        return this.property(name, guild -> formatDate(guild, timeSupplier, messages.dateFormat, noValue), fallbackResolver)
                .property(name + "-time", guild -> formatTime( guild, timeSupplier, noValue), fallbackResolver);
    }

    private static String formatDate(Guild guild, Function<Guild, Instant> timeFunction, FunnyTimeFormatter formatter, String noValue) {
        Instant endTime = timeFunction.apply(guild);
        return endTime.isBefore(Instant.now())
                ? noValue
                : formatter.format(endTime);
    }

    private static String formatTime(Guild guild, Function<Guild, Instant> timeFunction, String noValue) {
        Instant endTime = timeFunction.apply(guild);
        return endTime.isBefore(Instant.now())
                ? noValue
                : TimeUtils.formatTime(Duration.between(endTime, Instant.now()));
    }
    @Override
    public GuildPlaceholders create() {
        return new GuildPlaceholders();
    }

}
