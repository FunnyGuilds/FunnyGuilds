package net.dzikoysk.funnyguilds.rank

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.config.RawString
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.placeholders.GuildPlaceholdersService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class RankPlaceholdersServiceTest : FunnyGuildsSpec() {

    @Test
    fun `all occurrences and positions are replaced and immediately reflect stat changes`() {
        config.ptopOffline = RawString("")
        config.top.format.ptop = RawString(":{VALUE}")
        val first = userManager.createFake(UUID.randomUUID(), "First")
        val second = userManager.createFake(UUID.randomUUID(), "Second")
        first.rank.points = 1500
        second.rank.points = 1000
        val text = "{PTOP-points-1}|{PTOP-points-2}|{PTOP-points-1}|{POSITION-points}"
        assertEquals("First:1500|Second:1000|First:1500|1", rankPlaceholdersService.format(first, text, first))
        second.rank.points = 2000
        assertEquals("Second:2000|First:1500|Second:2000|2", rankPlaceholdersService.format(first, text, first))
    }

    @Test
    fun `invalid and missing top entries use the configured fallback`() {
        val viewer = userManager.createFake(UUID.randomUUID(), "Viewer")
        listOf("{PTOP-points-0}", "{PTOP-points-99999999999999999999999}", "{PTOP-unknown-1}", "{PTOP-points-2}").forEach {
            assertEquals("Brak (PTOP-x)", rankPlaceholdersService.formatTop(it, viewer), it)
        }
        assertEquals("0", rankPlaceholdersService.formatTopPosition("{POSITION-points}", null))
        assertEquals("Brak (guild-min-members w config.yml)", rankPlaceholdersService.formatTopPosition("{G-POSITION-avg_points}", null))
    }

    @Test
    fun `legacy guild tops keep using average points and accept underscore and hyphen names`() {
        funnyGuilds.tablistConfiguration.useRelationshipColors = false
        config.gtopPoints = RawString(":{POINTS}")
        config.top.format.gtop = RawString(":{VALUE}")
        val first = userManager.createFake(UUID.randomUUID(), "First")
        val second = userManager.createFake(UUID.randomUUID(), "Second")
        first.rank.points = 1400
        second.rank.points = 600
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guild.addMember(first)
        guild.addMember(second)
        val input = "{GTOP-1}|{GTOP-avg_points-1}|{GTOP-avg-points-1}|{G-POSITION-avg_points}"
        assertEquals("TAG:1000|TAG:1000|TAG:1000|1", rankPlaceholdersService.format(first, input, first))
        config.top.enableLegacyPlaceholders = false
        assertEquals("{GTOP-1}", rankPlaceholdersService.format(first, "{GTOP-1}", first))
    }

    @Test
    fun `guild points placeholders preserve the average versus total distinction`() {
        val first = userManager.createFake(UUID.randomUUID(), "First")
        val second = userManager.createFake(UUID.randomUUID(), "Second")
        first.rank.points = 1500
        second.rank.points = 500
        val guild = Guild("Guild", "TAG")
        guild.setOwner(first)
        guild.validity = Instant.now().plusSeconds(86400)
        guild.protection = Instant.now().plusSeconds(3600)
        guildManager.addGuild(guild)
        guild.addMember(second)
        val placeholders = GuildPlaceholdersService.createGuildPlaceholders(funnyGuilds)
        assertEquals("1000/2000/1000", placeholders.formatVariables("{POINTS}/{TOTAL-POINTS}/{AVG-POINTS}", guild))
        first.rank.points = 2500
        assertEquals("1500/3000/1500", placeholders.formatVariables("{POINTS}/{TOTAL-POINTS}/{AVG-POINTS}", guild))
    }
}
