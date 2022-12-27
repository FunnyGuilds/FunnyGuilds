package net.dzikoysk.funnyguilds.shared;

import java.util.Arrays;

public enum TimeDivision {

    SECOND(1000L, "s", "sek"),
    MINUTE(60000L, "m", "min"),
    HOUR(3600000L, "h", "godz"),
    DAY(86400000L, "d", "dni", "day"),
    WEEK(604800000L, "w", "t", "tyg"),
    MONTH(2592000000L, "mo", "ms", "mc", "mies"),
    YEAR(31536000000L,  "y", "r", "l", "lat", "rok");

    private final long millis;

    private final String[] abbreviations;

    TimeDivision(long millis, String... abbreviations) {
        this.millis = millis;
        this.abbreviations = abbreviations;
    }

    public long getMillis() {
        return this.millis;
    }

    public String[] getAbbreviations() {
        return Arrays.copyOf(this.abbreviations, this.abbreviations.length);
    }



}
