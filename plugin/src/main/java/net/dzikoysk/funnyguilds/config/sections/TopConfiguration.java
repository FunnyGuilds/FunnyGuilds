package net.dzikoysk.funnyguilds.config.sections;

import com.google.common.collect.ImmutableMap;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.dzikoysk.funnyguilds.config.RangeFormatting;
import net.dzikoysk.funnyguilds.config.RawString;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class TopConfiguration extends OkaeriConfig {

    @Comment("Lista topek graczy, ktore maja byc obliczane i parsowane.")
    @Comment("Najlepiej wlaczyc tylko te, ktore beda uzywane, zeby niepotrzebnie nie obciazac serwera.")
    @Comment("Topki te mozna uzywac na formie placeholderow {PTOP-typ-x} np. na tabie oraz w placeholderach.")
    @Comment("Dostepne typy topek: points, kills, deaths, assists, logouts")
    public Set<String> enabledUserTops = new TreeSet<>(Arrays.asList("points", "kills", "deaths"));

    @Comment("Lista topek gildii, ktore maja byc obliczane i parsowane.")
    @Comment("Najlepiej wlaczyc tylko te, ktore beda uzywane, zeby niepotrzebnie nie obciazac serwera.")
    @Comment("Topki te mozna uzywac na formie placeholderow {GTOP-typ-x} np. na tabie oraz w placeholderach.")
    @Comment("Dostepne typy topek: points, kills, deaths, assists, logouts, avg_points, avg_kills, avg_deaths, avg_assists, avg_logouts")
    public Set<String> enabledGuildTops = new TreeSet<>(Arrays.asList("kills", "deaths", "avg_points"));

    @Comment("Czy placeholdery {PTOP-x} oraz {GTOP-x} maja byc wlaczone na tabie.")
    @Comment("Wylaczenie tej funkcji moze minimalnie odciazyc serwer, wiec jesli to mozliwe najlepiej ja wylaczyc.")
    public boolean enableTabLegacyPlaceholders = true;

    public Format format = new Format();

    public static class Format extends OkaeriConfig {

        @Comment("Znacznik z wartoscia dodawany do zmiennej {PTOP-typ-x}")
        @Comment("Uzywaj zmiennych {VALUE} i {VALUE-FORMAT}")
        @Comment("Jesli nie chcesz wyswietlac wartosci, tylko sam nick - nie podawaj tu nic")
        public RawString ptop = new RawString(" &7[{VALUE}&7]");

        @Comment("Znacznik z wartoscia dodawany do zmiennej {GTOP-typ-x}")
        @Comment("Uzywaj zmiennych {VALUE} i {VALUE-FORMAT}")
        @Comment("Jesli nie chcesz wyswietlac wartosci, tylko sam tag - nie podawaj tu nic")
        public RawString gtop = new RawString(" &7[&b{VALUE}&7]");

        @Comment("Wyglad znacznikika {VALUE-FORMAT} w zaleznosci od wartosci topki graczy")
        @Comment("Lista powinna byc podana od najmniejszych do najwiekszych rankingow")
        @Comment("Elementy listy powinny byc postaci: \"minValue-maxValue wyglad\", np.: \"0-750 &4{VALUE}\"")
        @Comment("Pamietaj, aby kazdy mozliwy ranking mial ustalony format!")
        @Comment("* uzyta w zapisie elementu listy oznacza wszystkie wartosci od danego minRank w gore, np.: \"1500-* &6&l{POINTS}\"")
        @Comment("Nazwa sekcji oznacza dla jakiego typu topki ma byc uzywane dane formatowanie")
        public Map<String, List<RangeFormatting>> ptopValueFormatting = ImmutableMap.<String, List<RangeFormatting>>builder()
                .put("points", Arrays.asList(
                        new RangeFormatting(0, 749, "&4{VALUE}"),
                        new RangeFormatting(750, 999, "&c{VALUE}"),
                        new RangeFormatting(1000, 1499, "&a{VALUE}"),
                        new RangeFormatting(1500, Float.MAX_VALUE, "&6&l{VALUE}")
                ))
                .put("kills", Arrays.asList(
                        new RangeFormatting(0, 10, "&c{VALUE}"),
                        new RangeFormatting(11, 25, "&a{VALUE}"),
                        new RangeFormatting(26, 50, "&e{VALUE}"),
                        new RangeFormatting(51, Float.MAX_VALUE, "&6&l{VALUE}")
                ))
                .put("deaths", Arrays.asList(
                        new RangeFormatting(0, 10, "&c{VALUE}"),
                        new RangeFormatting(11, 25, "&a{VALUE}"),
                        new RangeFormatting(26, 50, "&e{VALUE}"),
                        new RangeFormatting(51, Float.MAX_VALUE, "&6&l{VALUE}")
                ))
                .build();

        @Comment("Wyglad znacznikika {VALUE-FORMAT} w zaleznosci od wartosci topki gildii")
        @Comment("Lista powinna byc podana od najmniejszych do najwiekszych rankingow")
        @Comment("Elementy listy powinny byc postaci: \"minValue-maxValue wyglad\", np.: \"0-750 &4{VALUE}\"")
        @Comment("Pamietaj, aby kazdy mozliwy ranking mial ustalony format!")
        @Comment("* uzyta w zapisie elementu listy oznacza wszystkie wartosci od danego minRank w gore, np.: \"1500-* &6&l{POINTS}\"")
        @Comment("Nazwa sekcji oznacza dla jakiego typu topki ma byc uzywane dane formatowanie")
        public Map<String, List<RangeFormatting>> gtopValueFormatting = ImmutableMap.<String, List<RangeFormatting>>builder()
                .put("kills", Arrays.asList(
                        new RangeFormatting(0, 30, "&c{VALUE}"),
                        new RangeFormatting(31, 75, "&a{VALUE}"),
                        new RangeFormatting(76, 150, "&e{VALUE}"),
                        new RangeFormatting(151, Float.MAX_VALUE, "&6&l{VALUE}")
                ))
                .put("deaths", Arrays.asList(
                        new RangeFormatting(0, 30, "&c{VALUE}"),
                        new RangeFormatting(31, 75, "&a{VALUE}"),
                        new RangeFormatting(76, 150, "&e{VALUE}"),
                        new RangeFormatting(151, Float.MAX_VALUE, "&6&l{VALUE}")
                ))
                .put("avg_points", Arrays.asList(
                        new RangeFormatting(0, 749, "&4{VALUE}"),
                        new RangeFormatting(750, 999, "&c{VALUE}"),
                        new RangeFormatting(1000, 1499, "&a{VALUE}"),
                        new RangeFormatting(1500, Float.MAX_VALUE, "&6&l{VALUE}")
                ))
                .build();

    }

}
