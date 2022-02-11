package net.dzikoysk.funnyguilds.config.range.formatting;

import java.util.List;
import java.util.Map;
import net.dzikoysk.funnyguilds.config.range.NumberRange;
import panda.std.stream.PandaStream;

public class NumberRangeFormatting extends RangeFormatting<NumberRange> {

    public NumberRangeFormatting(NumberRange range, String value) {
        super(range, value);
    }

    public NumberRangeFormatting(Number minRange, Number maxRange, String value) {
        this(new NumberRange(minRange, maxRange), value);
    }

    public static Map<NumberRange, String> toNumberRangeMap(List<NumberRangeFormatting> formattingList) {
        return PandaStream.of(formattingList)
                .toMap(NumberRangeFormatting::getRange, NumberRangeFormatting::getValue);
    }

}
