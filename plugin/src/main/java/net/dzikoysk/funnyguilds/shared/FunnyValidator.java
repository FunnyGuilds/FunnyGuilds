package net.dzikoysk.funnyguilds.shared;

import java.util.regex.Pattern;

public final class FunnyValidator {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^\\w{3,16}$");
    private static final Pattern UUID_PATTERN = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

    private FunnyValidator() {
    }

    /**
     * Validate username.
     *
     * @param name username to validate
     * @return if username is valid
     */
    public static boolean validateUsername(String name) {
        return USERNAME_PATTERN.matcher(name).matches();
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


}
