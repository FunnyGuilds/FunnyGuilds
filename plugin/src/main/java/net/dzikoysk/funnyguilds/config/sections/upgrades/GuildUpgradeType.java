package net.dzikoysk.funnyguilds.config.sections.upgrades;

import java.util.Locale;
import java.util.Optional;

public enum GuildUpgradeType {

    POINTS_BOOST,
    MEMBERS;

    public static Optional<GuildUpgradeType> fromKey(String key) {
        if (key == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(GuildUpgradeType.valueOf(key.toUpperCase(Locale.ROOT)));
        }
        catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

}
