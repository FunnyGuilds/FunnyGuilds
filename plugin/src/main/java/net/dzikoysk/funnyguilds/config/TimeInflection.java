package net.dzikoysk.funnyguilds.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.pluralize.Pluralize;
import java.util.Locale;

public class TimeInflection extends OkaeriConfig {

    private Locale locale;
    private String[] nominativeInflection;
    private String[] accusativeInflection;
    private String shortForm;

    public TimeInflection(Locale locale, String[] nominativeInflection, String[] accusativeInflection, String shortForm) {
        this.locale = locale;
        this.nominativeInflection = nominativeInflection;
        this.accusativeInflection = accusativeInflection;
        this.shortForm = shortForm;
    }

    public TimeInflection(String[] nominativeInflection, String[] accusativeInflection, String shortForm) {
        this(Locale.forLanguageTag("pl"), nominativeInflection, accusativeInflection, shortForm);
    }

    public String getShortForm() {
        return this.shortForm;
    }

    public String getForm(int amount) {
        return this.getForm(amount, Case.NOMINATIVE);
    }

    public String getForm(int amount, Case inflectionCase) {
        if (inflectionCase == Case.SHORT) {
            return this.shortForm;
        }

        String[] inflection = this.nominativeInflection;
        if (inflectionCase == Case.ACCUSATIVE) {
            inflection = this.accusativeInflection;
        }

        return Pluralize.pluralize(this.locale, amount, inflection);
    }

    public String getFormatted(int amount, Case inflectionCase) {
        if (inflectionCase == Case.SHORT) {
            return amount + this.getShortForm();
        }

        return amount + " " + this.getForm(amount, inflectionCase);
    }

    public enum Case {

        NOMINATIVE,
        ACCUSATIVE,
        SHORT

    }

}
