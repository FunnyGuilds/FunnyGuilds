package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.dzikoysk.funnyguilds.config.RangeFormatting;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import org.jetbrains.annotations.NotNull;

public class RangeFormattingTransformer extends BidirectionalTransformer<String, RangeFormatting> {

    @Override
    public GenericsPair<String, RangeFormatting> getPair() {
        return this.genericsPair(String.class, RangeFormatting.class);
    }

    @Override
    public RangeFormatting leftToRight(@NotNull String data, @NotNull SerdesContext serdesContext) {
        return new RangeFormatting(ChatUtils.colored(data));
    }

    @Override
    public String rightToLeft(RangeFormatting data, @NotNull SerdesContext serdesContext) {
        return ChatUtils.decolor(data.toString());
    }

}
