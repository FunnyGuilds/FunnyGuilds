package net.dzikoysk.funnyguilds.data.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.dzikoysk.funnyguilds.data.UUIDConflictException;

/**
 * Utility class for detecting UUID conflicts during user data loading.
 */
public final class UUIDConflictDetector {

    private final Map<String, UUID> nameToUuidMap = new HashMap<>();

    /**
     * Checks if a user with the given name already exists with a different UUID.
     * If a conflict is detected, throws UUIDConflictException.
     *
     * @param userName the username to check
     * @param userUuid the UUID of the user
     * @throws UUIDConflictException if the username already exists with a different UUID
     */
    public void checkAndRegister(String userName, UUID userUuid) {
        UUID existingUuid = this.nameToUuidMap.get(userName);
        if (existingUuid != null && !existingUuid.equals(userUuid)) {
            throw new UUIDConflictException(userName, existingUuid, userUuid);
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
