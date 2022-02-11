package net.dzikoysk.funnyguilds.config.range.formatting;

import java.util.List;
import java.util.Map;
import net.dzikoysk.funnyguilds.config.range.IntegerRange;
import panda.std.stream.PandaStream;

public class IntegerRangeFormatting extends RangeFormatting<IntegerRange> {

    public IntegerRangeFormatting(IntegerRange range, String value) {
        super(range, value);
    }

    public IntegerRangeFormatting(int minRange, int maxRange, String value) {
        this(new IntegerRange(minRange, maxRange), value);
    }

    public static Map<IntegerRange, String> toIntegerRangeMap(List<IntegerRangeFormatting> formattingList) {
        return PandaStream.of(formattingList)
                .toMap(IntegerRangeFormatting::getRange, IntegerRangeFormatting::getValue);
    }

}
