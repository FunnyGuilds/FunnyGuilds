package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;

import java.time.LocalTime;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class TntProtectionConfiguration extends OkaeriConfig {

    public TimeConfig time = new TimeConfig();

    public static class TimeConfig extends OkaeriConfig {

        @Comment("Czy wlaczyc ochrone przed TNT w gildiach w podanych godzinach")
        public boolean enabled = false;

        @Comment("Czy wlaczyc ochrone przed TNT na całym serwerze w podanych godzinach")
        public boolean enabledGlobal = false;

        @Comment("O ktorej godzinie ma sie zaczac ochrona przed TNT w gildii")
        @Comment("Godzina w formacie HH:mm")
        @CustomKey("start-time")
        public String startTime_ = "22:00";
        @Exclude
        public LocalTime startTime;

        @Comment("Do ktorej godziny ma dzialac ochrona przed TNT w gildii")
        @Comment("Godzina w formacie HH:mm")
        @CustomKey("end-time")
        public String endTime_ = "06:00";
        @Exclude
        public LocalTime endTime;
        @Exclude
        public boolean passingMidnight;

    }

    public BuildConfig build = new BuildConfig();

    public static class BuildConfig extends OkaeriConfig {

        @Comment("Minimalna wysokosc od ktorej mozna stawiac TNT.")
        public int minHeight = 0;

        @Comment("Maksymalna wysokosc do ktorej mozna stawiac TNT.")
        public int maxHeight = 255;

    }

    public ExplodeConfig explode = new ExplodeConfig();

    public static class ExplodeConfig extends OkaeriConfig {

        @Comment("Minimalna wysokosc od ktorej TNT wybucha.")
        public int minHeight = 0;

        @Comment("Maksymalna wysokosc do ktorej TNT wybucha.")
        public int maxHeight = 255;

    }

}