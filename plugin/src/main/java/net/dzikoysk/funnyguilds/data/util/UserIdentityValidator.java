package net.dzikoysk.funnyguilds.data.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import panda.std.Result;

/** Read-only, fail-fast validation performed before creating users or resolving guild members. */
public final class UserIdentityValidator {

    private final Map<String, Identity> identitiesByName = new HashMap<>();
    private final Map<UUID, Identity> identitiesByUuid = new HashMap<>();

    public Result<UUID, String> accept(String rawUuid, String name, String source) {
        if (name == null || name.isBlank()) {
            return Result.error("Missing or blank username in " + source);
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
            if (!uuid.toString().equalsIgnoreCase(rawUuid)) {
                return Result.error("Invalid or missing UUID for '" + name + "' in " + source);
            }
        } catch (IllegalArgumentException | NullPointerException exception) {
            return Result.error("Invalid or missing UUID for '" + name + "' in " + source);
        }

        Identity incoming = new Identity(uuid, name, source);
        String normalizedName = name.toLowerCase(Locale.ROOT);
        Identity previous = this.identitiesByName.get(normalizedName);
        if (previous != null && !previous.uuid().equals(uuid)) {
            return Result.error("UUID conflict for username '" + previous.name() + "' / '" + name + "': "
                    + previous.uuid() + " (" + previous.source() + ") and " + uuid + " (" + source + ")");
        }

        Identity previousUuid = this.identitiesByUuid.get(uuid);
        if (previousUuid != null && !previousUuid.name().equals(name)) {
            return Result.error("Multiple usernames for UUID " + uuid + ": '" + previousUuid.name()
                    + "' (" + previousUuid.source() + ") and '" + name + "' (" + source + ")");
        }

        this.identitiesByName.putIfAbsent(normalizedName, incoming);
        this.identitiesByUuid.putIfAbsent(uuid, incoming);
        return Result.ok(uuid);
    }

    private record Identity(UUID uuid, String name, String source) {
    }
}
