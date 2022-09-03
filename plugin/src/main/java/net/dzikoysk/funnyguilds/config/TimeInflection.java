package net.dzikoysk.funnyguilds.config;

import eu.okaeri.configs.OkaeriConfig;

public class TimeInflection extends OkaeriConfig {

    private String singularNominativeForm;
    private String singularAccusativeForm;
    private String doubleForm;
    private String pluralForm;
    private String shortForm;

    public TimeInflection(String singularNominativeForm, String singularAccusativeForm, String doubleForm, String pluralForm, String shortForm) {
        this.singularNominativeForm = singularNominativeForm;
        this.singularAccusativeForm = singularAccusativeForm;
        this.doubleForm = doubleForm;
        this.pluralForm = pluralForm;
        this.shortForm = shortForm;
    }

    public TimeInflection(String singularNominativeForm, String doubleForm, String pluralForm, String shortForm) {
        this(singularNominativeForm, singularNominativeForm, doubleForm, pluralForm, shortForm);
    }

    public TimeInflection(String singularForm, String doubleForm, String shortForm) {
        this(singularForm, doubleForm, doubleForm, shortForm);
    }

    public String getSingularNominativeForm() {
        return this.singularNominativeForm;
    }

    public String getSingularAccusativeForm() {
        return this.singularAccusativeForm;
    }

    public String getDoubleForm() {
        return this.doubleForm;
    }

    public String getPluralForm() {
        return this.pluralForm;
    }

    public String getShortForm() {
        return this.shortForm;
    }

    public String getForm(long amount) {
        return this.getForm(amount, Case.NOMINATIVE);
    }

    public String getForm(long amount, Case inflectionCase) {
        if (inflectionCase == Case.SHORT) {
            return this.shortForm;
        }

        if (amount == 1) {
            if (inflectionCase == Case.ACCUSATIVE) {
                return this.singularAccusativeForm;
            }

            return this.singularNominativeForm;
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

    public String getFormatted(long amount, Case inflectionCase) {
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
