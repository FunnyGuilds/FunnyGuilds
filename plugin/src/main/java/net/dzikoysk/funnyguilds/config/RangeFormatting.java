package net.dzikoysk.funnyguilds.config;

import java.util.List;
import java.util.Map;
import panda.std.stream.PandaStream;

public class RangeFormatting {

    public final NumberRange range;
    public final String value;

    public RangeFormatting(NumberRange range, String value) {
        this.range = range;
        this.value = value;
    }

    public RangeFormatting(Number minRange, Number maxRange, String value) {
        this(new NumberRange(minRange, maxRange), value);
    }

    public NumberRange getRange() {
        return range;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.range.toString() + " " + this.value;
    }

    public static Map<NumberRange, String> toNumberRangeMap(List<RangeFormatting> formattingList) {
        return PandaStream.of(formattingList)
                .toMap(RangeFormatting::getRange, RangeFormatting::getValue);
    }

}
