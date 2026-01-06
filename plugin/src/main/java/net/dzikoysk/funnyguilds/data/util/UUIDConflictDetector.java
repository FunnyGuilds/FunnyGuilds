package net.dzikoysk.funnyguilds.data.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class for detecting UUID conflicts during user data loading.
 */
public final class UUIDConflictDetector {

    private final Map<String, UUID> nameToUuidMap = new HashMap<>();

    /**
     * Checks if a user with the given name already exists with a different UUID.
     * If a conflict is detected, throws IllegalStateException.
     *
     * @param userName the username to check
     * @param userUuid the UUID of the user
     * @throws IllegalStateException if the username already exists with a different UUID
     */
    public void checkAndRegister(String userName, UUID userUuid) {
        UUID existingUuid = this.nameToUuidMap.get(userName);
        if (existingUuid != null && !existingUuid.equals(userUuid)) {
            String message = """
                UUID conflict detected for player '%s'! Found multiple UUIDs: %s and %s.
                This typically happens when server switches between online-mode and offline-mode.
                Please either: 1) Restore the original online-mode setting, or 2) Clear user data files."""
                .formatted(userName, existingUuid, userUuid);
            throw new IllegalStateException(message);
        }
        this.nameToUuidMap.put(userName, userUuid);
    }

    /**
     * Clears all tracked usernames and UUIDs.
     */
    public void clear() {
        this.nameToUuidMap.clear();
    }
}
