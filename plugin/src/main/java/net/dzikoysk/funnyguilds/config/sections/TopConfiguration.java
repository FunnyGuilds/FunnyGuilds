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

    @Deprecated
    @Comment("Opcja zachowana dla zgodnosci. Topy wszystkich statystyk sa dostepne automatycznie, bez listy wlaczonych typow.")
    public Set<String> enabledUserTops = new TreeSet<>(Arrays.asList("points", "kills", "deaths"));

    @Deprecated
    @Comment("Opcja zachowana dla zgodnosci. Topy gildii sa tworzone przy pierwszym uzyciu.")
    public Set<String> enabledGuildTops = new TreeSet<>(Arrays.asList("kills", "deaths", "avg_points"));

    @Comment("")
    @Comment("Czy placeholdery {PTOP-x} oraz {GTOP-x} mają byc właczone na tabie oraz w PlaceholderAPI")
    @Comment("Wyłączenie tej funkcji może minimalnie odciążyć serwer")
    public boolean enableLegacyPlaceholders = true;

    @Comment("")
    public Format format = new Format();

    public static class Format extends OkaeriConfig {

        @Comment("Znacznik z wartością dodawany do zmiennej {PTOP-typ-x}")
        @Comment("Używaj zmiennych {VALUE} i {VALUE-FORMAT}")
        @Comment("Jeśli nie chcesz wyświetlać wartości, tylko sam nick - nie podawaj tu nic")
        public RawString ptop = new RawString(" &7[{VALUE}&7]");

        @Comment("")
        @Comment("Znacznik z wartością dodawany do zmiennej {GTOP-typ-x}")
        @Comment("Używaj zmiennych {VALUE} i {VALUE-FORMAT}")
        @Comment("Jeśli nie chcesz wyświetlać wartości, tylko sam tag - nie podawaj tu nic")
        public RawString gtop = new RawString(" &7[&b{VALUE}&7]");

        @Comment("")
        @Comment("Wygląd znacznika {VALUE-FORMAT} w zależności od wartości topki graczy")
        @Comment("Lista powinna być podana od najmniejszych do największych rankingów")
        @Comment("Elementy listy powinny być postaci: \"minValue-maxValue wygląd\", np.: \"0-750 &4{VALUE}\"")
        @Comment("Pamiętaj, aby każdy możliwy ranking miał ustalony format!")
        @Comment("* użyta w zapisie elementu listy oznacza wszystkie wartości od danego minRank w górę, np.: \"1500-* &6&l{POINTS}\"")
        @Comment("Nazwa sekcji oznacza dla jakiego typu topki ma być używane dane formatowanie")
        public Map<String, List<RangeFormatting>> ptopValueFormatting = ImmutableMap.<String, List<RangeFormatting>>builder()
                .put("points", Arrays.asList(
                        new RangeFormatting(0, 749, "&4{VALUE}"),
                        new RangeFormatting(750, 999, "&c{VALUE}"),
                        new RangeFormatting(1000, 1499, "&a{VALUE}"),
                        new RangeFormatting(1500, Integer.MAX_VALUE, "&6&l{VALUE}")
                ))
                .put("kills", Arrays.asList(
                        new RangeFormatting(0, 10, "&c{VALUE}"),
                        new RangeFormatting(11, 25, "&a{VALUE}"),
                        new RangeFormatting(26, 50, "&e{VALUE}"),
                        new RangeFormatting(51, Integer.MAX_VALUE, "&6&l{VALUE}")
                ))
                .put("deaths", Arrays.asList(
                        new RangeFormatting(0, 10, "&c{VALUE}"),
                        new RangeFormatting(11, 25, "&a{VALUE}"),
                        new RangeFormatting(26, 50, "&e{VALUE}"),
                        new RangeFormatting(51, Integer.MAX_VALUE, "&6&l{VALUE}")
                ))
                .build();

        @Comment("")
        @Comment("Wygląd znacznika {VALUE-FORMAT} w zależności od wartości topki gildii")
        @Comment("Lista powinna być podana od najmniejszych do największych rankingów")
        @Comment("Elementy listy powinny być postaci: \"minValue-maxValue wygląd\", np.: \"0-750 &4{VALUE}\"")
        @Comment("Pamiętaj, aby każdy możliwy ranking miał ustalony format!")
        @Comment("* użyta w zapisie elementu listy oznacza wszystkie wartości od danego minRank w górę, np.: \"1500-* &6&l{POINTS}\"")
        @Comment("Nazwa sekcji oznacza dla jakiego typu topki ma być używane dane formatowanie")
        public Map<String, List<RangeFormatting>> gtopValueFormatting = ImmutableMap.<String, List<RangeFormatting>>builder()
                .put("kills", Arrays.asList(
                        new RangeFormatting(0, 30, "&c{VALUE}"),
                        new RangeFormatting(31, 75, "&a{VALUE}"),
                        new RangeFormatting(76, 150, "&e{VALUE}"),
                        new RangeFormatting(151, Integer.MAX_VALUE, "&6&l{VALUE}")
                ))
                .put("deaths", Arrays.asList(
                        new RangeFormatting(0, 30, "&c{VALUE}"),
                        new RangeFormatting(31, 75, "&a{VALUE}"),
                        new RangeFormatting(76, 150, "&e{VALUE}"),
                        new RangeFormatting(151, Integer.MAX_VALUE, "&6&l{VALUE}")
                ))
                .put("avg_points", Arrays.asList(
                        new RangeFormatting(0, 749, "&4{VALUE}"),
                        new RangeFormatting(750, 999, "&c{VALUE}"),
                        new RangeFormatting(1000, 1499, "&a{VALUE}"),
                        new RangeFormatting(1500, Integer.MAX_VALUE, "&6&l{VALUE}")
                ))
                .build();

    }

}
