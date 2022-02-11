package net.dzikoysk.funnyguilds.config.range.formatting;

import java.util.List;
import java.util.Map;
import net.dzikoysk.funnyguilds.config.range.DoubleRange;
import panda.std.stream.PandaStream;

public class DoubleRangeFormatting extends RangeFormatting<DoubleRange> {

    public DoubleRangeFormatting(DoubleRange range, String value) {
        super(range, value);
    }

    public DoubleRangeFormatting(double minRange, double maxRange, String value) {
        this(new DoubleRange(minRange, maxRange), value);
    }

    public static Map<DoubleRange, String> toIntegerRangeMap(List<DoubleRangeFormatting> formattingList) {
        return PandaStream.of(formattingList)
                .toMap(DoubleRangeFormatting::getRange, DoubleRangeFormatting::getValue);
    }

}
