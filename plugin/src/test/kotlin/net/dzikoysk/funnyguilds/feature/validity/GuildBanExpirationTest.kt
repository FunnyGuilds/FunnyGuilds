package net.dzikoysk.funnyguilds.feature.validity

import java.nio.file.Path
import java.time.Instant
import java.util.UUID
import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel
import net.dzikoysk.funnyguilds.data.flat.seralizer.FlatGuildSerializer
import net.dzikoysk.funnyguilds.guild.Guild
import org.bukkit.configuration.file.YamlConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GuildBanExpirationTest : FunnyGuildsSpec() {

    @Test
    fun `reading ban status and scanning guilds only dirties expired bans`() {
        val unbanned = guildManager.addGuild(Guild("Unbanned", "NONE"))
        val active = guildManager.addGuild(Guild("Active", "LIVE"))
        val expired = guildManager.addGuild(Guild("Expired", "PAST"))
        val now = Instant.now()
        listOf(unbanned, active, expired).forEach { it.validity = now.plusSeconds(3600) }
        active.setBan(now.plusSeconds(3600))
        expired.setBan(now.minusSeconds(60))
        listOf(unbanned, active, expired).forEach { it.markUnchanged() }

        assertFalse(unbanned.isBanned)
        assertTrue(active.isBanned)
        assertFalse(expired.isBanned)
        assertTrue(expired.ban.isPresent)
        assertTrue(listOf(unbanned, active, expired).none { it.wasChanged() })

        val handler = GuildValidationHandler(funnyGuilds)
        repeat(7) { handler.run() }
        assertFalse(unbanned.wasChanged())
        assertFalse(active.wasChanged())
        assertTrue(expired.ban.isEmpty)
        assertTrue(expired.wasChanged())

        expired.markUnchanged()
        repeat(7) { handler.run() }
        assertFalse(expired.wasChanged())
    }

    @Test
    fun `ban expires exactly at its deadline`() {
        val guild = Guild("Guild", "TAG")
        val deadline = Instant.now().plusSeconds(60)
        guild.setBan(deadline)
        guild.markUnchanged()

        guild.expireBan(deadline.minusNanos(1))
        assertEquals(deadline, guild.ban.orElseThrow())
        assertFalse(guild.wasChanged())

        guild.expireBan(deadline)
        assertTrue(guild.ban.isEmpty)
        assertTrue(guild.wasChanged())
    }

    @Test
    fun `a ban that expired offline is cleared by the next incremental save`(@TempDir directory: Path) {
        config.regionsEnabled = false
        `when`(funnyGuilds.pluginDataFolder).thenReturn(directory.toFile())
        val model = FlatDataModel(funnyGuilds)
        `when`(funnyGuilds.dataModel).thenReturn(model)
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        val now = Instant.now()
        guild.owner = userManager.createFake(UUID.randomUUID(), "Owner")
        guild.validity = now.plusSeconds(3600)
        guild.protection = now
        guild.setBan(now.minusSeconds(60))
        model.save(true)
        val file = directory.resolve("guilds/Guild.yml").toFile()
        assertTrue(YamlConfiguration.loadConfiguration(file).getLong("ban") > 0)

        guildManager.removeGuild(guild)
        val reloaded = FlatGuildSerializer.deserialize(file).orElseThrow()
        assertFalse(reloaded.isBanned)
        assertTrue(reloaded.ban.isEmpty)
        assertTrue(reloaded.wasChanged())

        model.save(true)
        assertEquals(0L, YamlConfiguration.loadConfiguration(file).getLong("ban"))
        assertFalse(reloaded.wasChanged())

        guildManager.removeGuild(reloaded)
        val loadedAgain = FlatGuildSerializer.deserialize(file).orElseThrow()
        assertTrue(loadedAgain.ban.isEmpty)
        assertFalse(loadedAgain.wasChanged())
    }
}
