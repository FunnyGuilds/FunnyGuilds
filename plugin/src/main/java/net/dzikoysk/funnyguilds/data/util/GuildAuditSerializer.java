package net.dzikoysk.funnyguilds.data.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import net.dzikoysk.funnyguilds.config.sections.GuildAuditConfiguration;
import net.dzikoysk.funnyguilds.feature.audit.GuildAuditEntry;
import net.dzikoysk.funnyguilds.feature.audit.GuildAuditService;
import net.dzikoysk.funnyguilds.guild.Guild;

public final class GuildAuditSerializer {

    private static final Gson GSON = new Gson();
    private static final Type ENTRIES = new TypeToken<List<GuildAuditEntry>>() { }.getType();

    private GuildAuditSerializer() {
    }

    public static String serialize(Guild guild, GuildAuditConfiguration settings) {
        List<GuildAuditEntry> retained = GuildAuditService.retain(guild.getAuditEntries(), settings, Instant.now());
        return retained.isEmpty() ? null : GSON.toJson(retained, ENTRIES);
    }

    public static List<GuildAuditEntry> deserialize(String data) {
        if (data == null || data.isBlank()) {
            return List.of();
        }
        List<GuildAuditEntry> loaded = GSON.fromJson(data, ENTRIES);
        return loaded == null ? List.of() : loaded.stream().filter(Objects::nonNull).filter(entry -> entry.type() != null).toList();
    }
}
