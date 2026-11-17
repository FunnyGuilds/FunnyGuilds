package net.dzikoysk.funnyguilds.feature.placeholders.formatting

import dev.peri.yetanothermessageslibrary.message.holder.impl.ChatHolder
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.config.RangeFormatting
import net.dzikoysk.funnyguilds.config.sections.ValueFormattingConfiguration
import net.dzikoysk.funnyguilds.feature.command.user.InfoCommand
import net.dzikoysk.funnyguilds.feature.command.user.PlayerInfoCommand
import net.dzikoysk.funnyguilds.feature.placeholders.BasicPlaceholdersService
import net.dzikoysk.funnyguilds.feature.placeholders.TimePlaceholdersService
import net.dzikoysk.funnyguilds.feature.tablist.TablistPlaceholdersService
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.placeholders.GuildPlaceholdersService
import net.dzikoysk.funnyguilds.shared.adventure.ComponentUtil
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter
import net.dzikoysk.funnyguilds.user.FakeUserProfile
import net.dzikoysk.funnyguilds.user.User
import net.dzikoysk.funnyguilds.user.UserStatKeys
import net.dzikoysk.funnyguilds.user.placeholders.UserPlaceholdersService
import net.dzikoysk.funnyguilds.user.placeholders.UserPlaceholders
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.plugin.PluginDescriptionFile
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.lenient
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.util.Locale
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

class ValuePlaceholdersTest : FunnyGuildsSpec() {

    private lateinit var users: UserPlaceholdersService
    private lateinit var guilds: GuildPlaceholdersService
    private lateinit var tablist: TablistPlaceholdersService
    private lateinit var target: User

    @BeforeEach
    fun prepareServices() {
        config.loadProcessedProperties()
        users = UserPlaceholdersService(messageService)
        users.register(funnyGuilds, "user", UserPlaceholdersService.createUserPlaceholders(funnyGuilds))
        guilds = GuildPlaceholdersService(messageService)
        guilds.register(funnyGuilds, "guild", GuildPlaceholdersService.createGuildPlaceholders(funnyGuilds))
        tablist = TablistPlaceholdersService(messageService, BasicPlaceholdersService(messageService), TimePlaceholdersService(messageService), users, guilds)
        target = userManager.createFake(UUID.randomUUID(), "Target", FakeUserProfile.online(80))
        rankService.setUserStatValue(target, UserStatKeys.POINTS, 1500)
        rankService.setUserStatValue(target, UserStatKeys.KILLS, 9)
        rankService.setUserStatValue(target, UserStatKeys.DEATHS, 4)
        rankService.setUserStatValue(target, UserStatKeys.ASSISTS, 3)
        rankService.setUserStatValue(target, UserStatKeys.LOGOUTS, 2)
    }

    private fun text(component: Component) = PlainTextComponentSerializer.plainText().serialize(component)
    private fun render(input: String) = tablist.format(target, Component.text(input), target)

    @Test
    fun `all user metrics work independently of top configuration and preserve raw values`() {
        val values = mapOf("POINTS" to "1500", "KILLS" to "9", "DEATHS" to "4", "ASSISTS" to "3", "LOGOUTS" to "2", "KDR" to "2.25", "KDA" to "3.00")
        config.valueFormatting.user = values.keys.associate { it.lowercase() to listOf(RangeFormatting("0-* &a{VALUE} units")) }
        config.loadProcessedProperties()
        for ((name, value) in values) {
            assertEquals("$value units / $value", text(render("{$name-FORMAT} / {$name}")))
        }
        assertEquals("§e80", LegacyComponentSerializer.legacySection().serialize(render("{PING-FORMAT}")))
        rankService.setUserStatValue(target, UserStatKeys.KILLS, 12)
        assertEquals("12 units", text(render("{KILLS-FORMAT}")))
    }

