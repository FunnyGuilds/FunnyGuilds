package net.dzikoysk.funnyguilds.shared;

import java.util.Arrays;

public enum TimeDivision {

    SECOND(1000L, "sekunda", "sekundę", "sekundy", "sekund", "s", "s", "sek"),
    MINUTE(60000L, "minuta", "minutę", "minuty", "minut", "m", "m", "min"),
    HOUR(3600000L, "godzina", "godzinę", "godziny", "godzin", "h", "h", "godz"),
    DAY(86400000L, "dzień", "dzień", "dni", "dni", "d", "d", "dni", "day"),
    WEEK(604800000L, "tydzień, ", "tydzień", "tygodnie", "tygodni", "w", "w", "t", "tyg"),
    MONTH(2592000000L, "miesiąc", "miesiąc", "miesiące", "miesięcy", "ms", "mo", "ms", "mies"),
    YEAR(31536000000L, "rok", "rok", "lata", "lata", "r", "y", "r", "l", "lat", "rok");

    private final long millis;

    private final String singularFormNominative;
    private final String singularFormAccusative;
    private final String doubleForm;
    private final String pluralForm;
    private final String shortForm;

    private final String[] abbreviations;

    TimeDivision(long millis, String singularFormNominative, String singularFormAccusative, String doubleForm, String pluralForm, String shortForm, String... abbreviations) {
        this.millis = millis;

        this.singularFormNominative = singularFormNominative;
        this.singularFormAccusative = singularFormAccusative;
        this.doubleForm = doubleForm;
        this.pluralForm = pluralForm;
        this.shortForm = shortForm;

        this.abbreviations = abbreviations;
    }

    public long getMillis() {
        return this.millis;
    }

    public String getForm(long amount) {
        return this.getForm(amount, Form.NOMINATIVE);
    }

    public String getForm(long amount, Form form) {
        if (form == Form.SHORT) {
            return this.shortForm;
        }

        if (amount == 1) {
            if (form == Form.ACCUSATIVE) {
                return this.singularFormAccusative;
            }

            return this.singularFormNominative;
        }

        final long onesNumber = amount % 10;
        final long tensNumber = amount % 100;

        if (onesNumber < 2 || onesNumber > 4) {
            return this.pluralForm;
        }

        if (tensNumber >= 12 && tensNumber <= 14) {
            return this.pluralForm;
        }

        return this.doubleForm;
    }

    public String getFormatted(long amount, Form form) {
        if (form == Form.SHORT) {
            return amount + this.getShortForm();
        }

        return amount + " " + this.getForm(amount, form);
    }

    public String getShortForm() {
        return this.shortForm;
    }

    public String[] getAbbreviations() {
        return Arrays.copyOf(this.abbreviations, this.abbreviations.length);
    }

    public enum Form {

        NOMINATIVE,
        ACCUSATIVE,
        SHORT

    }

}
