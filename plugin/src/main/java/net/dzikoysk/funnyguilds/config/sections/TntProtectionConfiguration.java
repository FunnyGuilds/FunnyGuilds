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

}