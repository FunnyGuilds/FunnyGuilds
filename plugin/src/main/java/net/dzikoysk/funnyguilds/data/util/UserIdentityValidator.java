package net.dzikoysk.funnyguilds.data.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import panda.std.Result;

/** Read-only, fail-fast validation performed before creating users or resolving guild members. */
public final class UserIdentityValidator {

    private final Map<String, Identity> identitiesByName = new HashMap<>();

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
        Identity previous = this.identitiesByName.get(name);
        if (previous != null && !previous.uuid().equals(uuid)) {
            return Result.error("UUID conflict for username '" + previous.name() + "' / '" + name + "': "
                    + previous.uuid() + " (" + previous.source() + ") and " + uuid + " (" + source + ")");
        }

        this.identitiesByName.putIfAbsent(name, incoming);
        return Result.ok(uuid);
    }

    private record Identity(UUID uuid, String name, String source) {
    }
}
