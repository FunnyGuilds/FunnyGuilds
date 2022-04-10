package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.dzikoysk.funnyguilds.config.RangeFormatting;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;

public class RangeFormattingTransformer extends BidirectionalTransformer<String, RangeFormatting> {

    @Override
    public GenericsPair<String, RangeFormatting> getPair() {
        return this.genericsPair(String.class, RangeFormatting.class);
    }

    @Override
    public RangeFormatting leftToRight(String data, SerdesContext serdesContext) {
        return new RangeFormatting(ChatUtils.colored(data));
    }

    @Override
    public String rightToLeft(RangeFormatting data, SerdesContext serdesContext) {
        return ChatUtils.decolor(data.toString());
    }

}
