package net.dzikoysk.funnyguilds.data;

import java.util.UUID;

/**
 * Exception thrown when multiple users with the same name but different UUIDs are detected during data loading.
 * This typically happens when a server switches between online-mode and offline-mode.
 */
public class UUIDConflictException extends RuntimeException {

    private final String userName;
    private final UUID firstUuid;
    private final UUID secondUuid;

    public UUIDConflictException(String userName, UUID firstUuid, UUID secondUuid) {
        super(buildMessage(userName, firstUuid, secondUuid));
        this.userName = userName;
        this.firstUuid = firstUuid;
        this.secondUuid = secondUuid;
    }

    private static String buildMessage(String userName, UUID firstUuid, UUID secondUuid) {
        return String.format(
            "UUID conflict detected for player '%s'! Found multiple UUIDs: %s and %s. " +
            "This typically happens when server switches between online-mode and offline-mode. " +
            "Please either: 1) Restore the original online-mode setting, or 2) Clear user data files.",
            userName, firstUuid, secondUuid
        );
    }

    public String getUserName() {
        return userName;
    }

    public UUID getFirstUuid() {
        return firstUuid;
    }

    public UUID getSecondUuid() {
        return secondUuid;
    }
}
