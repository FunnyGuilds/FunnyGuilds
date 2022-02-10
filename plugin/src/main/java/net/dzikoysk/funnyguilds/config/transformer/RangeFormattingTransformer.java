package net.dzikoysk.funnyguilds.config.transformer;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.RangeFormatting;

public class RangeFormattingTransformer extends BidirectionalTransformer<String, RangeFormatting> {

    private static final Pattern RANGE_PATTERN = Pattern.compile("(-?[0-9.*]+)-(-?[0-9.*]+)");

    @Override
    public GenericsPair<String, RangeFormatting> getPair() {
        return this.genericsPair(String.class, RangeFormatting.class);
    }

    @Override
    public RangeFormatting leftToRight(String data, SerdesContext serdesContext) {
        String[] split = data.split(" ");

        Matcher matcher = RANGE_PATTERN.matcher(split[0]);

        Number min = Integer.MIN_VALUE;
        Number max = Integer.MAX_VALUE;
        if (matcher.matches()) {
            min = parseNumber(matcher.group(1), Integer.MIN_VALUE);
            max = parseNumber(matcher.group(2), Integer.MAX_VALUE);
        }

        return new RangeFormatting(new NumberRange(min, max), split[1]);
    }

    @Override
    public String rightToLeft(RangeFormatting data, SerdesContext serdesContext) {
        return data.toString();
    }

    private static Number parseNumber(String numberString, Number fallback) {
        try {
            if (numberString.contains("*")) {
                return fallback;
            }
            else {
                if (numberString.contains(".")) {
                    return Float.parseFloat(numberString);
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
