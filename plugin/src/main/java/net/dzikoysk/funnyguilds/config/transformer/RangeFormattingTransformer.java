package net.dzikoysk.funnyguilds.config.transformer;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.dzikoysk.funnyguilds.config.RangeFormatting;

public class RangeFormattingTransformer extends BidirectionalTransformer<String, RangeFormatting> {

    @Override
    public GenericsPair<String, RangeFormatting> getPair() {
        return this.genericsPair(String.class, RangeFormatting.class);
    }

    @Override
    public RangeFormatting leftToRight(String data, SerdesContext serdesContext) {
        return new RangeFormatting(data);
    }

    @Override
    public String rightToLeft(RangeFormatting data, SerdesContext serdesContext) {
        return data.toString();
    }

}
