package net.dzikoysk.funnyguilds.rank

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.config.RangeFormatting
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.placeholders.GuildPlaceholdersService
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter
import net.dzikoysk.funnyguilds.user.User
import net.dzikoysk.funnyguilds.user.UserStatKeys
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Locale
import java.util.UUID

class RankPlaceholdersServiceTest : FunnyGuildsSpec() {

    private fun render(text: String, user: User?): String = PlainTextComponentSerializer.plainText()
        .serialize(rankPlaceholdersService.format(user, Component.text(text), user))

    @Test
    fun `all occurrences and positions immediately reflect stat changes`() {
        config.top.format.ptop = ":{VALUE}"
        config.loadProcessedProperties()
        val first = userManager.createFake(UUID.randomUUID(), "First")
        val second = userManager.createFake(UUID.randomUUID(), "Second")
        rankService.setUserStatValue(first, UserStatKeys.POINTS, 1500)
        rankService.setUserStatValue(second, UserStatKeys.POINTS, 1000)
        val text = "{PTOP-points-1}|{PTOP-points-2}|{PTOP-points-1}|{POSITION-points}"
        assertEquals("First:1500|Second:1000|First:1500|1", render(text, first))
        rankService.setUserStatValue(second, UserStatKeys.POINTS, 2000)
        assertEquals("Second:2000|First:1500|Second:2000|2", render(text, first))
        assertEquals("Second|2000|${second.uuid}", render("{PTOP-points-1-NAME}|{PTOP-points-1-VALUE}|{PTOP-points-1-UUID}", first))
    }

    @Test
    fun `invalid unknown and missing top entries use configured fallbacks`() {
        val viewer = userManager.createFake(UUID.randomUUID(), "Viewer")
        listOf("{PTOP-points-0}", "{PTOP-points-99999999999999999999999}", "{PTOP-unknown-1}", "{PTOP-points-2}").forEach {
            assertEquals("Brak (PTOP-x)", render(it, viewer), it)
        }
        assertEquals("0", render("{POSITION-points}", null))
        assertEquals("Brak (guild-min-members w config.yml)", render("{G-POSITION-avg_points}", null))
    }

    @Test
    fun `guild tops support average names and native war counters`() {
        config.top.format.gtop = ":{VALUE}"
        config.loadProcessedProperties()
        val first = userManager.createFake(UUID.randomUUID(), "First")
        val second = userManager.createFake(UUID.randomUUID(), "Second")
        rankService.setUserStatValue(first, UserStatKeys.POINTS, 1400)
        rankService.setUserStatValue(second, UserStatKeys.POINTS, 600)
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guildManager.addMember(guild, first)
        guildManager.addMember(guild, second)
        val input = "{GTOP-avg_points-1}|{GTOP-avg-points-1}|{G-POSITION-avg_points}"
        assertEquals("TAG:1000|TAG:1000|1", render(input, first))
        assertEquals("0", render("{GTOP-wars_won-1-VALUE}", first))
        guild.warsWon = 3
        guild.warStreak = 2
        guildManager.refreshGuild(guild)
        assertEquals("3|2|1", render("{GTOP-wars_won-1-VALUE}|{GTOP-war_streak-1-VALUE}|{G-POSITION-wars_won}", first))
    }

    @Test
    fun `guild points preserve current 5x total and average placeholders`() {
        val first = userManager.createFake(UUID.randomUUID(), "First")
        val second = userManager.createFake(UUID.randomUUID(), "Second")
        rankService.setUserStatValue(first, UserStatKeys.POINTS, 1500)
        rankService.setUserStatValue(second, UserStatKeys.POINTS, 500)
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guildManager.addMember(guild, first)
        guildManager.addMember(guild, second)
        val placeholders = GuildPlaceholdersService.createGuildPlaceholders(funnyGuilds)
        val formatter = FunnyFormatter().register(placeholders, guild)
        fun format() = PlainTextComponentSerializer.plainText().serialize(
            formatter.replace(Locale.ROOT, Component.text("{POINTS}/{AVG-POINTS}")))
        assertEquals("2000/1000", format())
        rankService.setUserStatValue(first, UserStatKeys.POINTS, 2500)
        assertEquals("3000/1500", format())
    }

    @Test
    fun `a guild native stat inside a user top renders the no value fallback`() {
        val viewer = userManager.createFake(UUID.randomUUID(), "Viewer")
        assertEquals("Brak (PTOP-x)", render("{PTOP-wars_won-1}", viewer))
    }

    @Test
    fun `dash and underscore average guild top variants render identically`() {
        config.top.format.gtop = ":{VALUE-FORMAT}"
        config.top.format.gtopValueFormatting = mapOf("avg_kdr" to listOf(RangeFormatting("0-* avg {VALUE}")))
        config.loadProcessedProperties()
        val first = userManager.createFake(UUID.randomUUID(), "First")
        val second = userManager.createFake(UUID.randomUUID(), "Second")
        rankService.setUserStatValue(first, UserStatKeys.KILLS, 4)
        rankService.setUserStatValue(first, UserStatKeys.DEATHS, 2)
        rankService.setUserStatValue(second, UserStatKeys.KILLS, 2)
        rankService.setUserStatValue(second, UserStatKeys.DEATHS, 1)
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guild.addMember(first)
        guild.addMember(second)
        val underscore = render("{GTOP-avg_kdr-1}", first)
        assertEquals("TAG:avg 2.00", underscore)
        assertEquals(underscore, render("{GTOP-avg-kdr-1}", first))
    }
}
