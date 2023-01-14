package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.dzikoysk.funnyguilds.config.FunnyTimeFormatter;
import org.jetbrains.annotations.NotNull;

public class FunnyTimeFormatterTransformer extends BidirectionalTransformer<String, FunnyTimeFormatter> {

    @Override
    public GenericsPair<String, FunnyTimeFormatter> getPair() {
        return this.genericsPair(String.class, FunnyTimeFormatter.class);
    }

    @Override
    public FunnyTimeFormatter leftToRight(@NotNull String data, @NotNull SerdesContext serdesContext) {
        return new FunnyTimeFormatter(data);
    }

    @Override
    public String rightToLeft(FunnyTimeFormatter data, @NotNull SerdesContext serdesContext) {
        return data.getFormat();
    }

}
