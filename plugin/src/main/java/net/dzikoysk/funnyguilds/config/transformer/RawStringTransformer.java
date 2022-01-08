package net.dzikoysk.funnyguilds.config.transformer;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.dzikoysk.funnyguilds.config.RawString;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;

public class RawStringTransformer extends BidirectionalTransformer<String, RawString> {

    @Override
    public GenericsPair<String, RawString> getPair() {
        return this.genericsPair(String.class, RawString.class);
    }

    @Override
    public RawString leftToRight(String data, SerdesContext serdesContext) {
        return new RawString(ChatUtils.colored(data));
    }

    @Override
    public String rightToLeft(RawString data, SerdesContext serdesContext) {
        return ChatUtils.decolor(data.getValue());
    }

}
