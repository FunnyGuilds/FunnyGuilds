package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.validator.annotation.DecimalMin;
import eu.okaeri.validator.annotation.Min;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class SecuritySystemConfiguration extends OkaeriConfig {

    @Comment("Czy system bezpieczeństwa (prosty anti-cheat) ma byc włączony?")
    public boolean enabled = true;

    @Min(0)
    @Comment("")
    @Comment("Maksymalna liczba naruszeń (reach/freecam) w ciągu 10 minutach, po których graczowi zostanie zablokowane wchodzenie w interakcje z sercem gildii")
    public int maxViolations = 2;

    @Comment("")
    public Reach reach = new Reach();

    public static class Reach extends OkaeriConfig {

        @DecimalMin("0")
        @Comment("Z jakiej odległości gracz może wejść w interakcje z sercem gildii w trybie survival")
        public double survivalReach = 3.75;

        @DecimalMin("0")
        @Comment("")
        @Comment("Z jakiej odległości gracz może wejść w interakcje z sercem gildii w trybie creative")
        public double creativeReach = 5.0;

        @DecimalMin("0")
        @Comment("")
        @Comment("Margines sprawdzania z jak daleka gracz uderzył serce gildii")
        @Comment("Jeśli dostajesz fałszywe alarmy od Security - zwiększ tę wartość do około 0.50 lub więcej")
        public double compensation = 0.25;

    }

    @Comment("")
    @CustomKey("freecam")
    public FreeCam freeCam = new FreeCam();

    public static class FreeCam extends OkaeriConfig {

        @Min(0)
        @Comment("Margines sprawdzania przez ile bloków gracz uderzył serce gildii")
        public int compensation = 0;

    }

}
