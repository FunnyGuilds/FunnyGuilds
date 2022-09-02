package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.dzikoysk.funnyguilds.config.FunnyPattern;
import org.jetbrains.annotations.NotNull;

public class FunnyPatternTransformer extends BidirectionalTransformer<String, FunnyPattern> {

    @Override
    public GenericsPair<String, FunnyPattern> getPair() {
        return this.genericsPair(String.class, FunnyPattern.class);
    }

    @Override
    public FunnyPattern leftToRight(@NotNull String data, @NotNull SerdesContext serdesContext) {
        return new FunnyPattern(data);
    }

    @Override
    public String rightToLeft(@NotNull FunnyPattern pattern, @NotNull SerdesContext serdesContext) {
        return pattern.getPattern();
    }
}
