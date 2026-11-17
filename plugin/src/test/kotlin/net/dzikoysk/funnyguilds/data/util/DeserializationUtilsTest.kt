package net.dzikoysk.funnyguilds.data.util

import java.time.Duration
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.GuildStatKeys
import net.dzikoysk.funnyguilds.rank.GuildStatAggregationType
import net.dzikoysk.funnyguilds.user.User
import org.bukkit.Location
import org.junit.jupiter.api.Test

class DeserializationUtilsTest : FunnyGuildsSpec() {

    /**
     * Builds a raw deserialization payload: [uuid, name, tag, owner, home, region, members,
     * allies, enemies, born, validity, protection, lives, ban, deputies, tntProtectionBypass,
     * pvp, heartLives, warsWon, warsLost, lastWarGuildTag, warStreak].
     */
    private fun deserializeGuild(
        born: Instant = Instant.now().minusSeconds(3600),
        validity: Instant?,
        warsWon: Int = 5,
        warsLost: Int = 2,
        warStreak: Int = 3,
    ): Guild {
        val owner = userManager.createFake(UUID.randomUUID(), "owner")
        val member = userManager.createFake(UUID.randomUUID(), "member")
        val values = arrayOf<Any?>(
            UUID.randomUUID(),
            "guild",
            "TAG",
            owner,
            Location(null, 0.0, 0.0, 0.0),
            null,
            setOf(owner, member),
            emptySet<Guild>(),
            emptySet<Guild>(),
            born,
            validity,
            Instant.now().plusSeconds(3600),
            3,
            null,
            emptySet<User>(),
            null,
            true,
            2,
            warsWon,
            warsLost,
            null,
            warStreak,
        )

        return DeserializationUtils.deserializeGuild(config, guildManager, values).orElseThrow()
    }

    @Test
    fun `deserializeGuild should keep a stored validity instant`() {
        val stored = Instant.now().plusSeconds(3600)

        val guild = deserializeGuild(validity = stored)

        assertEquals(stored, guild.getValidity())
    }

    @Test
    fun `deserializeGuild should resolve a null validity from the configuration`() {
        val guild = deserializeGuild(validity = null)

        assertTrue(guild.isValid())
        val delta = Duration.between(Instant.now(), guild.getValidity())
        assertTrue(delta <= config.validityStart.plusSeconds(5), "validity $delta is longer than validityStart")
        assertTrue(delta >= config.validityStart.minusSeconds(5), "validity $delta is shorter than validityStart")
    }

    @Test
    fun `deserializeGuild should resolve a born-encoded validity from the configuration`() {
        val born = Instant.now().minusSeconds(3600)

        val guild = deserializeGuild(born = born, validity = born)

        assertTrue(guild.isValid())
        val delta = Duration.between(Instant.now(), guild.getValidity())
        assertTrue(delta <= config.validityStart.plusSeconds(5), "validity $delta is longer than validityStart")
        assertTrue(delta >= config.validityStart.minusSeconds(5), "validity $delta is shorter than validityStart")
    }

    @Test
    fun `deserializeGuild should repair member back references and rank loaded war statistics`() {
        // Materialize the tracker before the guild exists, so its entry only reflects the
        // refresh performed after members and war statistics have been loaded.
        val warsWonTracker = rankService.getGuildRankTracker(GuildStatKeys.WARS_WON, GuildStatAggregationType.TOTAL_LONG)

        val guild = deserializeGuild(validity = Instant.now().plusSeconds(3600), warsWon = 7)

        assertEquals(7, warsWonTracker.valueOfEntity(guild).orElseThrow())
        assertTrue(guild.getMembers().isNotEmpty())
        assertTrue(guild.getMembers().all { member -> member.getGuild().orElse(null) == guild })
    }

}
