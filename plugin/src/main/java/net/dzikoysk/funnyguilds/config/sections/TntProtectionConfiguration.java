package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;

import java.time.LocalTime;

@Names(strategy = NameStrategy.IDENTITY)
public class TntProtectionConfiguration extends OkaeriConfig {

    public Time time = new Time();

    @Names(strategy = NameStrategy.IDENTITY)
    public static class Time extends OkaeriConfig {

        @Comment("Czy wlaczyc ochrone przed TNT w gildiach w podanych godzinach")
        public boolean enabled = false;

        @Comment("Czy wlaczyc ochrone przed TNT na całym serwerze w podanych godzinach")
        @CustomKey("enabled-global")
        public boolean enabledGlobal = false;

        @Comment("O której godzinie ma sie zaczac ochrona przed TNT w gildii")
        @Comment("Godzina w formacie HH:mm")
        @CustomKey("start-time")
        public String startTime_ = "22:00";
        @Exclude
        public LocalTime startTime;

        @Comment("Do której godziny ma dzialac ochrona przed TNT w gildii")
        @Comment("Godzina w formacie HH:mm")
        @CustomKey("end-time")
        public String endTime_ = "06:00";
        @Exclude
        public LocalTime endTime;
        @Exclude
        public boolean passingMidnight;

    }

    public Build build = new Build();

    @Names(strategy = NameStrategy.IDENTITY)
    public static class Build extends OkaeriConfig {

        @Comment("Minimalna wysokosc od ktorej mozna stawiac TNT.")
        @CustomKey("min-height")
        public int minHeight = 0;

        @Comment("Maksymalna wysokosc do ktorej mozna stawiac TNT.")
        @CustomKey("max-height")
        public int maxHeight = 255;

    }

    public Explode explode = new Explode();

    @Names(strategy = NameStrategy.IDENTITY)
    public static class Explode extends OkaeriConfig {

        @Comment("Minimalna wysokosc od ktorej TNT wybucha.")
        @CustomKey("min-height")
        public int minHeight = 0;

        @Comment("Maksymalna wysokosc do ktorej TNT wybucha.")
        @CustomKey("max-height")
        public int maxHeight = 255;

    }

}