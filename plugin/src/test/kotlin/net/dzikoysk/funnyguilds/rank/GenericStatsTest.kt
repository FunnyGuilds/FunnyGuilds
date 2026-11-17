package net.dzikoysk.funnyguilds.rank

import com.google.gson.JsonParser
import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.data.database.SQLDataModel
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils
import net.dzikoysk.funnyguilds.data.database.element.SQLNamedStatement
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseUserSerializer
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel
import net.dzikoysk.funnyguilds.data.flat.seralizer.FlatUserSerializer
import net.dzikoysk.funnyguilds.data.util.UserStatsSerializer
import net.dzikoysk.funnyguilds.data.util.YamlWrapper
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyServer
import net.kyori.adventure.key.Key
import org.bukkit.OfflinePlayer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.Optional
import java.nio.file.Path
import java.sql.ResultSet
import java.util.UUID
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.dzikoysk.funnyguilds.user.User

class GenericStatsTest : FunnyGuildsSpec() {

    // No registration list, comparator, formatting entry, serializer field or SQL column is needed.
    private val wins = StatKey.integerStatKey("arena-wins")
    private val balance = StatKey.doubleStatKey(Key.key("arena", "balance"))

    @Test
    fun `a typed key automatically exposes user and guild tops positions and placeholders`() {
        config.top.enabledUserTops.clear()
        config.top.enabledGuildTops.clear()
        config.top.format.ptop = ":{VALUE}"
        config.loadProcessedProperties()
        config.top.format.gtop = ":{VALUE}"
        config.loadProcessedProperties()
        config.top.useRelationshipColors = false
        val first = userManager.createFake(UUID.randomUUID(), "First")
        val second = userManager.createFake(UUID.randomUUID(), "Second")
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guild.addMember(first)
        guild.addMember(second)
        rankService.setUserStatValue(first, wins, 7)
        rankService.setUserStatValue(second, wins, 2)
        rankService.setUserStatValue(first, balance, 2.25)
        rankService.setUserStatValue(second, balance, 0.75)
        val input = "{PTOP-arena-wins-1}|{POSITION-arena-wins}|{GTOP-arena-wins-1}|{GTOP-avg_arena-wins-1}|{G-POSITION-arena-wins}"
        assertEquals("First:7|1|TAG:9|TAG:4.50|1", render(input, first))
        assertEquals("First:2.25|TAG:3.00|TAG:1.50", render("{PTOP-arena:balance-1}|{GTOP-arena:balance-1}|{GTOP-avg_arena:balance-1}", first))
    }

    @Test
    fun `generic stats survive the real YAML save and load path`(@TempDir directory: Path) {
        val uuid = UUID.randomUUID()
        val user = userManager.createFake(uuid, "Player")
        user.rank.points = 1234
        rankService.setUserStatValue(user, wins, 17)
        rankService.setUserStatValue(user, balance, 2.125)
        val file = directory.resolve("player.yml").toFile()
        val model = mock(FlatDataModel::class.java)
        `when`(model.getUserFile(user)).thenReturn(Optional.of(file))
        `when`(funnyGuilds.dataModel).thenReturn(model)
        assertTrue(FlatUserSerializer.serialize(user))
        val saved = JsonParser().parse(YamlWrapper(file).getString("stats")).asJsonObject
        assertFalse(saved.has("funnyguilds:points"))
        assertEquals("integer", saved.getAsJsonObject("funnyguilds:arena-wins").get("type").asString)
        assertEquals("double", saved.getAsJsonObject("arena:balance").get("type").asString)

        userManager.removeUser(user)
        prepareServer(uuid)
        val loaded = FlatUserSerializer.deserialize(file).get()
        assertEquals(1234, loaded.rank.points)
        assertEquals(17, rankService.getUserStatValue(loaded, wins).get())
        assertEquals(2.125, rankService.getUserStatValue(loaded, balance).get())
        assertFalse(loaded.wasChanged())
        assertEquals(1, rankService.getUserTopPosition(loaded, wins).get())
    }