    @Test
    fun `all guild metrics work with and without prefix and distinguish totals from averages`() {
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guild.addMember(target)
        target.setGuild(guild)
        val other = userManager.createFake(UUID.randomUUID(), "Other", FakeUserProfile.online())
        rankService.setUserStatValue(other, UserStatKeys.POINTS, 500)
        rankService.setUserStatValue(other, UserStatKeys.KILLS, 3)
        rankService.setUserStatValue(other, UserStatKeys.DEATHS, 2)
        rankService.setUserStatValue(other, UserStatKeys.ASSISTS, 1)
        rankService.setUserStatValue(other, UserStatKeys.LOGOUTS, 4)
        guild.addMember(other)
        guild.warsWon = 3
        guild.warsLost = 2
        guild.warStreak = 1
        val values = mapOf(
            "POINTS" to "2000", "AVG-POINTS" to "1000", "KILLS" to "12", "AVG-KILLS" to "6",
            "DEATHS" to "6", "AVG-DEATHS" to "3", "ASSISTS" to "4", "AVG-ASSISTS" to "2",
            "LOGOUTS" to "6", "AVG-LOGOUTS" to "3", "KDR" to "2.00", "AVG-KDR" to "1.88",
            "KDA" to "2.67", "AVG-KDA" to "2.50", "WARS-WON" to "3", "WARS-LOST" to "2", "WAR-STREAK" to "1"
        )
        config.valueFormatting.guild = values.keys.associate { it.lowercase().replace('-', '_') to listOf(RangeFormatting("0-* &b{VALUE} units")) }
        config.loadProcessedProperties()
        for ((name, value) in values) {
            assertEquals("$value units", text(render("{G-$name-FORMAT}")), name)
            assertEquals("$value units", text(guilds.format(target, Component.text("{$name-FORMAT}"), guild)), name)
        }
    }

    @Test
    fun `missing guild returns neutral numbers instead of applying zero range`() {
        assertEquals(Component.text("0"), render("{G-POINTS-FORMAT}"))
        assertEquals("0.00 / 0.00 / 0", text(render("{G-KDR-FORMAT} / {G-AVG-KDA-FORMAT} / {G-WARS-WON-FORMAT}")))
    }

    @Test
    fun `registered placeholders see new removed and replaced rules after reload`() {
        assertEquals("9", text(render("{KILLS-FORMAT}")))
        config.valueFormatting.user = mapOf("kills" to listOf(RangeFormatting("0-* &a{VALUE} kills")))
        config.loadProcessedProperties()
        assertEquals("9 kills", text(render("{KILLS-FORMAT}")))
        config.valueFormatting = ValueFormattingConfiguration()
        config.loadProcessedProperties()
        assertEquals("9", text(render("{KILLS-FORMAT}")))
    }

    @Test
    fun `failed reload keeps the complete previous formatting snapshot`() {
        val previous = config.valueFormatting
        val snapshot = config.getValueFormatting()
        previous.user = mapOf("kills" to listOf(RangeFormatting("0-* new")))
        previous.ping = listOf(RangeFormatting("0-* valid").apply { value = "" })
        val error = assertThrows(IllegalArgumentException::class.java) { config.loadProcessedProperties() }
        assertTrue(error.message!!.contains("value-formatting.ping[0]"))
        assertSame(snapshot, config.getValueFormatting())
        assertEquals("9", text(render("{KILLS-FORMAT}")))
        assertEquals("80", text(render("{PING-FORMAT}")))
    }

    @Test
    fun `rank service formats top values while preserving raw suffix and name styles`() {
        config.top.format.ptop = " &7[{VALUE-FORMAT}&7]"
        config.top.format.ptopValueFormatting = emptyMap()
        config.valueFormatting.user = mapOf("points" to listOf(RangeFormatting("0-* &a{VALUE} pts")))
        config.loadProcessedProperties()
        val formatted = rankPlaceholdersService.format(target, Component.text("{PTOP-POINTS-1}"), target)
        assertEquals("Target [1500 pts]", text(formatted))
        assertEquals(ComponentUtil.toLegacy(Component.text("Target", config.onlineColor).append(ComponentUtil.colored(" &7[&a1500 pts&7]"))), ComponentUtil.toLegacy(formatted))
        assertEquals("1500", text(rankPlaceholdersService.format(target, Component.text("{PTOP-POINTS-1-VALUE}"), target)))
    }

    @Test
    fun `ping uses original zero and negative values`() {
        for (ping in listOf(0, -1)) {
            val user = userManager.createFake(UUID.randomUUID(), "Ping${ping + 1}", FakeUserProfile(false, false, ping))
            assertEquals(ping.toString(), text(tablist.format(user, Component.text("{PING-FORMAT}"), user)))
        }
    }

