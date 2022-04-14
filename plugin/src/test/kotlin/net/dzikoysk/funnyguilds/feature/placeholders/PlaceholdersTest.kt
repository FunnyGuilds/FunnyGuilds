package net.dzikoysk.funnyguilds.feature.placeholders

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.placeholders.GuildPlaceholdersService
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils
import net.dzikoysk.funnyguilds.user.FakeUserProfile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import panda.std.Pair
import java.util.*

class PlaceholdersTest : FunnyGuildsSpec() {

    @Test
    fun `test ONLINE placeholder`() {
        var text = "§7offline - <online>online</online> - offline - <online>online</online> - offline"
        val formatter = BasicPlaceholdersService.ONLINE.toFormatter(ChatUtils.getLastColorBefore(text, "<online>"))

        text = formatter.format(text)

        assertEquals("§7offline - §aonline§7 - offline - §aonline§7 - offline", text)
    }

    @Test
    fun `test GUILD MEMBERS COLOR CONTEXT placeholder`() {
        val guild = guildManager.addGuild(Guild("guild", "TEST"))
        val user1 = userManager.createFake(UUID.randomUUID(), "user1", FakeUserProfile.offline())
        val user2 = userManager.createFake(UUID.randomUUID(), "user2", FakeUserProfile.online())
        val user3 = userManager.createFake(UUID.randomUUID(), "user3", FakeUserProfile.offline())

        guild.addMember(user1)
        guild.addMember(user2)
        guild.addMember(user3)

        var text1 = "§7{MEMBERS}"
        text1 = GuildPlaceholdersService.GUILD_MEMBERS_COLOR_CONTEXT.formatVariables(
            text1,
            Pair.of(ChatUtils.getLastColorBefore(text1, "{MEMBERS}"), guild)
        )

        assertEquals("§7user1, §auser2§7, user3", text1)

        var text2 = "§c{MEMBERS}"
        text2 = GuildPlaceholdersService.GUILD_MEMBERS_COLOR_CONTEXT.formatVariables(
            text2,
            Pair.of(ChatUtils.getLastColorBefore(text2, "{MEMBERS}"), guild)
        )

        assertEquals("§cuser1, §auser2§c, user3", text2)

        var text3 = "§a{MEMBERS}"
        text3 = GuildPlaceholdersService.GUILD_MEMBERS_COLOR_CONTEXT.formatVariables(
            text3,
            Pair.of(ChatUtils.getLastColorBefore(text3, "{MEMBERS}"), guild)
        )

        assertEquals("§auser1, §auser2§a, user3", text3)
    }

}