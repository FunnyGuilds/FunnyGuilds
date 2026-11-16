package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.dzikoysk.funnyguilds.config.RangeFormatting;

public class ValueFormattingConfiguration extends OkaeriConfig {

    @Comment("Formaty statystyk gracza dla placeholderów z końcówką -FORMAT (points, kills, deaths, assists, logouts, kdr, kda)")
    @Comment("Wpis: 'min-max szablon', np. '1500-* &6{VALUE} punktów'. Pierwszy pasujący wpis wygrywa.")
    @Comment("Granice całkowite: obie włącznie; granica dziesiętna: prawa wyłącznie. * oznacza granicę typu int.")
    @Comment("Brak reguł, [] lub luka w zakresach zwracają samą liczbę. Szablon obsługuje kolory i MiniMessage.")
    @Comment("Wszystkie obsługiwane statystyki są wypisane poniżej; zastąp [] własną listą progów, aby dodać formatowanie.")
    public Map<String, List<RangeFormatting>> user = userFormatting();

    @Comment("Formaty statystyk gildii; średnie mają klucze avg_points, avg_kills itd.")
    @Comment("Dostępne są także wars_won, wars_lost i war_streak. Brak gildii zwraca neutralne 0 lub 0.00.")
    @Comment("Każda statystyka ma osobną listę; [] wyłącza jej formatowanie.")
    public Map<String, List<RangeFormatting>> guild = guildFormatting();

    @Comment("Format placeholdera PING-FORMAT. Ping offline/ujemny jest przekazywany bez zmiany.")
    public List<RangeFormatting> ping = List.of(
            new RangeFormatting(0, 75, "&a{VALUE}"),
            new RangeFormatting(76, 150, "&e{VALUE}"),
            new RangeFormatting(151, Integer.MAX_VALUE, "&c{VALUE}")
    );

    private static Map<String, List<RangeFormatting>> userFormatting() {
        Map<String, List<RangeFormatting>> formats = new LinkedHashMap<>();
        formats.put("points", points());
        formats.put("kills", List.of());
        formats.put("deaths", List.of());
        formats.put("assists", List.of());
        formats.put("logouts", List.of());
        formats.put("kdr", List.of());
        formats.put("kda", List.of());
        return formats;
    }

    private static Map<String, List<RangeFormatting>> guildFormatting() {
        Map<String, List<RangeFormatting>> formats = userFormatting();
        formats.put("avg_points", points());
        formats.put("avg_kills", List.of());
        formats.put("avg_deaths", List.of());
        formats.put("avg_assists", List.of());
        formats.put("avg_logouts", List.of());
        formats.put("avg_kdr", List.of());
        formats.put("avg_kda", List.of());
        formats.put("wars_won", List.of());
        formats.put("wars_lost", List.of());
        formats.put("war_streak", List.of());
        return formats;
    }

    private static List<RangeFormatting> points() {
        return List.of(
                new RangeFormatting(Integer.MIN_VALUE, 749, "&4{VALUE}"),
                new RangeFormatting(750, 999, "&c{VALUE}"),
                new RangeFormatting(1000, 1499, "&a{VALUE}"),
                new RangeFormatting(1500, Integer.MAX_VALUE, "&6&l{VALUE}")
        );
    }
}
