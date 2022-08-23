package net.dzikoysk.funnyguilds.shared;

public enum TimeDivision {

    SECOND(1000L, " sekunda", " sekundy", " sekund", "s", "s", "sek"),
    MINUTE(60000L, " minuta", " minuty", " minut", "m", "m", "min"),
    HOUR(3600000L, " godzina", " godziny", " godzin", "h", "h", "godz"),
    DAY(86400000L, " dzień", " dni", " dni", "d", "d", "dni", "day"),
    WEEK(604800000L, " tydzień", " tygodnie", " tygodni", "w", "w", "t", "tyg"),
    MONTH(2592000000L, " miesiąc", " miesiące", " miesięcy", "ms", "mo", "ms", "mies"),
    YEAR(31536000000L, " rok", " lata", " lata", "r", "y", "r", "l", "lat", "rok");

    private final long millis;

    private final String singularForm;
    private final String doubleForm;
    private final String pluralForm;

    private final String shortForm;

    private final String[] abbreviations;

    TimeDivision(long millis, String singularForm, String doubleForm, String pluralForm, String shortForm, String... abbreviations) {
        this.millis = millis;

        this.singularForm = singularForm;
        this.doubleForm = doubleForm;
        this.pluralForm = pluralForm;
        this.shortForm = shortForm;

        this.abbreviations = abbreviations;
    }

    public long getMillis() {
        return this.millis;
    }

    public String getShortForm() {
        return this.shortForm;
    }

    public String getForm(long amount, boolean shortForm) {
        if (shortForm) {
            return this.shortForm;
        }

        if (amount == 1) {
            return this.singularForm;
        }

        long onesNumber = amount % 10;
        long tensNumber = amount % 100;

        if (onesNumber < 2 || onesNumber > 4) {
            return this.pluralForm;
        }

        if (tensNumber >= 12 && tensNumber <= 14) {
            return this.pluralForm;
        }

        return this.doubleForm;
    }

    public String getForm(long amount) {
        return this.getForm(amount, false);
    }

    public String[] getAbbreviations() {
        return this.abbreviations;
    }
}
