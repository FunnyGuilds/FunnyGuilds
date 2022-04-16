package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.dzikoysk.funnyguilds.config.NumberRange;

public class NumberRangeTransformer extends BidirectionalTransformer<String, NumberRange> {

    @Override
    public GenericsPair<String, NumberRange> getPair() {
        return this.genericsPair(String.class, NumberRange.class);
    }

    @Override
    public NumberRange leftToRight(String data, SerdesContext serdesContext) {
        return new NumberRange(data);
    }

    @Override
    public String rightToLeft(NumberRange data, SerdesContext serdesContext) {
        return data.toString();
    }
}
