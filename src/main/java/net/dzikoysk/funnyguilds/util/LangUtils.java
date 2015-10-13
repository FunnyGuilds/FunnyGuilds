package net.dzikoysk.funnyguilds.util;

public class LangUtils {

    public static String get(boolean b) {
        if (b)
            return "Tak";
        return "Nie";
    }

    public static String get(String s) {
        if (s == null)
            return "Brak";
        return s;
    }

}