    @Test
    fun `SQL storage uses one generic column and restores typed custom stats`() {
        val uuid = UUID.randomUUID()
        val user = userManager.createFake(uuid, "Player")
        rankService.setUserStatValue(user, wins, 17)
        rankService.setUserStatValue(user, balance, 2.125)
        val payload = UserStatsSerializer.serialize(user)
        val model = SQLDataModel(funnyGuilds)
        assertTrue(model.usersTable.getSQLElement("stats").isPresent)
        val statement = mock(SQLNamedStatement::class.java)
        `when`(funnyGuilds.dataModel).thenReturn(model)
        mockStatic(SQLBasicUtils::class.java).use { sql ->
            sql.`when`<SQLNamedStatement> { SQLBasicUtils.getInsert(model.usersTable) }.thenReturn(statement)
            DatabaseUserSerializer.serialize(user)
            verify(statement).set("stats", payload)
            verify(statement).executeUpdate()
        }

        userManager.removeUser(user)
        prepareServer(uuid)
        val row = mock(ResultSet::class.java)
        `when`(row.getString("uuid")).thenReturn(uuid.toString())
        `when`(row.getString("name")).thenReturn("Player")
        `when`(row.getString("stats")).thenReturn(payload)
        `when`(row.getString("reason")).thenReturn(null)
        `when`(row.getString("last_ip")).thenReturn(null)
        `when`(row.getInt("points")).thenReturn(1234)
        val loaded = DatabaseUserSerializer.deserialize(row).get()
        assertEquals(1234, loaded.rank.points)
        assertEquals(17, rankService.getUserStatValue(loaded, wins).get())
        assertEquals(2.125, rankService.getUserStatValue(loaded, balance).get())
        assertFalse(loaded.wasChanged())
    }

    @Test
    fun `loading discovers previously unknown keys and keeps legacy counters authoritative`() {
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        assertFalse(StatKey.find("restored:unknown-wins").isPresent)
        UserStatsSerializer.deserialize(user, """{"restored:unknown-wins":{"type":"integer","value":9},"funnyguilds:points":{"type":"integer","value":1}}""")
        assertEquals(config.rankStart, user.rank.points)
        val restored = rankService.getUserRankTracker("restored:unknown-wins").get()
        assertEquals(9, restored.valueOfEntity(user).get().toInt())
    }

    @Test
    fun `conflicting types and nonfinite values fail explicitly`() {
        assertThrows(IllegalArgumentException::class.java) { StatKey.doubleStatKey("arena-wins") }
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        assertThrows(IllegalArgumentException::class.java) { rankService.setUserStatValue(user, balance, Double.NaN) }
        assertThrows(IllegalArgumentException::class.java) { rankService.setUserStatValue(user, balance, Double.POSITIVE_INFINITY) }
    }

    @Test
    fun `equal stat identities share one tracker even with a different key implementation`() {
        val alias = object : StatKey<Int> {
            override fun key(): Key = wins.key()
            override fun getValueType(): Class<Int> = Int::class.javaObjectType
            override fun zero(): Int = 0
            override fun compare(first: Int, second: Int): Int = first.compareTo(second)
        }
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        rankService.setUserStatValue(user, alias, 7)
        rankService.changeUserStatValue(user, wins) { it + 1 }
        assertEquals(8, rankService.getUserStatValue(user, alias).get())
        assertEquals(8, rankService.getUserStatValue(user, wins).get())
        assertEquals(1, rankService.getUserStats(user).keys.count { it.key() == wins.key() })
    }

    @Test
    fun `a custom guild aggregation follows member stat changes automatically`() {
        val maximum = GuildStatAggregationType.create(Key.key("arena", "maximum"), Int::class.javaObjectType) { service, guild, key ->
            guild.members.maxOfOrNull { member -> service.getUserStatValue(member, key).map { it.toInt() }.orElse(0) } ?: 0
        }
        val first = userManager.createFake(UUID.randomUUID(), "First")
        val second = userManager.createFake(UUID.randomUUID(), "Second")
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guild.addMember(first)
        guild.addMember(second)
        val tracker = rankService.getGuildRankTracker(wins, maximum)
        rankService.setUserStatValue(first, wins, 5)
        rankService.setUserStatValue(second, wins, 8)
        assertEquals(8, tracker.valueOfEntity(guild).get().toInt())
        guild.removeMember(second)
        assertEquals(5, tracker.valueOfEntity(guild).get().toInt())
    }

    private fun render(text: String, user: User): String = PlainTextComponentSerializer.plainText()
        .serialize(rankPlaceholdersService.format(user, Component.text(text), user))

    private fun prepareServer(uuid: UUID) {
        val server = mock(FunnyServer::class.java)
        `when`(server.getOfflinePlayer(uuid)).thenReturn(mock(OfflinePlayer::class.java))
        `when`(server.getPlayer(uuid)).thenReturn(Optional.empty())
        `when`(funnyGuilds.funnyServer).thenReturn(server)
    }
}
