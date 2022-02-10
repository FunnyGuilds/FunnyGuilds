package net.dzikoysk.funnyguilds.config;

import java.util.List;
import java.util.Map;
import panda.std.stream.PandaStream;

public class NumberFormatting {

    public final NumberRange range;
    public final String value;

    public NumberFormatting(NumberRange range, String value) {
        this.range = range;
        this.value = value;
    }

    public NumberFormatting(Number minRange, Number maxRange, String value) {
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

    public static Map<NumberRange, String> toNumberRangeMap(List<NumberFormatting> formattingList) {
        return PandaStream.of(formattingList)
                .toMap(NumberFormatting::getRange, NumberFormatting::getValue);
    }

}
