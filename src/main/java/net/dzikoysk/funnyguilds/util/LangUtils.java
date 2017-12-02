package net.dzikoysk.funnyguilds.util;

public final class LangUtils {

    private LangUtils() {
    }

    public static String get(boolean b) {
        return b ? "Tak" : "Nie";
    }

    public static String get(String s) {
        return (s != null) ? s : "Brak";
    }
}
