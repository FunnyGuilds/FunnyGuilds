package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import java.text.SimpleDateFormat;

public class SimpleDateFormatTransformer extends BidirectionalTransformer<String, SimpleDateFormat> {

    @Override
    public GenericsPair<String, SimpleDateFormat> getPair() {
        return this.genericsPair(String.class, SimpleDateFormat.class);
    }

    @Override
    public SimpleDateFormat leftToRight(String data, SerdesContext serdesContext) {
        return new SimpleDateFormat(data);
    }

    @Override
    public String rightToLeft(SimpleDateFormat data, SerdesContext serdesContext) {
        return data.toPattern();
    }

}
