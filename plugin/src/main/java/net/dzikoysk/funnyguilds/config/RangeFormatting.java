package net.dzikoysk.funnyguilds.config;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import panda.std.stream.PandaStream;

public class RangeFormatting {

    private static final Pattern RANGE_PATTERN = Pattern.compile("(-?[0-9.*]+)-(-?[0-9.*]+)");

    private final NumberRange range;
    private String value;

    public RangeFormatting(NumberRange range, String value) {
        this.range = range;
        this.value = value;
    }

    public RangeFormatting(Number min, Number max, String value) {
        this(new NumberRange(min, max), value);
    }

    public RangeFormatting(String string) {
        String[] split = string.split(" ");
        this.range = new NumberRange(split[0]);
        this.value = split[1];
    }

    public NumberRange getRange() {
        return range;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.range.toString() + " " + this.value;
    }

    public static Map<NumberRange, String> toRangeMap(List<RangeFormatting> formattingList) {
        return PandaStream.of(formattingList)
                .toMap(RangeFormatting::getRange, RangeFormatting::getValue);
    }
}
