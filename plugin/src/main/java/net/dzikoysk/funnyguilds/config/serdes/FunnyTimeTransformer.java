package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import java.time.LocalTime;
import net.dzikoysk.funnyguilds.config.FunnyTime;
import org.jetbrains.annotations.NotNull;

public class FunnyTimeTransformer extends BidirectionalTransformer<String, FunnyTime> {

    @Override
    public GenericsPair<String, FunnyTime> getPair() {
        return this.genericsPair(String.class, FunnyTime.class);
    }

    @Override
    public FunnyTime leftToRight(@NotNull String data, @NotNull SerdesContext serdesContext) {
        // standard parse from string
        if (data.contains(":")) {
            return new FunnyTime(LocalTime.parse(data, FunnyTime.TIME_FORMATTER));
        }

        // something went wrong, probably yaml spec, whoopsie... https://noyaml.com/
        int value;
        try {
            value = Integer.parseInt(data);
        }
        catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Cannot resolve time from " + data, exception);
        }

        // the value is above 24 hours in both cases
        if (value > 86400) {
            throw new IllegalArgumentException("Cannot resolve time from " + value);
        }

        // value in minutes is above 24 hours
        // restore as seconds from midnight
        // 00:00 - 00:24 may have bad time
        if (value > 1440) {
            int hours = value / 3600;
            int minutes = (value % 3600) / 60;
            return new FunnyTime(LocalTime.of(hours, minutes));
        }

        // restore as minutes from midnight
        int hours = value / 60;
        int minutes = value % 60;
        return new FunnyTime(LocalTime.of(hours, minutes));
    }

    @Override
    public String rightToLeft(@NotNull FunnyTime data, @NotNull SerdesContext serdesContext) {
        return data.getFormattedTime();
    }

}
