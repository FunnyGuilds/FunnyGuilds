package net.dzikoysk.funnyguilds.config.transformer.formatting;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.config.range.formatting.DoubleRangeFormatting;

public class DoubleRangeFormattingTransformer extends BidirectionalTransformer<String, DoubleRangeFormatting> {

    private static final Pattern RANGE_PATTERN = Pattern.compile("(-?[0-9.*]+)-(-?[0-9.*]+)");

    @Override
    public GenericsPair<String, DoubleRangeFormatting> getPair() {
        return this.genericsPair(String.class, DoubleRangeFormatting.class);
    }

    @Override
    public DoubleRangeFormatting leftToRight(String data, SerdesContext serdesContext) {
        String[] split = data.split(" ");

        Matcher matcher = RANGE_PATTERN.matcher(split[0]);

        double min = Integer.MIN_VALUE;
        double max = Integer.MAX_VALUE;
        if (matcher.matches()) {
            min = parseNumber(matcher.group(1), Integer.MIN_VALUE);
            max = parseNumber(matcher.group(2), Integer.MAX_VALUE);
        }

        return new DoubleRangeFormatting(min, max, split[1]);
    }

    @Override
    public String rightToLeft(DoubleRangeFormatting data, SerdesContext serdesContext) {
        return data.toString();
    }

    private static double parseNumber(String numberString, double fallback) {
        try {
            if (numberString.contains("*")) {
                return fallback;
            }
            else {
                if (numberString.contains(".")) {
                    return Double.parseDouble(numberString);
                }
                else {
                    return Integer.parseInt(numberString);
                }
            }
        }
        catch (NumberFormatException ex) {
            ex.printStackTrace();
            return fallback;
        }
    }

}
