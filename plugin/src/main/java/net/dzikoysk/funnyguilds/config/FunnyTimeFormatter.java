package net.dzikoysk.funnyguilds.config;

import net.dzikoysk.funnyguilds.FunnyGuilds;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FunnyTimeFormatter {

    private final String format;
    private final DateTimeFormatter formatter;

    public FunnyTimeFormatter(String format) {
        this.format = format;
        this.formatter = DateTimeFormatter.ofPattern(format, Locale.ROOT);
    }

    public String getFormat() {
        return this.format;
    }

    public String format(Instant instant) {
        return this.formatter.format(ZonedDateTime.ofInstant(instant,
                FunnyGuilds.getInstance().getPluginConfiguration().timeZone
        ));
    }

}
