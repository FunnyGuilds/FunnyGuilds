package net.dzikoysk.funnyguilds.feature.audit

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.data.util.GuildAuditSerializer
import net.dzikoysk.funnyguilds.feature.audit.GuildAuditEntry.Type
import net.dzikoysk.funnyguilds.guild.Guild
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GuildAuditServiceTest : FunnyGuildsSpec() {

    @Test
    fun `retention keeps the newest entries within both configured limits`() {
        val now = Instant.parse("2026-08-01T00:00:00Z")
        val entries = listOf(0L, 2L, 3L, 40L).map { daysAgo ->
            GuildAuditEntry(now.minus(Duration.ofDays(daysAgo)).toEpochMilli(), null, "CONSOLE", Type.JOINED, emptyMap())
        }
        val settings = config.guildAudit
        settings.maxEntries = 2
        settings.retention = Duration.ofDays(30)

        assertEquals(entries.take(2), GuildAuditService.retain(entries, settings, now))
        settings.maxEntries = 8
        assertEquals(entries.take(3), GuildAuditService.retain(entries, settings, now))
        settings.maxEntries = 0
        assertTrue(GuildAuditService.retain(entries, settings, now).isEmpty())
    }

    @Test
    fun `both guilds retain independent perspectives after round trip`() {
        val source = Guild("Origin", "ORIG")
        val destination = Guild("Destination", "DEST")
        val actor = userManager.createFake(UUID.randomUUID(), "leader")

        GuildAuditService(funnyGuilds).recordRelation(source, destination, actor, Type.WAR_DECLARED)

        val original = GuildAuditSerializer.deserialize(GuildAuditSerializer.serialize(source, config.guildAudit)).single()
        val other = GuildAuditSerializer.deserialize(GuildAuditSerializer.serialize(destination, config.guildAudit)).single()
        assertEquals(actor.getUUID(), original.actorId())
        assertEquals("DEST", original.details().getValue("TARGET"))
        assertEquals("ORIG", other.details().getValue("TARGET"))
        assertEquals("DEST", other.details().getValue("DESTINATION"))
        assertEquals(Type.WAR_DECLARED, other.type())
    }
}
