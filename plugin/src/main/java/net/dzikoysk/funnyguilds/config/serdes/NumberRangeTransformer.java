package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.dzikoysk.funnyguilds.config.NumberRange;
import org.jetbrains.annotations.NotNull;

public class NumberRangeTransformer extends BidirectionalTransformer<String, NumberRange> {

    @Override
    public GenericsPair<String, NumberRange> getPair() {
        return this.genericsPair(String.class, NumberRange.class);
    }

    @Override
    public NumberRange leftToRight(@NotNull String data, @NotNull SerdesContext serdesContext) {
        return new NumberRange(data);
    }

    @Override
    public String rightToLeft(NumberRange data, @NotNull SerdesContext serdesContext) {
        return data.toString();
    }

}
