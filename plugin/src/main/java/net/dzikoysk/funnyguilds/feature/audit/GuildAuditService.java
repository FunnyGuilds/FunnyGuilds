package net.dzikoysk.funnyguilds.feature.audit;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.sections.GuildAuditConfiguration;
import net.dzikoysk.funnyguilds.event.guild.GuildRoleChangeEvent;
import net.dzikoysk.funnyguilds.feature.audit.GuildAuditEntry.Type;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.GuildRolePolicy;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class GuildAuditService implements Listener {

    private final FunnyGuilds plugin;

    public GuildAuditService(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    public void record(Guild guild, User actor, Type type, Map<String, String> details) {
        this.record(guild, actor.getUUID(), actor.getName(), type, details);
    }

    public void record(Guild guild, CommandSender actor, Type type, Map<String, String> details) {
        this.record(guild, actor instanceof Player player ? player.getUniqueId() : null, actor.getName(), type, details);
    }

    private void record(Guild guild, UUID actorId, String actorName, Type type, Map<String, String> details) {
        GuildAuditConfiguration settings = this.plugin.getPluginConfiguration().guildAudit;
        if (settings.maxEntries == 0) {
            return;
        }
        Instant now = Instant.now();
        List<GuildAuditEntry> draft = new ArrayList<>(guild.getAuditEntries().size() + 1);
        draft.add(new GuildAuditEntry(now.toEpochMilli(), actorId, actorName, type, details));
        draft.addAll(guild.getAuditEntries());
        guild.setAuditEntries(retain(draft, settings, now));
    }

    public List<GuildAuditEntry> entries(Guild guild) {
        List<GuildAuditEntry> current = guild.getAuditEntries();
        List<GuildAuditEntry> retained = retain(current, this.plugin.getPluginConfiguration().guildAudit, Instant.now());
        if (retained.size() != current.size()) {
            guild.setAuditEntries(retained);
        }
        return retained;
    }

    public void pruneExpiredEntries() {
        this.plugin.getGuildManager().getGuilds().forEach(this::entries);
    }

    public static List<GuildAuditEntry> retain(List<GuildAuditEntry> entries, GuildAuditConfiguration settings, Instant now) {
        long cutoff = settings.retention.isZero() ? Long.MIN_VALUE : now.minus(settings.retention).toEpochMilli();
        return entries.stream().filter(entry -> entry.time() >= cutoff).limit(settings.maxEntries).toList();
    }

    public void recordRelation(Guild guild, Guild target, User actor, Type type) {
        this.recordRelation(guild, target, actor, type, Map.of());
    }

    public void recordRelation(Guild guild, Guild target, User actor, Type type, Map<String, String> details) {
        Map<String, String> values = new HashMap<>(details);
        values.put("ORIGIN", guild.getTag());
        values.put("DESTINATION", target.getTag());
        values.put("TARGET", target.getTag());
        this.record(guild, actor, type, values);
        values.put("TARGET", guild.getTag());
        this.record(target, actor, type, values);
    }

    @EventHandler
    public void onRoleChange(GuildRoleChangeEvent event) {
        GuildRolePolicy before = event.getPrevious();
        GuildRolePolicy after = event.getCurrent();
        List<String> changes = new ArrayList<>();
        changes.addAll(changed(before.roles(), after.roles(), id -> roleName(id, before, after)));
        changes.addAll(changed(before.assignments(), after.assignments(), this::playerName));
        changes.addAll(changed(before.overrides(), after.overrides(), this::playerName));
        if (!before.delegatedPermissions().equals(after.delegatedPermissions())) {
            Set<String> keys = new HashSet<>(before.delegatedPermissions());
            keys.addAll(after.delegatedPermissions());
            keys.removeIf(key -> before.delegatedPermissions().contains(key) == after.delegatedPermissions().contains(key));
            changes.addAll(keys.stream().sorted().toList());
        }
        this.record(event.getGuild(), event.getActor(), Type.PERMISSIONS_CHANGED,
                Map.of("TARGET", String.join(", ", changes.stream().distinct().sorted().toList())));
    }

    private String playerName(UUID id) {
        return this.plugin.getUserManager().findByUuid(id).map(User::getName).orElse(id.toString());
    }

    private static String roleName(UUID id, GuildRolePolicy before, GuildRolePolicy after) {
        if (id.equals(GuildRolePolicy.DEPUTY)) {
            return "DEPUTY";
        }
        if (id.equals(GuildRolePolicy.MEMBER)) {
            return "MEMBER";
        }
        return after.roles().getOrDefault(id, before.roles().get(id)).name();
    }

    private static <T> List<String> changed(Map<UUID, T> before, Map<UUID, T> after, Function<UUID, String> label) {
        Set<UUID> ids = new HashSet<>(before.keySet());
        ids.addAll(after.keySet());
        return ids.stream().filter(id -> !Objects.equals(before.get(id), after.get(id))).map(label).toList();
    }
}
