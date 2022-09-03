package net.dzikoysk.funnyguilds.shared;

import java.util.regex.Pattern;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;

public final class FunnyValidator {

    private static final Pattern UUID_PATTERN = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

    private FunnyValidator() {
    }

    /**
     * Validate username.
     *
     * @param config plugin configuration
     * @param name username to validate
     * @return if username is valid
     */
    public static NameResult validateUsername(PluginConfiguration config, String name) {
        if (name.length() < config.playerNameMinLength) {
            return NameResult.TOO_SHORT;
        }

        if (name.length() > config.playerNameMaxLength) {
            return NameResult.TOO_LONG;
        }

        if (!config.playerNameRegex.matches(name)) {
            return NameResult.INVALID;
        }

        return NameResult.VALID;
    }

    /**
     * Validate universally unique identifier.
     *
     * @param uuid universally unique identifier to validate
     * @return if universally unique identifier is valid
     */
    public static boolean validateUUID(String uuid) {
        return UUID_PATTERN.matcher(uuid).matches();
    }

    public enum NameResult {
        TOO_SHORT,
        TOO_LONG,
        INVALID,
        VALID
    }


}
