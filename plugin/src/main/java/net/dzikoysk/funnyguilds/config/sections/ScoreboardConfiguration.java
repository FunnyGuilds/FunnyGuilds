package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.time.Duration;
import net.dzikoysk.funnyguilds.config.RawString;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class ScoreboardConfiguration extends OkaeriConfig {

    @Comment("Czy systemy oparte na scoreboardzie (podane niżej) powinny być włączone?")
    public boolean enabled = true;

    @Comment("")
    @Comment("Czy FunnyGuilds powinno korzystać ze współdzielonego scoreboarda")
    @Comment("Ta opcja pozwala na wspólne działanie pluginu FunnyGuilds oraz innych pluginów modyfikujących scoreboard")
    @Comment("UWAGA: opcja eksperymentalna i może powodować błędy przy wyświetlaniu rzeczy zależnych od scoreboardów!")
    public boolean useSharedScoreboard = false;

    @Comment("")
    public NameTag nametag = new NameTag();

    public static class NameTag extends OkaeriConfig {

        @Comment("Czy system nametagów powinien być włączony (wartość przed i po nicku gracza)")
        public boolean enabled = true;

        @Comment("")
        @Comment("Co jaki czas nametagi wszystkich graczy powinny być odświeżane (niezależnie od innych triggerów)")
        @Comment("Format: <wartość><jednostka><wartość><jednostka><...>")
        @Comment("Jednostki: s - sekundy, m - minuty, h - godziny")
        @Comment("Przykład: 1m30s")
        public Duration updateRate = Duration.ofMinutes(1);

        @Comment("")
        @Comment("Konfiguracja prefixu (wartość przed nickiem gracza)")
        @Comment("Wspierane placeholdery: {TAG}, {REL_TAG}, {NAME}, {POS}")
        @Comment("Wspierane jest również PlaceholderAPI (w tym relacyjne placeholdery)")
        public Value prefix = new Value(new RawString(""), new RawString("{REL_TAG} "));

        @Comment("")
        @Comment("Konfiguracja suffixu (wartość po nicku gracza)")
        @Comment("Wspierane placeholdery: {TAG}, {REL_TAG}, {NAME}, {POS}")
        @Comment("Wspierane jest również PlaceholderAPI (w tym relacyjne placeholdery)")
        public Value suffix = new Value();

        public static class Value extends OkaeriConfig {

            private RawString noGuild = new RawString("");
            private RawString ourGuild = new RawString("");
            private RawString alliesGuild = new RawString("");
            private RawString enemiesGuild = new RawString("");
            private RawString otherGuild = new RawString("");

            public Value(RawString noGuild, RawString ourGuild, RawString alliesGuild, RawString enemiesGuild, RawString otherGuild) {
                this.noGuild = noGuild;
                this.ourGuild = ourGuild;
                this.alliesGuild = alliesGuild;
                this.enemiesGuild = enemiesGuild;
                this.otherGuild = otherGuild;
            }

            public Value(RawString noGuild, RawString anyGuild) {
                this(noGuild, anyGuild, anyGuild, anyGuild, anyGuild);
            }

            public Value() {
            }

            public RawString getNoGuild() {
                return this.noGuild;
            }

            public RawString getOurGuild() {
                return this.ourGuild;
            }

            public RawString getAlliesGuild() {
                return this.alliesGuild;
            }

            public RawString getEnemiesGuild() {
                return this.enemiesGuild;
            }

            public RawString getOtherGuild() {
                return this.otherGuild;
            }

        }

    }

    @Comment("")
    public Dummy dummy = new Dummy();

    public static class Dummy extends OkaeriConfig {

        @Comment("Czy włączyć dummy z punktami (liczbę punktów pod nickiem gracza)")
        @Comment("UWAGA: zalecane jest wyłączenie tej opcji w przypadku konfliktów z BungeeCordem, więcej szczegółów tutaj: https://github.com/FunnyGuilds/FunnyGuilds/issues/769")
        public boolean enabled = true;

        @Comment("")
        @Comment("Co jaki czas dummy wszystkich graczy powinny być odświeżane (niezależnie od innych triggerów)")
        @Comment("Format: <wartość><jednostka><wartość><jednostka><...>")
        @Comment("Jednostki: s - sekundy, m - minuty, h - godziny")
        @Comment("Przykład: 1m30s")
        public Duration updateRate = Duration.ofMinutes(1);

        @Comment("")
        @Comment("Wygląd nazwy wyświetlanej za punktami")
        public RawString suffix = new RawString("pkt");

    }

    @Comment("")
    public QueueConfiguration queueConfiguration = new QueueConfiguration();

    public static class QueueConfiguration extends OkaeriConfig {

        @Comment("Co jaki czas kolejka (nametagów/dummy) powinna być odświeżana")
        @Comment("Wartość podawana w tickach (1 sekunda = 20 ticków)")
        @Comment("Wyższe wartości mogą powodować opóźnienia w aktualizacji nametagów/dummy, ale zmniejszają ryzyko lagów")
        public int updateRate = 2;

        @Comment("")
        @Comment("Maksymalna liczba update'ów w trakcie trwania jednego ticku")
        @Comment("Wyższe wartości zmniejszają ryzyko opóźnień podczas aktualizacji nametagów/dummy, ale mogą powodować lagi")
        public int maxUpdatesInTick = 100;


    }

}
