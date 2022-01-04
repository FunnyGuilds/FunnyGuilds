package net.dzikoysk.funnyguilds.config.transformer;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeTransformer extends BidirectionalTransformer<String, LocalTime> {

    public final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public GenericsPair<String, LocalTime> getPair() {
        return this.genericsPair(String.class, LocalTime.class);
    }

    @Override
    public LocalTime leftToRight(String data, SerdesContext serdesContext) {
        return LocalTime.parse(data, TIME_FORMATTER);
    }

    @Override
    public String rightToLeft(LocalTime data, SerdesContext serdesContext) {
        return data.format(TIME_FORMATTER);
    }

}
