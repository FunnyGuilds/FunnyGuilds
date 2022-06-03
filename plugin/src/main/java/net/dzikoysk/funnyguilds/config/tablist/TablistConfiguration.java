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
import net.dzikoysk.funnyguilds.config.NumberRange;
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

    @Comment("Wyglad naglowka w liscie graczy")
    @CustomKey("player-list-header")
    public String playerListHeader = "&7FunnyGuilds &b4.10.1 Snowdrop &8- &bgithub.com/funnyguilds";

    @Comment("Wyglad stopki w liscie graczy.")
    @CustomKey("player-list-footer")
    public String playerListFooter = "&c&lWiadomosci braku (pokazujace sie, gdy gracz nie ma gildii) mozna zmienic w pliku messages.yml!";


    @Comment("Wyglad glowek na liscie graczy.")
    @Comment("Funkcja dziala jedynie dla zautoryzowanych graczy (tzw. premium), gracze niezautoryzowani (tzw. cracked/non-premium) nie zobacza zadnych zmian")
    public Heads heads = new Heads();

    public static class Heads extends OkaeriConfig {

        @Comment("Czy customowe glowki na liscie graczy maja byc wlaczone")
        public boolean enabled = true;

        @Comment("Tekstura glowy, ktora ma byc wyswietlana na liscie graczy")
        @Comment("Teksture mozna wziac z np. https://mineskin.org/ (mozna wybrac z galerii lub utworzyc wlasna)")
        public Map<NumberRange, SkinTexture> textures = ImmutableMap.<NumberRange, SkinTexture>builder()
                .put(new NumberRange(1, 80), new SkinTexture(
                        "ewogICJ0aW1lc3RhbXAiIDogMTY0OTkzMjg0OTYyOCwKICAicHJvZmlsZUlkIiA6ICIzMjIzNDVjNzg2ZTY0ZWU3OWNlZDA3NTAzMmI5MzQxZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJQZXJpZDB0XyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84ZWUyMTYxODk0NjA5M2QzZDYzNjk1M2M0ZTM4Mzc5NDM3MjFhOWE4N2NiZGRlZmEwYmVmMmZjZmY0NTQ1ZGU4IgogICAgfQogIH0KfQ==",
                        "JK7IOCq36OLWWKmqp0NZHtZ0gKCXHgXWiHJS7d648X+5Nk7JrYhrV0GksyNOlBZzu+q0OtMGOnL/o+c9UFoedfIwEojjCv9Aty8knKU0+xfD190148nkXxEqOw6X/JJIe+7oWAQ+dBm7E/9LID2nU4X0clbWHFaFm5yFEbvuSYHdXrI6WRmAZoaacTOI5AVAudOz2nc3PCl9Zwl/Uzf1YosLOyYc3VQKGEnWSZHqiDWOGsPpHuVmQazOtgeg39LyfCDiDi7dT33nQjdiGab3DTYLIXNzKvG3pijzJGfs5seV4RJNlHWosmmVSfvmEOw6AONWMxxNnKd6aEl2OLIDc7SeZT6zgvyXtd7MZ6WtWB7d+8aBdH7hRodtDnUXIdiqFuQcT/3Z+ljU1N0rTn7e/RQ6Ci+/U05V/BTGJ1/reYldEjhT2M+3A93ydWP6qclzztWxjdOwqOS2ga4Z6KcsnnQCkbR+ZfUbCdrwSEW8bg6iwUb4ZJc5FIqe7is5hT8h4HU5U1ODTzC0qNZ5jwPNv35FVEy33zeZjrYvAYRWPkjr5ro1TIsP8pnrzK+SAsnVjq1+wNltPPF0DYRncMqVPlQ9jH4QwrHP5MHlTTZm3O5d2M8v62RhOx+lUjpIZes6Si0BChMrZOGnmr/M2h5bIkxEpBLnmvaHgVkLBdm+hx0="
                ))
                .put(new NumberRange(46, 46), new SkinTexture(
                        "ewogICJ0aW1lc3RhbXAiIDogMTU4ODc3MTU5NzE5OSwKICAicHJvZmlsZUlkIiA6ICI3NTE0NDQ4MTkxZTY0NTQ2OGM5NzM5YTZlMzk1N2JlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFua3NNb2phbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmVjN2VlYTBlOTMzMmIzZGEyYjk0MTY5Y2ZmMjI2ZTlmOTAwMTg3MmY2ZjZhN2U5Yjc3OTI2MmRlMDAyYWQzYyIKICAgIH0KICB9Cn0=",
                        "ukTVfQx0saoV8CjINbZUO4sbfebp122ErB2ltCjqiTZUWTUQVVm2k7yBanlqKFhHQQE2fSjgdvNaJ7zRaDDXy4d8ZUKQTeIW4vPtnebyxnwscfJQzZ5qYGJn4RPnM/mEUGf14QS4pz6U7SK0Xb5YpWRNXsm6LfOirwbcwjAH1TEFBnVpkspIySo/5f/6Y1tROXXm3BGsY38MXeG3ItgMRjsJhDmXnJnf0lkIX0EYUMq1F/pf9rsHaCine3vELpL97zUTxjAdgJ/cU3kGzDi2bLfQq7SCOBiMs1Mpi9AvXyQAjG52z4IuX6id958lg3pwHYDO713p5YPaCbuxYoaY2JJJTHtwjigKcnBvFgqNbHE9wIVe+vPd3y63YfXYdSoErDlO32vBpf1VuQhoN5LPfGKUadCU/f6QcE6eXtztIbiWHFQ/ZoceCEvDHu5d76jIuAV18YlYOi7JruYorfyG3WdvNmR0nVzYHJ8wZdny081fkglF3G4dfraa6rJ3eiR62B3YpCAqv4qpRucsYyM9P5Ct8tmhxAGM8Nybu0+QtkNnBalbm8JFY6ATiZ6xHqBmQgypMFqpiarM3euLLR1U+pdoxxGkM9655RSbR/jvV2bcBRrvYj0UllqVUeqp7trI7Ch6ogNMOk6ZxrRstL6/mRA/BVyK3r5FsTzpU1IZIlE="
                ))
                .put(new NumberRange(47, 47), new SkinTexture(
                        "eyJ0aW1lc3RhbXAiOjE1MTgyNTQ4MjQ0OTQsInByb2ZpbGVJZCI6ImIwZDczMmZlMDBmNzQwN2U5ZTdmNzQ2MzAxY2Q5OGNhIiwicHJvZmlsZU5hbWUiOiJPUHBscyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDJiNzYzMTc3YTdkN2UzN2E1YmNkMTkwMzAzYzI0MjQyMWY1Njg3YmRkZGIyMTc0N2EzZjA5MTMzZDUzNjg1In19fQ==",
                        "JRLfdFqhjxEGuFnkfhgyLw7/kGqmJS9a5p7oJDMqG9zeaV4E0lfcU2UXH9j8+3gblHTYYS2lftQ3BS2AJx2vA4A+3Y2Wj0C4t6BRa87RVZIJfEObbDFnCuESwwQYpJ+vyefI224Uto7cOJfqcq9G/TeV/tkZkfuPjJs1xw+ZX4gsVYPzFqhdlejRrmAfZJ5KasRLSi2HVCk7y/Vh6a0mPZm12eRK15RaANoQKqkA8dpSvW/OF/coh9ig0ydGUmCBjGphg5LkgFSQv+iFIeDJTz6CNbZrnb1haDq1HxKWjkJWDKmuBzMug04vkue5bxYNTVKRPazEh0PSdv2RmNLsClRMhFepN1vQUarJTRwEwHLR6mdjPsbb/LYvdVzqlgyO2y/XBcdWbOUSq0486S9vzHHDMvb0hCsKHWkKEyrs5O73+QtJhSU4iKGGJHwf3I3VC9wJlBAFNkZT7IU3B+ZBNJlG3IGqfB3Rda75dZDT5a9NKrwrdgi58lsNkZziu5tUOSFLIsB7aWJnDoyAXAe+AQopOmv+oKvP1EbdKBGAGOY6s4/UgnIytTCiSQSBb63QBDLfB6AoVXCfa48mruAXvBLUGt1BYQ7tXWA8vWdi01TMPx0iCfISW8FQJ/1pb4LHxRPrVJEFGSqt6kH7X+UNj+oX16mehTgSMKi1GPOOvu0="
                ))
                .put(new NumberRange(48, 48), new SkinTexture(
                        "ewogICJ0aW1lc3RhbXAiIDogMTY0ODE3ODEwNzQzOSwKICAicHJvZmlsZUlkIiA6ICJlZDUzZGQ4MTRmOWQ0YTNjYjRlYjY1MWRjYmE3N2U2NiIsCiAgInByb2ZpbGVOYW1lIiA6ICI0MTQxNDE0MWgiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjVkN2M3MzdlYTkxZWFkNGNjNTA1NzcxNzQ5NDU3OTMzNzAwNWQ2MjRkMDZhM2QzYzdkNzkyZTZhY2IwYWM3ZiIKICAgIH0KICB9Cn0=",
                        "EdfrzjzISPGfFioxn6X1Jj1eo6jDw6t76PzevgQW7VHg/TJ8BzXL/CH8PTn9zA/MHOmNmhV6FraU9WkOsAwCEGUJC0klhaB97LE/X1qP4Btip3cVv3UaCXQyu20FcQVXoelgNa2vFWUL8aVLUSl0xl2AzTIWo57ktSf/F/oUl7/qQVOTyy3P/mOZl+PiPX3HLYVj7euas+djRC8oNqipRMuqYI+LBG2LvDvlOiupgBAfE68xmbeHQdAj21Sv/nF/CeHHWcYRO2QLKIaBuA4rHma6E9yagAKptOlL8oVGadrlWg1b4xbzjSz78LDX5v/j1Zo1d+xovdYAVjPik9uy4gs1rV7ZWLC0AeN567E4QbXJIkaPrzmAdVWbhs903CfEAQB/RNHhakL9rquGJlkXm5hJjqxLNJoOtjKgH5OHz5oMiCIz4BLFPc0eLNx2jKcNeehAijtvs2o1DRA5pwbPL/2YtHE87Keu6z0VA6RYxWNIntFHILOrD8wBcl3aMuiE3Y59+MTRjeaFgkaNfFugzAUYGl5D3i5IfPLhMpCb66aEifYVvp4MFGEustk9dGNztV4qr1mAufHKlD6iDf4pq+0808w00+GTamaHhOhm++OUT1KZsj/mrsYQwPzkBJ7g8HqyGrrTfZ8Tch6l+GSkUMaVvYngiZ7nQ8ugFzNWyag="
                ))
                .put(new NumberRange(66, 66), new SkinTexture(
                        "ewogICJ0aW1lc3RhbXAiIDogMTU4ODc3MTU5NzE5OSwKICAicHJvZmlsZUlkIiA6ICI3NTE0NDQ4MTkxZTY0NTQ2OGM5NzM5YTZlMzk1N2JlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFua3NNb2phbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmVjN2VlYTBlOTMzMmIzZGEyYjk0MTY5Y2ZmMjI2ZTlmOTAwMTg3MmY2ZjZhN2U5Yjc3OTI2MmRlMDAyYWQzYyIKICAgIH0KICB9Cn0=",
                        "ukTVfQx0saoV8CjINbZUO4sbfebp122ErB2ltCjqiTZUWTUQVVm2k7yBanlqKFhHQQE2fSjgdvNaJ7zRaDDXy4d8ZUKQTeIW4vPtnebyxnwscfJQzZ5qYGJn4RPnM/mEUGf14QS4pz6U7SK0Xb5YpWRNXsm6LfOirwbcwjAH1TEFBnVpkspIySo/5f/6Y1tROXXm3BGsY38MXeG3ItgMRjsJhDmXnJnf0lkIX0EYUMq1F/pf9rsHaCine3vELpL97zUTxjAdgJ/cU3kGzDi2bLfQq7SCOBiMs1Mpi9AvXyQAjG52z4IuX6id958lg3pwHYDO713p5YPaCbuxYoaY2JJJTHtwjigKcnBvFgqNbHE9wIVe+vPd3y63YfXYdSoErDlO32vBpf1VuQhoN5LPfGKUadCU/f6QcE6eXtztIbiWHFQ/ZoceCEvDHu5d76jIuAV18YlYOi7JruYorfyG3WdvNmR0nVzYHJ8wZdny081fkglF3G4dfraa6rJ3eiR62B3YpCAqv4qpRucsYyM9P5Ct8tmhxAGM8Nybu0+QtkNnBalbm8JFY6ATiZ6xHqBmQgypMFqpiarM3euLLR1U+pdoxxGkM9655RSbR/jvV2bcBRrvYj0UllqVUeqp7trI7Ch6ogNMOk6ZxrRstL6/mRA/BVyK3r5FsTzpU1IZIlE="
                ))
                .put(new NumberRange(67, 67), new SkinTexture(
                        "eyJ0aW1lc3RhbXAiOjE1MTgyNTQ4MjQ0OTQsInByb2ZpbGVJZCI6ImIwZDczMmZlMDBmNzQwN2U5ZTdmNzQ2MzAxY2Q5OGNhIiwicHJvZmlsZU5hbWUiOiJPUHBscyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDJiNzYzMTc3YTdkN2UzN2E1YmNkMTkwMzAzYzI0MjQyMWY1Njg3YmRkZGIyMTc0N2EzZjA5MTMzZDUzNjg1In19fQ==",
                        "JRLfdFqhjxEGuFnkfhgyLw7/kGqmJS9a5p7oJDMqG9zeaV4E0lfcU2UXH9j8+3gblHTYYS2lftQ3BS2AJx2vA4A+3Y2Wj0C4t6BRa87RVZIJfEObbDFnCuESwwQYpJ+vyefI224Uto7cOJfqcq9G/TeV/tkZkfuPjJs1xw+ZX4gsVYPzFqhdlejRrmAfZJ5KasRLSi2HVCk7y/Vh6a0mPZm12eRK15RaANoQKqkA8dpSvW/OF/coh9ig0ydGUmCBjGphg5LkgFSQv+iFIeDJTz6CNbZrnb1haDq1HxKWjkJWDKmuBzMug04vkue5bxYNTVKRPazEh0PSdv2RmNLsClRMhFepN1vQUarJTRwEwHLR6mdjPsbb/LYvdVzqlgyO2y/XBcdWbOUSq0486S9vzHHDMvb0hCsKHWkKEyrs5O73+QtJhSU4iKGGJHwf3I3VC9wJlBAFNkZT7IU3B+ZBNJlG3IGqfB3Rda75dZDT5a9NKrwrdgi58lsNkZziu5tUOSFLIsB7aWJnDoyAXAe+AQopOmv+oKvP1EbdKBGAGOY6s4/UgnIytTCiSQSBb63QBDLfB6AoVXCfa48mruAXvBLUGt1BYQ7tXWA8vWdi01TMPx0iCfISW8FQJ/1pb4LHxRPrVJEFGSqt6kH7X+UNj+oX16mehTgSMKi1GPOOvu0="
                ))
                .put(new NumberRange(68, 68), new SkinTexture(
                        "ewogICJ0aW1lc3RhbXAiIDogMTY0ODE3ODEwNzQzOSwKICAicHJvZmlsZUlkIiA6ICJlZDUzZGQ4MTRmOWQ0YTNjYjRlYjY1MWRjYmE3N2U2NiIsCiAgInByb2ZpbGVOYW1lIiA6ICI0MTQxNDE0MWgiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjVkN2M3MzdlYTkxZWFkNGNjNTA1NzcxNzQ5NDU3OTMzNzAwNWQ2MjRkMDZhM2QzYzdkNzkyZTZhY2IwYWM3ZiIKICAgIH0KICB9Cn0=",
                        "EdfrzjzISPGfFioxn6X1Jj1eo6jDw6t76PzevgQW7VHg/TJ8BzXL/CH8PTn9zA/MHOmNmhV6FraU9WkOsAwCEGUJC0klhaB97LE/X1qP4Btip3cVv3UaCXQyu20FcQVXoelgNa2vFWUL8aVLUSl0xl2AzTIWo57ktSf/F/oUl7/qQVOTyy3P/mOZl+PiPX3HLYVj7euas+djRC8oNqipRMuqYI+LBG2LvDvlOiupgBAfE68xmbeHQdAj21Sv/nF/CeHHWcYRO2QLKIaBuA4rHma6E9yagAKptOlL8oVGadrlWg1b4xbzjSz78LDX5v/j1Zo1d+xovdYAVjPik9uy4gs1rV7ZWLC0AeN567E4QbXJIkaPrzmAdVWbhs903CfEAQB/RNHhakL9rquGJlkXm5hJjqxLNJoOtjKgH5OHz5oMiCIz4BLFPc0eLNx2jKcNeehAijtvs2o1DRA5pwbPL/2YtHE87Keu6z0VA6RYxWNIntFHILOrD8wBcl3aMuiE3Y59+MTRjeaFgkaNfFugzAUYGl5D3i5IfPLhMpCb66aEifYVvp4MFGEustk9dGNztV4qr1mAufHKlD6iDf4pq+0808w00+GTamaHhOhm++OUT1KZsj/mrsYQwPzkBJ7g8HqyGrrTfZ8Tch6l+GSkUMaVvYngiZ7nQ8ugFzNWyag="
                ))
                .build();

    }

    @Comment("Czy animowana tablista ma byc wlaczona?")
    public boolean playerListAnimated = true;

    @Comment("Wartosc cycles to liczba cykli przez ktore widac dana strone na tabliscie (1 cykl = 1 wyslanie tablisty, czestotliwosc wysylania tablisty mozna zmienic ustawiajac wartosc playerListUpdateInterval)")
    @Comment("Sekcje player-list konfiguruje sie w ten sam sposob co zwykla. Ustawia sie w niej wszystkie komorki, ktore maja sie zmieniac (nadpisujac zwykla konfiguracje)")
    @Comment("Sekcje player-list-header konfiguruje sie w ten sam sposob co zwykla")
    @Comment("Sekcje player-list-footer konfiguruje sie w ten sam sposob co zwykla")
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
    @Comment("Liczba pingu pokazana przy kazdej komorce")
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

    @Comment("Czy zmienne typu {PTOP-x} oraz {GTOP-x} powinny byc pokolorowane w zaleznosci od relacji gildyjnych")
    @CustomKey("player-list-use-relationship-colors")
    public boolean playerListUseRelationshipColors = false;

}
