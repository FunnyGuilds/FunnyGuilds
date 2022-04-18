package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.dzikoysk.funnyguilds.config.FunnyItemStack;

public class FunnyItemStackTransformer extends BidirectionalTransformer<String, FunnyItemStack> {

    @Override
    public GenericsPair<String, FunnyItemStack> getPair() {
        return this.genericsPair(String.class, FunnyItemStack.class);
    }

    @Override
    public FunnyItemStack leftToRight(String data, SerdesContext serdesContext) {
        return FunnyItemStack.parse(data);
    }

    @Override
    public String rightToLeft(FunnyItemStack data, SerdesContext serdesContext) {
        return data.getItemString();
    }

}
