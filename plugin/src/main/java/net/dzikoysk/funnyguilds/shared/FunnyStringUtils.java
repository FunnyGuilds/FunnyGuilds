package net.dzikoysk.funnyguilds.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import panda.utilities.text.Joiner;

public final class FunnyStringUtils {

    private FunnyStringUtils() {
    }

    public static String join(Iterable<String> strings, boolean insertSpaces) {
        return join(strings, insertSpaces ? ", " : ",");
    }

    public static String join(Iterable<String> strings) {
        return join(strings, false);
    }

    public static String join(Iterable<String> strings, String separator) {
        return Joiner.on(separator).join(strings).toString();
    }

    public static List<String> fromString(String s) {
        List<String> list = new ArrayList<>();
        if (s == null || s.isEmpty()) {
            return list;
        }

        return Arrays.asList(s.split(","));
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
