package net.dzikoysk.funnyguilds.data.util

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.permission.GuildRolePolicy
import net.dzikoysk.funnyguilds.user.User
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeserializationUtilsTest : FunnyGuildsSpec() {

    @Test
    fun `invalid role policy and audit log do not prevent guild loading`() {
        config.regionsEnabled = false
        val owner = userManager.createFake(UUID.randomUUID(), "Owner")
        val guildId = UUID.randomUUID()
        val now = Instant.now()
        val values = arrayOfNulls<Any?>(26)
        values[0] = guildId
        values[1] = "Test guild"
        values[2] = "TEST"
        values[3] = owner
        values[6] = setOf(owner)
        values[7] = emptySet<Guild>()
        values[8] = emptySet<Guild>()
        values[9] = now
        values[10] = now.plus(Duration.ofDays(7))
        values[11] = now
        values[12] = 3
        values[14] = emptySet<User>()
        values[16] = false
        values[17] = 3
        values[18] = 0
        values[19] = 0
        values[21] = 0
        values[23] = emptyList<String>()
        values[24] = "{\"version\":2,\"policy\":null}"
        values[25] = "{invalid"

        val guild = DeserializationUtils.deserializeGuild(config, guildManager, values).orElseThrow()

        assertEquals(GuildRolePolicy.EMPTY, guild.rolePolicy)
        assertTrue(guild.auditEntries.isEmpty())
        assertTrue(guildManager.findByUuid(guildId).isPresent)
    }

}
