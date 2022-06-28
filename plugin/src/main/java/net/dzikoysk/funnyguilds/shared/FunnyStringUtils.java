package net.dzikoysk.funnyguilds.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import panda.utilities.text.Joiner;

public final class FunnyStringUtils {

    private FunnyStringUtils() {
    }

    public static String join(Collection<String> strings, boolean insertSpaces) {
        return join(strings, insertSpaces ? ", " : ",");
    }

    public static String join(Collection<String> strings) {
        return join(strings, false);
    }

    public static String join(Collection<String> strings, String separator) {
        return Joiner.on(separator).join(strings).toString();
    }

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static String replace(String string, String pattern, String replacement) {
        return StringUtils.replace(string, pattern, replacement);
    }

    public static List<String> fromString(String string) {
        List<String> list = new ArrayList<>();
        if (isEmpty(string)) {
            return list;
        }

        return Arrays.asList(string.split(","));
    }

    public static String appendDigit(int number) {
        return number > 9 ? Integer.toString(number) : "0" + number;
    }

    public static String appendDigit(String number) {
        return number.length() > 1 ? number : "0" + number;
    }

    public static String getPercent(double dividend, double divisor) {
        return getPercent(dividend / divisor);
    }

    public static String getPercent(double fraction) {
        return String.format(Locale.US, "%.1f", 100.0D * fraction);
    }

}
