package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.configs.serdes.commons.duration.DurationSpec;
import eu.okaeri.validator.annotation.PositiveOrZero;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class GuildAuditConfiguration extends OkaeriConfig {

    @Comment("Maksymalna liczba wpisów na gildię. 0 wyłącza dziennik.")
    @PositiveOrZero
    public int maxEntries = 200;

    @Comment("Wiek przechowywanych wpisów. 0 wyłącza limit wieku.")
    @PositiveOrZero
    @DurationSpec(fallbackUnit = ChronoUnit.DAYS)
    public Duration retention = Duration.ofDays(30);
}
