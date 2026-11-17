package net.dzikoysk.funnyguilds.guild.placeholders

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.permission.GuildPermissionChecker
import net.dzikoysk.funnyguilds.user.FakeUserProfile
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.lenient
import java.util.Locale
import java.util.UUID

class MemberPlaceholdersTest : FunnyGuildsSpec() {

    private lateinit var service: GuildPlaceholdersService

    @BeforeEach
    fun setupMemberPlaceholders() {
        val checker = GuildPermissionChecker.create(funnyGuilds)
        lenient().`when`(funnyGuilds.guildPermissionChecker).thenReturn(checker)

        service = GuildPlaceholdersService(messageService)
        service.register(funnyGuilds, "members", GuildPlaceholdersService.createMemberPlaceholders(funnyGuilds))
    }

    @Test
    fun `should sort members by online status, then priority, then name`() {
        val guild = guildManager.addGuild(Guild("TestGuild", "TEST"))

        val owner = userManager.createFake(UUID.randomUUID(), "Owner", FakeUserProfile.online())
        val deputy = userManager.createFake(UUID.randomUUID(), "Deputy", FakeUserProfile.online())
        val onlineMember = userManager.createFake(UUID.randomUUID(), "Beta", FakeUserProfile.online())
        val onlineMemberAlpha = userManager.createFake(UUID.randomUUID(), "Alpha", FakeUserProfile.online())
        val offlineMember = userManager.createFake(UUID.randomUUID(), "Zeta", FakeUserProfile.offline())

        guild.setOwner(owner)
        guild.addMember(deputy)
        guild.addDeputy(deputy)
        guild.addMember(onlineMember)
        guild.addMember(onlineMemberAlpha)
        guild.addMember(offlineMember)

        assertEquals("Owner", plainName(guild, 1))
        assertEquals("Deputy", plainName(guild, 2))
        assertEquals("Alpha", plainName(guild, 3))
        assertEquals("Beta", plainName(guild, 4))
        assertEquals("Zeta", plainName(guild, 5))
    }

    @Test
    fun `should fall back to no-value message when index exceeds member count`() {
        val guild = guildManager.addGuild(Guild("Tiny", "T"))
        guild.setOwner(userManager.createFake(UUID.randomUUID(), "Solo", FakeUserProfile.online()))

        val rendered = render(guild, 5)
        assertEquals(messageService.get(null) { it.gMemberNoValue }, rendered)
    }

    @Test
    fun `should color online members with online color and offline with offline color`() {
        val guild = guildManager.addGuild(Guild("Colors", "C"))
        guild.setOwner(userManager.createFake(UUID.randomUUID(), "OnlineOwner", FakeUserProfile.online()))
        guild.addMember(userManager.createFake(UUID.randomUUID(), "OfflineMember", FakeUserProfile.offline()))

        assertEquals(config.onlineColor, render(guild, 1).color())
        assertEquals(config.offlineColor, render(guild, 2).color())
    }

    @Test
    fun `should treat vanished players as offline when respect-vanish is enabled`() {
        config.gMemberRespectVanish = true
        val guild = guildManager.addGuild(Guild("Vanish", "V"))

        val vanishedOwner = userManager.createFake(UUID.randomUUID(), "Sneaky", FakeUserProfile(true, true, 0))
        guild.setOwner(vanishedOwner)

        val rendered = render(guild, 1)
        assertEquals(config.offlineColor, rendered.color())
    }

    @Test
    fun `should treat vanished players as online when respect-vanish is disabled`() {
        config.gMemberRespectVanish = false
        val guild = guildManager.addGuild(Guild("Vanish2", "V2"))

        val vanishedOwner = userManager.createFake(UUID.randomUUID(), "Sneaky", FakeUserProfile(true, true, 0))
        guild.setOwner(vanishedOwner)

        val rendered = render(guild, 1)
        assertEquals(config.onlineColor, rendered.color())
    }

    private fun render(guild: Guild, index: Int): Component =
        service.format(null, Component.text("{G-MEMBER-$index}"), guild, "{G-", "}") { it.uppercase(Locale.ROOT) }

    private fun plainName(guild: Guild, index: Int): String =
        (render(guild, index) as TextComponent).content()
}
