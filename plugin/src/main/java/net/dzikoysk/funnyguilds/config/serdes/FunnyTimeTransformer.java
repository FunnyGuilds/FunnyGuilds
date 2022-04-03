package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import java.time.LocalTime;
import net.dzikoysk.funnyguilds.config.FunnyTime;

public class FunnyTimeTransformer extends BidirectionalTransformer<String, FunnyTime> {

    @Override
    public GenericsPair<String, FunnyTime> getPair() {
        return this.genericsPair(String.class, FunnyTime.class);
    }

    @Override
    public FunnyTime leftToRight(String data, SerdesContext serdesContext) {
        return new FunnyTime(LocalTime.parse(data, FunnyTime.TIME_FORMATTER));
    }

    @Override
    public String rightToLeft(FunnyTime data, SerdesContext serdesContext) {
        return data.getFormattedTime();
    }

}
