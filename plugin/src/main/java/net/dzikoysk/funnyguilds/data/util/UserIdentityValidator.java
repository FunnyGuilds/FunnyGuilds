package net.dzikoysk.funnyguilds.data.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/** Read-only, fail-fast validation performed before creating users or resolving guild members. */
public final class UserIdentityValidator {

    private final Map<String, Identity> identitiesByName = new HashMap<>();
    private final Map<UUID, Identity> identitiesByUuid = new HashMap<>();

    public void accept(String rawUuid, String name, String source) {
        if (name == null || name.isBlank()) {
            throw new IllegalStateException("Missing or blank username in " + source);
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
            if (!uuid.toString().equalsIgnoreCase(rawUuid)) {
                throw new IllegalArgumentException("UUID must use the full canonical format");
            }
        }
        catch (IllegalArgumentException | NullPointerException exception) {
            throw new IllegalStateException("Invalid or missing UUID for '" + name + "' in " + source, exception);
        }

        Identity incoming = new Identity(uuid, name, source);
        Identity previous = this.identitiesByName.putIfAbsent(name.toLowerCase(Locale.ROOT), incoming);
        if (previous != null && !previous.uuid().equals(uuid)) {
            throw new IllegalStateException("""
                    ================================================================================
                    CRITICAL USER IDENTITY CONFLICT - FUNNYGUILDS STARTUP ABORTED
                    ================================================================================
                    Username: %s / %s
                    UUID #1: %s
                    UUID #2: %s
                    Source #1: %s
                    Source #2: %s

                    FunnyGuilds is being disabled to protect player and guild data.
                    User records were not automatically merged or deleted.

                    Possible causes: changed online-mode/authentication, incorrect Velocity/Bungee
                    forwarding, imported or manually edited data, or reuse of a username.

                    Stop the server and back up FunnyGuilds data. Verify authentication and proxy
                    forwarding settings, identify the accounts and resolve the conflict manually.
                    Then restart the server. Do not blindly delete user data or change online-mode:
                    proxy backends may legitimately use online-mode=false with UUID forwarding.
                    ================================================================================
                    """.formatted(previous.name(), name, previous.uuid(), uuid, previous.source(), source));
        }

        Identity previousUuid = this.identitiesByUuid.putIfAbsent(uuid, incoming);
        if (previousUuid != null && !previousUuid.name().equals(name)) {
            throw new IllegalStateException("Multiple usernames for UUID " + uuid + ": '" + previousUuid.name()
                    + "' (" + previousUuid.source() + ") and '" + name + "' (" + source + ")");
        }
    }

    private record Identity(UUID uuid, String name, String source) {
    }
}