    @Test
    fun `player info command and right click preview format the target not the recipient`() {
        val receiver = mock(CommandSender::class.java)
        val messages = messageService.messageRepositories[Locale.forLanguageTag("pl")]!!
        messages.playerInfoList = ChatHolder.message("{PLAYER}: {POINTS-FORMAT}, {KDR-FORMAT}, {PING-FORMAT}, {POINTS}")
        messages.playerRightClickInfo = messages.playerInfoList
        val command = PlayerInfoCommand()
        command.config = config
        command.messageService = messageService
        command.userPlaceholdersService = users
        command.rankService = rankService
        command.sendInfoMessage({ it.playerInfoList }, target, receiver)
        command.sendInfoMessage({ it.playerRightClickInfo }, target, receiver)
        val captured = ArgumentCaptor.forClass(Component::class.java)
        verify(receiver, times(2)).sendMessage(captured.capture())
        for (message in captured.allValues) {
            assertEquals("Target: 1500, 2.25, 80, 1500", text(message))
            assertTrue(LegacyComponentSerializer.legacySection().serialize(message).contains("§6§l1500"))
        }
    }

    @Test
    fun `guild info command accepts formatted values without guild prefix`() {
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guild.addMember(target)
        val messages = messageService.messageRepositories[Locale.forLanguageTag("pl")]!!
        messages.infoList = ChatHolder.message("{POINTS-FORMAT} / {AVG-POINTS-FORMAT} / {WARS-WON-FORMAT}")
        val receiver = mock(CommandSender::class.java)
        val command = InfoCommand()
        command.messageService = messageService
        command.guildPlaceholdersService = guilds
        command.execute(receiver, arrayOf("TAG"))
        val captured = ArgumentCaptor.forClass(Component::class.java)
        verify(receiver).sendMessage(captured.capture())
        assertEquals("1500 / 1500 / 0", text(captured.value))
    }

    @Test
    fun `PAPI delegates formatted placeholders and preserves missing player behavior`() {
        lenient().`when`(funnyGuilds.description).thenReturn(PluginDescriptionFile("FunnyGuilds", "5.0.0", "net.dzikoysk.funnyguilds.FunnyGuilds"))
        lenient().`when`(funnyGuilds.tablistPlaceholdersService).thenReturn(tablist)
        val expansionClass = Class.forName("net.dzikoysk.funnyguilds.feature.hooks.placeholderapi.PlaceholderAPIHook\$FunnyGuildsPlaceholder")
        val constructor = expansionClass.getDeclaredConstructor(FunnyGuilds::class.java).apply { isAccessible = true }
        val expansion = constructor.newInstance(funnyGuilds) as PlaceholderExpansion
        val offline = mock(OfflinePlayer::class.java)
        lenient().`when`(offline.uniqueId).thenReturn(target.uuid)
        assertEquals("§6§l1500", expansion.onRequest(offline, "points-format"))
        assertEquals("§e80", expansion.onRequest(offline, "ping-format"))
        assertEquals("0.00", expansion.onRequest(offline, "g-avg-kdr-format"))
        assertEquals("", expansion.onRequest(null, "points-format"))
        val unknown = mock(OfflinePlayer::class.java)
        lenient().`when`(unknown.uniqueId).thenReturn(UUID.randomUUID())
        assertEquals("", expansion.onRequest(unknown, "points-format"))
    }

    @Test
    fun `only requested placeholders are evaluated once per render`() {
        val calls = AtomicInteger()
        val placeholders = UserPlaceholders()
            .property("present") { _: User -> calls.incrementAndGet(); "ok" }
            .property("absent") { _: User -> error("Unused resolver was invoked") }
        val formatter = FunnyFormatter().register(placeholders, target)
        assertEquals("ok ok", text(formatter.replace(Locale.ROOT, Component.text("{PRESENT} {PRESENT}"))))
        assertEquals(1, calls.get())
        formatter.replace(Locale.ROOT, Component.text("{PRESENT}"))
        assertEquals(2, calls.get())
    }
}
