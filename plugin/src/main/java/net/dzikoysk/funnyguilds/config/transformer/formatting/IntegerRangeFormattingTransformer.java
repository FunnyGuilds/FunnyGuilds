package net.dzikoysk.funnyguilds.config.transformer.formatting;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.config.range.formatting.IntegerRangeFormatting;

public class IntegerRangeFormattingTransformer extends BidirectionalTransformer<String, IntegerRangeFormatting> {

    private static final Pattern RANGE_PATTERN = Pattern.compile("(-?[0-9*]+)-(-?[0-9*]+)");

    @Override
    public GenericsPair<String, IntegerRangeFormatting> getPair() {
        return this.genericsPair(String.class, IntegerRangeFormatting.class);
    }

    @Override
    public IntegerRangeFormatting leftToRight(String data, SerdesContext serdesContext) {
        String[] split = data.split(" ");

        Matcher matcher = RANGE_PATTERN.matcher(split[0]);

        int min = Integer.MIN_VALUE;
        int max = Integer.MAX_VALUE;
        if (matcher.matches()) {
            min = parseInteger(matcher.group(1), Integer.MIN_VALUE);
            max = parseInteger(matcher.group(2), Integer.MAX_VALUE);
        }

        return new IntegerRangeFormatting(min, max, split[1]);
    }

    @Override
    public String rightToLeft(IntegerRangeFormatting data, SerdesContext serdesContext) {
        return data.toString();
    }

    private static int parseInteger(String numberString, int fallback) {
        try {
            if (numberString.contains("*")) {
                return fallback;
            }
            else {
                return Integer.parseInt(numberString);
            }
        }
        catch (NumberFormatException ex) {
            ex.printStackTrace();
            return fallback;
        }
    }

}
