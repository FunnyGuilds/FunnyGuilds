package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.validator.annotation.Min;
import eu.okaeri.validator.annotation.PositiveOrZero;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import eu.okaeri.configs.serdes.commons.duration.DurationSpec;

public class WarConfiguration extends OkaeriConfig {

    @Comment("Tryb systemu wojen:")
    @Comment(" LEGACY - klasyczne wypowiadanie wojen, brak regeneracji zyc, brak nagrod za wygrane")
    @Comment(" MODERN - dynamiczne zdobywanie zyc za podbicia i wygrane wojny, serie zwyciestw (streak), wykup serc")
    public WarMode mode = WarMode.MODERN;

    @Comment("")
    @Comment("Czy system podbijania i wojen gildii ma byc wlaczony")
    public boolean enabled = true;

    @Min(1)
    @Comment("")
    @Comment("Bazowa liczba zyc nowej gildii")
    public int lives = 3;

    @Min(1)
    @Comment("")
    @Comment("Maksymalna liczba zyc, jaka gildia moze osiagnac przez zdobywanie w walce lub wykup")
    @CustomKey("max-lives")
    public int maxLives = 5;

    @Min(1)
    @Comment("")
    @Comment("Ile HP ma pojedyncze serce gildii")
    @Comment("Po zbiciu HP serca gildia traci 1 zycie, a serce odnawia HP")
    @Comment("Wartosc 1 efektywnie wylacza mechanike HP serca - kazde uderzenie od razu odbiera zycie")
    @CustomKey("heart-lives")
    public int heartLives = 1;

    @Comment("")
    @Comment("Czy atakujaca gildia zyskuje +1 zycie po zbiciu zycia gildii bronionej (do limitu max-lives, tylko w trybie MODERN)")
    @CustomKey("conquer-gain-life")
    public boolean conquerGainLife = true;

    @Comment("")
    @Comment("Czy wygranie wojny (calkowite zniszczenie gildii) przyznaje +1 zycie zwyciezcy (do limitu max-lives, tylko w trybie MODERN)")
    @CustomKey("win-gain-life")
    public boolean winGainLife = true;

    @Comment("")
    @Comment("Czy do zaatakowania gildii wymagana jest aktywna wojna (komenda /g war)")
    @Comment("Przy ustawieniu na false zachowanie jest zgodne ze starym - mozna atakowac kazda nie-sojusznicza gildie bez wypowiadania wojny")
    @CustomKey("require-declaration")
    public boolean requireDeclaration = true;

    @PositiveOrZero
    @DurationSpec(fallbackUnit = ChronoUnit.HOURS)
    @Comment("")
    @Comment("Po jakim czasie od zalozenia mozna zaatakowac gildie")
    public Duration protection = Duration.ofHours(24);

    @PositiveOrZero
    @DurationSpec(fallbackUnit = ChronoUnit.HOURS)
    @Comment("")
    @Comment("Ile czasu trzeba czekac do nastepnego ataku na te sama gildie")
    public Duration wait = Duration.ofHours(24);

    @Comment("")
    @Comment("Czy gildia podczas okresu ochronnego ma posiadac ochrone przeciw TNT")
    @CustomKey("tnt-protection")
    public boolean tntProtection = true;

    @Comment("")
    @Comment("Ochrona anty-farmingowa zdobywania zyc i punktow wojennych")
    @CustomKey("anti-farming")
    public AntiFarmingConfiguration antiFarming = new AntiFarmingConfiguration();

    @Comment("")
    @Comment("System serii zwyciestw (win-streak)")
    public StreakConfiguration streak = new StreakConfiguration();

    public enum WarMode {
        LEGACY,
        MODERN
    }

    public static class AntiFarmingConfiguration extends OkaeriConfig {
        @Comment("Czy ochrona anty-farmingowa ma byc wlaczona")
        public boolean enabled = true;

        @Min(1)
        @Comment("Minimalna liczba czlonkow gildii ofiary, aby agresor mogl zdobyc zycie")
        @CustomKey("min-victim-members")
        public int minVictimMembers = 2;

        @DurationSpec(fallbackUnit = ChronoUnit.DAYS)
        @Comment("Minimalny wiek gildii ofiary, aby agresor mogl zdobyc zycie")
        @CustomKey("min-victim-age")
        public Duration minVictimAge = Duration.ofDays(3);

        @DurationSpec(fallbackUnit = ChronoUnit.HOURS)
        @Comment("Czasowy odstep ponownego zdobycia zycia na tej samej gildii")
        @CustomKey("victim-cooldown")
        public Duration victimCooldown = Duration.ofHours(12);

        @Comment("Czy blokowac zdobywanie zyc jesli atakujacy i ofiara maja ten sam adres IP")
        @CustomKey("protect-same-ip")
        public boolean protectSameIp = true;
    }

    public static class StreakConfiguration extends OkaeriConfig {
        @Comment("Czy serie zwyciestw maja byc aktywne")
        public boolean enabled = true;

        @Comment("Kamienie milowe (liczba zwyciestw z rzedu), przy ktorych wysylany jest globalny komunikat")
        @CustomKey("broadcast-milestones")
        public List<Integer> broadcastMilestones = Arrays.asList(3, 5, 10, 15, 20, 25, 50);
    }
}
