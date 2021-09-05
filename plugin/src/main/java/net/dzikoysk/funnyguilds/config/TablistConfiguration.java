package net.dzikoysk.funnyguilds.config;

import com.google.common.collect.ImmutableMap;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.validator.annotation.Min;

import java.util.Map;

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
    @Comment("{G-ALLIES} - liczba sojusznikow gildii")
    @Comment("{G-POINTS} - punkty gildii")
    @Comment("{G-POINTS-FORMAT} - punkty gildii z formatowaniem")
    @Comment("{G-POSITION} - pozycja gildii gracza w rankingu")
    @Comment("{G-KILLS} - suma zabojstw czlonkow gildii")
    @Comment("{G-DEATHS} - suma smierci czlonkow gildii")
    @Comment("{G-KDR} - stosunek zabojstw do smierci czlonkow gildii")
    @Comment("{G-MEMBERS-ONLINE} - liczba czlonkow gildii online")
    @Comment("{G-MEMBERS-ALL} - liczba wszystkich czlonkow gildii")
    @Comment("{G-VALIDITY} - data wygasniecia gildii")
    @Comment("{G-REGION-SIZE} - rozmiar gildii")
    @Comment("> Spis pozostalych zmiennych:")
    @Comment("{GUILDS} - liczba gildii na serwerze")
    @Comment("{USERS} - liczba uzytkownikow serwera")
    @Comment("{ONLINE} - liczba graczy online")
    @Comment("{TPS} - TPS serwera (wspierane tylko od wersji 1.8.8+ spigot/paperspigot)")
    @Comment("{SECOND} - Sekunda")
    @Comment("{MINUTE} - Minuta")
    @Comment("{HOUR} - Godzina")
    @Comment("{DAY_OF_WEEK} - Dzien tygodnia wyrazony w postaci nazwy dnia")
    @Comment("{DAY_OF_MONTH} - Dzien miesiaca wyrazony w postaci liczby")
    @Comment("{MONTH} - Miesiac wyrazony w postaci nazwy miesiaca")
    @Comment("{MONTH_NUMBER} - Miesiac wyrazony w postaci liczby")
    @Comment("{YEAR} - Rok")
    @Comment("{PTOP-<pozycja>} - Gracz na podanej pozycji w rankingu (np. {PTOP-1}, {PTOP-60})")
    @Comment("{GTOP-<pozycja>} - Gildia na podanej pozycji w rankingu (np. {GTOP-1}, {PTOP-50})")
    @CustomKey("player-list")
    public Map<Integer, String> playerList = ImmutableMap.<Integer, String>builder()
            .put(1, "&7Nick: &b{PLAYER}")
            .put(2, "&7Ping: &b{PING}")
            .put(3, "&7Punkty: &b{POINTS}")
            .put(4, "&7Zabojstwa: &b{KILLS}")
            .put(5, "&7Smierci: &b{DEATHS}")
            .put(6, "&7KDR: &b{KDR}")
            .put(7, "&7Gildia: &b{G-NAME}")
            .put(9, "&7TAG: &b{G-TAG}")
            .put(10, "&7Punkty gildii: &b{G-POINTS-FORMAT}")
            .put(11, "&7Pozycja gildii: &b{G-POSITION}")
            .put(12, "&7Liczba graczy online: &b{G-MEMBERS-ONLINE}")
            .put(21, "&7Online: &b{ONLINE}")
            .put(22, "&7TPS: &b{TPS}")
            .put(41, "&bTop 3 Gildii")
            .put(42, "&71. &b{GTOP-1}")
            .put(43, "&72. &b{GTOP-2}")
            .put(44, "&73. &b{GTOP-3}")
            .put(61, "&bTop 3 Graczy")
            .put(62, "&71. &b{PTOP-1}")
            .put(63, "&72. &b{PTOP-2}")
            .put(64, "&73. &b{PTOP-3}")
            .build();

    @Comment("Wyglad naglowka w liscie graczy.")
    @CustomKey("player-list-header")
    public String playerListHeader = "&7FunnyGuilds &b4.10.0 Tribute";

    @Comment("Wyglad stopki w liscie graczy.")
    @CustomKey("player-list-footer")
    public String playerListFooter = "&c&lWiadomosci braku (pokazujace sie, gdy gracz nie ma gildii) mozna zmienic w pliku messages.yml!";

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
