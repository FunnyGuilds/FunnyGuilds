package net.dzikoysk.funnyguilds.data.util;

import com.google.gson.Gson;
import net.dzikoysk.funnyguilds.guild.permission.GuildRolePolicy;

public final class GuildRoleSerializer {

    private static final Gson GSON = new Gson();

    private GuildRoleSerializer() {
    }

    public static String serialize(GuildRolePolicy policy) {
        return policy.equals(GuildRolePolicy.EMPTY) ? null : GSON.toJson(new StoredPolicy(1, policy));
    }

    public static GuildRolePolicy deserialize(String value) {
        if (value == null || value.isBlank()) {
            return GuildRolePolicy.EMPTY;
        }
        StoredPolicy stored = GSON.fromJson(value, StoredPolicy.class);
        if (stored == null || stored.version() != 1 || stored.policy() == null) {
            throw new IllegalArgumentException("Unsupported guild role policy");
        }
        return stored.policy();
    }

    private record StoredPolicy(int version, GuildRolePolicy policy) {
    }
}
