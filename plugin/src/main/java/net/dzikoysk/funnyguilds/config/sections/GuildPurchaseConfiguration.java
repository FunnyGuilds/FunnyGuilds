package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.time.Duration;
import java.util.List;
import java.util.Locale;

public final class GuildPurchaseConfiguration {

    public static final List<String> DEFAULT_RENEWAL_PERIODS = List.of("12h", "1d", "3d", "5d", "7d", "14d", "31d");

    private GuildPurchaseConfiguration() {
    }

    public enum Mode {
        ITEMS, MONEY, BOTH;

        public boolean includesItems() {
            return this != MONEY;
        }

        public boolean includesMoney() {
            return this != ITEMS;
        }
    }

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class Renewal extends OkaeriConfig {

        @Comment("Tryb płatności: ITEMS, MONEY lub BOTH.")
        public Mode mode = Mode.ITEMS;
        @Comment("Okres odpowiadający stawce validity-items i money. Pusty oznacza validity-time.")
        public String rateUnit = "";
        public double money = 0;

        public Duration rateUnit(Duration defaultPeriod) {
            return this.rateUnit.isBlank() ? defaultPeriod : parsePeriod(this.rateUnit);
        }
    }

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class Enlargement extends OkaeriConfig {

        @Comment("Tryb płatności: ITEMS, MONEY lub BOTH.")
        public Mode mode = Mode.ITEMS;
        @Comment("Ceny w Vault, po jednej na poziom. W trybie BOTH lista musi odpowiadać enlarge-items.")
        public List<Double> money = List.of();
    }

    public static Duration parsePeriod(String text) {
        String value = text.strip().toLowerCase(Locale.ROOT);
        if (value.length() < 2) {
            throw new IllegalArgumentException("Expected a period such as 12h or 3d: " + text);
        }
        long amount = Long.parseLong(value.substring(0, value.length() - 1));
        if (amount <= 0) {
            throw new IllegalArgumentException("Period must be positive: " + text);
        }
        return switch (value.charAt(value.length() - 1)) {
            case 'd' -> Duration.ofDays(amount);
            case 'h' -> Duration.ofHours(amount);
            case 'm' -> Duration.ofMinutes(amount);
            case 's' -> Duration.ofSeconds(amount);
            default -> throw new IllegalArgumentException("Unknown period unit: " + text);
        };
    }
}
