package net.dzikoysk.funnyguilds.listener

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.guild.Guild
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class PlayerDeathIPProtectionTest : FunnyGuildsSpec() {

    @Test
    fun `should match when victim IP equals a guildmate's last IP`() {
        val guild = guildManager.addGuild(Guild("Alts", "ALT"))
        val attacker = userManager.createFake(UUID.randomUUID(), "Attacker").apply { lastIP = "1.1.1.1" }
        val alt = userManager.createFake(UUID.randomUUID(), "Alt").apply { lastIP = "9.9.9.9" }
        guild.setOwner(attacker)
        guild.addMember(alt)

        assertTrue(PlayerDeath.victimSharesIPWithGuildMember("9.9.9.9", guild, attacker))
    }

    @Test
    fun `should not match when no guildmate shares the victim IP`() {
        val guild = guildManager.addGuild(Guild("Honest", "HON"))
        val attacker = userManager.createFake(UUID.randomUUID(), "Attacker").apply { lastIP = "1.1.1.1" }
        val mate = userManager.createFake(UUID.randomUUID(), "Mate").apply { lastIP = "2.2.2.2" }
        guild.setOwner(attacker)
        guild.addMember(mate)

        assertFalse(PlayerDeath.victimSharesIPWithGuildMember("9.9.9.9", guild, attacker))
    }

    @Test
    fun `should ignore the attacker themselves when looking for shared IPs`() {
        val guild = guildManager.addGuild(Guild("Solo", "SOL"))
        val attacker = userManager.createFake(UUID.randomUUID(), "Attacker").apply { lastIP = "5.5.5.5" }
        guild.setOwner(attacker)

        assertFalse(PlayerDeath.victimSharesIPWithGuildMember("5.5.5.5", guild, attacker))
    }

    @Test
    fun `should not match when guildmate has no recorded IP`() {
        val guild = guildManager.addGuild(Guild("Empty", "EMP"))
        val attacker = userManager.createFake(UUID.randomUUID(), "Attacker").apply { lastIP = "1.1.1.1" }
        val mate = userManager.createFake(UUID.randomUUID(), "Mate")
        guild.setOwner(attacker)
        guild.addMember(mate)

        assertFalse(PlayerDeath.victimSharesIPWithGuildMember("1.1.1.1", guild, attacker))
    }

    @Test
    fun `should short-circuit on null victim IP`() {
        val guild = guildManager.addGuild(Guild("Null", "NUL"))
        val attacker = userManager.createFake(UUID.randomUUID(), "Attacker").apply { lastIP = "1.1.1.1" }
        val mate = userManager.createFake(UUID.randomUUID(), "Mate").apply { lastIP = "1.1.1.1" }
        guild.setOwner(attacker)
        guild.addMember(mate)

        assertFalse(PlayerDeath.victimSharesIPWithGuildMember(null, guild, attacker))
    }
}
