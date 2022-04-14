package net.dzikoysk.funnyguilds.config.tablist;

import com.google.common.collect.ImmutableMap;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.validator.annotation.Min;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.dzikoysk.funnyguilds.nms.api.playerlist.SkinTexture;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class TablistConfiguration extends OkaeriConfig {

    @Comment("Wyglad listy graczy, przedzial slotow - od 1 do 80")
    @Comment("Schemat wygladu listy: https://github.com/FunnyGuilds/FunnyGuilds/blob/master/assets/tab-scheme.png")
    @Comment("> Spis zmiennych gracza:")
    @Comment("{PLAYER} - nazwa gracza")
    @Comment("{WORLD} - swiat, w ktorym znajduje sie gracz")
    @Comment("{PING} - ping gracza")
    @Comment("{PING-FORMAT} - ping gracza z formatowaniem")
    @Comment("{POINTS} - punkty gracza")
    @Comment("{POINTS-FORMAT} - punkty gracza z formatowaniem")
    @Comment("{POSITION} - pozycja gracza w rankingu")
    @Comment("{POSITION-<typ>} - pozycja gracza w rankingu dla danego typu rankingu")
    @Comment("{KILLS} - liczba zabojstw gracza")
    @Comment("{DEATHS} - liczba smierci gracza")
    @Comment("{ASSISTS} - liczba asyst gracza")
    @Comment("{LOGOUTS} - liczba wylogowań gracza podczas walki")
    @Comment("{KDR} - stosunek zabojstw do smierci gracza")
    @Comment("{WG-REGION} - region WorldGuard'a, na ktorym znajduje sie gracz (pierwszy, jesli jest ich kilka)")
    @Comment("{WG-REGIONS} - regiony WorldGuard'a, na ktorych znajduje sie gracz (oddzielone przecinkami)")
    @Comment("{VAULT-MONEY} - balans konta gracza pobierany z pluginu Vault")
    @Comment("> Spis zmiennych gildyjnych:")
    @Comment("{G-NAME} - nazwa gildii do ktorej nalezy gracz")
    @Comment("{G-TAG} - tag gildii gracza")
    @Comment("{G-OWNER} - wlasciciel gildii")
    @Comment("{G-DEPUTIES} - zastepcy gildii")
    @Comment("{G-DEPUTY} - losowy z zastepcow gildii")
    @Comment("{G-LIVES} - liczba zyc gildii")
    @Comment("{G-LIVES-SYMBOL} - liczba zyc gildii w postaci powtarzajacego sie symbolu (do ustawienia w lives-repeating-symbol.full w config.yml) z pustymi serduszkami jesli zyc brakuje (do ustawienia w lives-repeating-symbol.empty w config.yml)")
    @Comment("{G-LIVES-SYMBOL-ALL} - liczba wszystkich zyc gildii w postaci powtarzajacego sie symbolu (do ustawienia w lives-repeating-symbol.full w config.yml)")
    @Comment("{G-ALLIES-ALL} - liczba sojusznikow gildii")
    @Comment("{G-ENEMIES-ALL} - liczba przeciwnikow gildii")
    @Comment("{G-TOTAL-POINTS} - suma punktow czlonkow gildii")
    @Comment("{G-AVG-POINTS} (kiedys {G-POINTS}) - srednia liczba punktow czlonkow gildii")
    @Comment("{G-POINTS-FORMAT} - srednia liczba punktow czlonkow gildii z formatowaniem")
    @Comment("{G-POSITION} - pozycja gildii gracza w rankingu")
    @Comment("{G-POSITION-<typ>} - pozycja gildii gracza w rankingu dla danego typu rankingu")
    @Comment("{G-KILLS} - suma zabojstw czlonkow gildii")
    @Comment("{G-AVG-KILLS} - srednia liczba zabojstw czlonkow gildii")
    @Comment("{G-DEATHS} - suma smierci czlonkow gildii")
    @Comment("{G-AVG-DEATHS} - srednia liczba smierci czlonkow gildii")
    @Comment("{G-ASSISTS} - suma asyst czlonkow gildii")
    @Comment("{G-AVG-ASSISTS} - srednia liczba asyst czlonkow gildii")
    @Comment("{G-LOGOUTS} - suma wylogowań czlonkow gildii")
    @Comment("{G-AVG-LOGOUTS} - srednia liczba wylogowań czlonkow gildii")
    @Comment("{G-KDR} - stosunek zabojstw do smierci gildii")
    @Comment("{G-AVG-KDR} - srednia stosunku zabojstw do smierci czlonkow gildii")
    @Comment("{G-MEMBERS-ONLINE} - liczba czlonkow gildii online")
    @Comment("{G-MEMBERS-ALL} - liczba wszystkich czlonkow gildii")
    @Comment("{G-VALIDITY} - data wygasniecia gildii")
    @Comment("{G-PROTECTION} - data wygasniecia ochrony gildii")
    @Comment("{G-REGION-SIZE} - rozmiar gildii")
    @Comment("> Spis pozostalych zmiennych:")
    @Comment("{GUILDS} - liczba gildii na serwerze")
    @Comment("{USERS} - liczba uzytkownikow serwera")
    @Comment("{ONLINE} - liczba graczy online")
    @Comment("{TPS} - TPS serwera")
    @Comment("{SECOND} - Sekunda")
    @Comment("{MINUTE} - Minuta")
    @Comment("{HOUR} - Godzina")
    @Comment("{DAY_OF_WEEK} - Dzien tygodnia wyrazony w postaci nazwy dnia")
    @Comment("{DAY_OF_MONTH} - Dzien miesiaca wyrazony w postaci liczby")
    @Comment("{MONTH} - Miesiac wyrazony w postaci nazwy miesiaca")
    @Comment("{MONTH_NUMBER} - Miesiac wyrazony w postaci liczby")
    @Comment("{YEAR} - Rok")
    @Comment("{PTOP-<pozycja>} - Gracz na podanej pozycji w rankingu (np. {PTOP-1}, {PTOP-60})")
    @Comment("{PTOP-<typ>-<pozycja>} - Gracz na podanej pozycji w rankingu (np. {PTOP-KILLS-1}, {PTOP-DEATHS-60}) dla danego typu")
    @Comment("{GTOP-<pozycja>} - Gildia na podanej pozycji w rankingu (np. {GTOP-1}, {PTOP-50})")
    @Comment("{GTOP-<typ>-<pozycja>} - Gildia na podanej pozycji w rankingu (np. {GTOP-KILLS-1}, {PTOP-DEATHS-50}) dla danego typu")
    @CustomKey("player-list")
    public Map<Integer, String> playerList = ImmutableMap.<Integer, String>builder()
            .put(2, " &b&lSTATYSTYKI")
            .put(4, " &7Nick: &b{PLAYER}")
            .put(6, " &7Punkty: &b{POINTS}")
            .put(7, " &7Zabojstwa: &b{KILLS}")
            .put(8, " &7Smierci: &b{DEATHS}")
            .put(9, " &7Asysty: &b{ASSISTS}")
            .put(10, " &7KDR: &b{KDR}")
            .put(12, " &b&lINFORMACJE")
            .put(14, " &7Ping: &b{PING}")
            .put(15, " &7TPS: &b{TPS}")
            .put(17, " &7Online: &b{ONLINE}")
            .put(19, " &7Godzina: &b{HOUR}:{MINUTE}:{SECOND}")
            .put(22, " &b&lSTATYSTYKI GILDII")
            .put(24, " &7Gildia: &b{G-TAG}")
            .put(26, " &7Punkty: &b{G-AVG-POINTS}")
            .put(27, " &7Zabojstwa: &b{G-KILLS}")
            .put(28, " &7Smierci: &b{G-DEATHS}")
            .put(29, " &7Asysty: &b{G-ASSISTS}")
            .put(30, " &7KDR: &b{G-KDR}")
            .put(32, " &7Zycia: &b{G-LIVES-SYMBOL} &8(&b{G-LIVES}&8)")
            .put(42, " &b&lTOP &8- &b&lGracze")
            .put(44, " &7Gracze: &b{USERS}")
            .put(46, " &b1. &7{PTOP-1}")
            .put(47, " &b2. &7{PTOP-2}")
            .put(48, " &b3. &7{PTOP-3}")
            .put(49, " &b4. &7{PTOP-4}")
            .put(50, " &b5. &7{PTOP-5}")
            .put(51, " &b6. &7{PTOP-6}")
            .put(52, " &b7. &7{PTOP-7}")
            .put(53, " &b8. &7{PTOP-8}")
            .put(54, " &b9. &7{PTOP-9}")
            .put(55, " &b10. &7{PTOP-10}")
            .put(57, " &7Twoja pozycja: &b{POSITION}")
            .put(62, " &b&lTOP &8- &b&lGildie")
            .put(64, " &7Gildie: &b{GUILDS}")
            .put(66, " &b1. &7{GTOP-1}")
            .put(67, " &b2. &7{GTOP-2}")
            .put(68, " &b3. &7{GTOP-3}")
            .put(69, " &b4. &7{GTOP-4}")
            .put(70, " &b5. &7{GTOP-5}")
            .put(71, " &b6. &7{GTOP-6}")
            .put(72, " &b7. &7{GTOP-7}")
            .put(73, " &b8. &7{GTOP-8}")
            .put(74, " &b9. &7{GTOP-9}")
            .put(75, " &b10. &7{GTOP-10}")
            .put(77, " &7Pozycja gildii: &b{G-POSITION}")
            .build();

    @Comment("Wyglad naglowka w liscie graczy.")
    @CustomKey("player-list-header")
    public String playerListHeader = "&7FunnyGuilds &b4.10.0 Snowdrop &8- &bgithub.com/funnyguilds";

    @Comment("Wyglad stopki w liscie graczy.")
    @CustomKey("player-list-footer")
    public String playerListFooter = "&c&lWiadomosci braku (pokazujace sie, gdy gracz nie ma gildii) mozna zmienic w pliku messages.yml!";


    @Comment("Wyglad glowek na liscie graczy.")
    @Comment("Funkcja dziala jedynie dla zautoryzowanych graczy (tzw. premium), gracze niezautoryzowani (tzw. cracked/non-premium) nie zobacza zadnych zmian.")
    public Heads heads = new Heads();

    public static class Heads extends OkaeriConfig {

        @Comment("Czy customowe glowki na liscie graczy maja byc wlaczone.")
        public boolean enabled = true;

        @Comment("Tekstura glowy, ktora ma byc wyswietlana na liscie graczy.")
        @Comment("Teksture mozna wziac z np. https://mineskin.org/ (mozna wybrac z galerii lub utworzyc wlasna).")
        public Texture fillerTexture = new Texture(
                "ewogICJ0aW1lc3RhbXAiIDogMTY0OTkzMjg0OTYyOCwKICAicHJvZmlsZUlkIiA6ICIzMjIzNDVjNzg2ZTY0ZWU3OWNlZDA3NTAzMmI5MzQxZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJQZXJpZDB0XyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84ZWUyMTYxODk0NjA5M2QzZDYzNjk1M2M0ZTM4Mzc5NDM3MjFhOWE4N2NiZGRlZmEwYmVmMmZjZmY0NTQ1ZGU4IgogICAgfQogIH0KfQ==",
                "JK7IOCq36OLWWKmqp0NZHtZ0gKCXHgXWiHJS7d648X+5Nk7JrYhrV0GksyNOlBZzu+q0OtMGOnL/o+c9UFoedfIwEojjCv9Aty8knKU0+xfD190148nkXxEqOw6X/JJIe+7oWAQ+dBm7E/9LID2nU4X0clbWHFaFm5yFEbvuSYHdXrI6WRmAZoaacTOI5AVAudOz2nc3PCl9Zwl/Uzf1YosLOyYc3VQKGEnWSZHqiDWOGsPpHuVmQazOtgeg39LyfCDiDi7dT33nQjdiGab3DTYLIXNzKvG3pijzJGfs5seV4RJNlHWosmmVSfvmEOw6AONWMxxNnKd6aEl2OLIDc7SeZT6zgvyXtd7MZ6WtWB7d+8aBdH7hRodtDnUXIdiqFuQcT/3Z+ljU1N0rTn7e/RQ6Ci+/U05V/BTGJ1/reYldEjhT2M+3A93ydWP6qclzztWxjdOwqOS2ga4Z6KcsnnQCkbR+ZfUbCdrwSEW8bg6iwUb4ZJc5FIqe7is5hT8h4HU5U1ODTzC0qNZ5jwPNv35FVEy33zeZjrYvAYRWPkjr5ro1TIsP8pnrzK+SAsnVjq1+wNltPPF0DYRncMqVPlQ9jH4QwrHP5MHlTTZm3O5d2M8v62RhOx+lUjpIZes6Si0BChMrZOGnmr/M2h5bIkxEpBLnmvaHgVkLBdm+hx0="
        );

        public static class Texture extends OkaeriConfig {

            public final String value;
            public final String signature;

            public Texture(String value, String signature) {
                this.value = value;
                this.signature = signature;
            }

            public SkinTexture toSkinTexture() {
                return new SkinTexture(value, signature);
            }

        }

    }

    @Comment("Czy animowana tablista ma byc wlaczona?")
    public boolean playerListAnimated = true;

    @Comment("Wartosc cycles to liczba cykli przez ktore widac dana strone na tabliscie. (1 cykl = 1 wyslanie tablisty, czestotliwosc wysylania tablisty mozna zmienic ustawiajac wartosc playerListUpdateInterval)")
    @Comment("Sekcje player-list konfiguruje sie w ten sam sposob co zwykla. Ustawia sie w niej wszystkie komorki, ktore maja sie zmieniac (nadpisujac zwykla konfiguracje).")
    @Comment("Sekcje player-list-header konfiguruje sie w ten sam sposob co zwykla.")
    @Comment("Sekcje player-list-footer konfiguruje sie w ten sam sposob co zwykla.")
    public List<TablistPage> pages = new LinkedList<>(
            Arrays.asList(
                    new TablistPage(10, ImmutableMap.<Integer, String>builder()
                                    .put(42, " &b&lTOP &8- &b&lPunkty")
                                    .put(46, " &b1. &7{PTOP-POINTS-1}")
                                    .put(47, " &b2. &7{PTOP-POINTS-2}")
                                    .put(48, " &b3. &7{PTOP-POINTS-3}")
                                    .put(49, " &b4. &7{PTOP-POINTS-4}")
                                    .put(50, " &b5. &7{PTOP-POINTS-5}")
                                    .put(51, " &b6. &7{PTOP-POINTS-6}")
                                    .put(52, " &b7. &7{PTOP-POINTS-7}")
                                    .put(53, " &b8. &7{PTOP-POINTS-8}")
                                    .put(54, " &b9. &7{PTOP-POINTS-9}")
                                    .put(55, " &b10. &7{PTOP-POINTS-10}")
                                    .put(57, " &7Twoja pozycja: &b{POSITION-POINTS}")
                                    .put(62, " &b&lTOP &8- &b&lPunkty")
                                    .put(66, " &b1. &7{GTOP-AVG_POINTS-1}")
                                    .put(67, " &b2. &7{GTOP-AVG_POINTS-2}")
                                    .put(68, " &b3. &7{GTOP-AVG_POINTS-3}")
                                    .put(69, " &b4. &7{GTOP-AVG_POINTS-4}")
                                    .put(70, " &b5. &7{GTOP-AVG_POINTS-5}")
                                    .put(71, " &b6. &7{GTOP-AVG_POINTS-6}")
                                    .put(72, " &b7. &7{GTOP-AVG_POINTS-7}")
                                    .put(73, " &b8. &7{GTOP-AVG_POINTS-8}")
                                    .put(74, " &b9. &7{GTOP-AVG_POINTS-9}")
                                    .put(75, " &b10. &7{GTOP-AVG_POINTS-10}")
                                    .put(77, " &7Pozycja gildii: &b{G-POSITION-AVG_POINTS}")
                                    .build(),
                            null, null),

                    new TablistPage(10, ImmutableMap.<Integer, String>builder()
                                    .put(42, " &b&lTOP &8- &b&lZabojstwa")
                                    .put(46, " &b1. &7{PTOP-KILLS-1}")
                                    .put(47, " &b2. &7{PTOP-KILLS-2}")
                                    .put(48, " &b3. &7{PTOP-KILLS-3}")
                                    .put(49, " &b4. &7{PTOP-KILLS-4}")
                                    .put(50, " &b5. &7{PTOP-KILLS-5}")
                                    .put(51, " &b6. &7{PTOP-KILLS-6}")
                                    .put(52, " &b7. &7{PTOP-KILLS-7}")
                                    .put(53, " &b8. &7{PTOP-KILLS-8}")
                                    .put(54, " &b9. &7{PTOP-KILLS-9}")
                                    .put(55, " &b10. &7{PTOP-KILLS-10}")
                                    .put(57, " &7Twoja pozycja: &b{POSITION-KILLS}")
                                    .put(62, " &b&lTOP &8- &b&lZabojstwa")
                                    .put(66, " &b1. &7{GTOP-KILLS-1}")
                                    .put(67, " &b2. &7{GTOP-KILLS-2}")
                                    .put(68, " &b3. &7{GTOP-KILLS-3}")
                                    .put(69, " &b4. &7{GTOP-KILLS-4}")
                                    .put(70, " &b5. &7{GTOP-KILLS-5}")
                                    .put(71, " &b6. &7{GTOP-KILLS-6}")
                                    .put(72, " &b7. &7{GTOP-KILLS-7}")
                                    .put(73, " &b8. &7{GTOP-KILLS-8}")
                                    .put(74, " &b9. &7{GTOP-KILLS-9}")
                                    .put(75, " &b10. &7{GTOP-KILLS-10}")
                                    .put(77, " &7Pozycja gildii: &b{G-POSITION-KILLS}")
                                    .build(),
                            "&7GitHub: &agithub.com/funnyguilds",
                            "&a&lWiadomosci braku (pokazujace sie, gdy gracz nie ma gildii) mozna zmienic w pliku messages.yml!"),

                    new TablistPage(10, ImmutableMap.<Integer, String>builder()
                                    .put(42, " &b&lTOP &8- &b&lSmierci")
                                    .put(46, " &b1. &7{PTOP-DEATHS-1}")
                                    .put(47, " &b2. &7{PTOP-DEATHS-2}")
                                    .put(48, " &b3. &7{PTOP-DEATHS-3}")
                                    .put(49, " &b4. &7{PTOP-DEATHS-4}")
                                    .put(50, " &b5. &7{PTOP-DEATHS-5}")
                                    .put(51, " &b6. &7{PTOP-DEATHS-6}")
                                    .put(52, " &b7. &7{PTOP-DEATHS-7}")
                                    .put(53, " &b8. &7{PTOP-DEATHS-8}")
                                    .put(54, " &b9. &7{PTOP-DEATHS-9}")
                                    .put(55, " &b10. &7{PTOP-DEATHS-10}")
                                    .put(57, " &7Twoja pozycja: &b{POSITION-DEATHS}")
                                    .put(62, " &b&lTOP &8- &b&lSmierci")
                                    .put(66, " &b1. &7{GTOP-DEATHS-1}")
                                    .put(67, " &b2. &7{GTOP-DEATHS-2}")
                                    .put(68, " &b3. &7{GTOP-DEATHS-3}")
                                    .put(69, " &b4. &7{GTOP-DEATHS-4}")
                                    .put(70, " &b5. &7{GTOP-DEATHS-5}")
                                    .put(71, " &b6. &7{GTOP-DEATHS-6}")
                                    .put(72, " &b7. &7{GTOP-DEATHS-7}")
                                    .put(73, " &b8. &7{GTOP-DEATHS-8}")
                                    .put(74, " &b9. &7{GTOP-DEATHS-9}")
                                    .put(75, " &b10. &7{GTOP-DEATHS-10}")
                                    .put(77, " &7Pozycja gildii: &b{G-POSITION-DEATHS}")
                                    .build(),
                            "&7Strona: &6funnyguilds.dzikoysk.net",
                            "&6&lWiadomosci braku (pokazujace sie, gdy gracz nie ma gildii) mozna zmienic w pliku messages.yml!")
            )
    );

    @Min(0)
    @Comment("Liczba pingu pokazana przy kazdej komorce.")
    @CustomKey("player-list-ping")
    public int playerListPing = 0;

    @Comment("Czy wszystkie mozliwe komorki maja zostac zapelnione, nie zwazywszy na liczbe graczy online")
    @CustomKey("player-list-fill-cells")
    public boolean playerListFillCells = true;

    @Comment("Czy tablista ma byc wlaczona")
    @CustomKey("player-list-enable")
    public boolean playerListEnable = true;

    @Min(1)
    @Comment("Co ile tickow lista graczy powinna zostac odswiezona")
    public int playerListUpdateInterval = 20;

    @Comment("Czy zmienne typu {PTOP-%} oraz {GTOP-%} powinny byc pokolorowane w zaleznosci od relacji gildyjnych")
    @CustomKey("player-list-use-relationship-colors")
    public boolean playerListUseRelationshipColors = false;

}
