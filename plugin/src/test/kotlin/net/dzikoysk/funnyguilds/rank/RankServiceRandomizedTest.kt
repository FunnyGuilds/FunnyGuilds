package net.dzikoysk.funnyguilds.rank

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.user.FakeUserProfile
import net.dzikoysk.funnyguilds.user.User
import net.dzikoysk.funnyguilds.user.UserStatKeys
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.lenient
import java.util.ArrayDeque
import java.util.Random
import java.util.UUID
import java.util.concurrent.Executor

/**
 * The oracle owns separate values and membership sets, and sorts everything from scratch.
 * It deliberately does not use GuildRank, aggregation helpers or tracker comparators.
 */
class RankServiceRandomizedTest : FunnyGuildsSpec() {

    private data class PlayerState(
        val user: User,
        var name: String,
        val stats: IntArray,
        val profile: FakeUserProfile,
        var guild: UUID? = null,
        var exempt: Boolean = false
    )

    private data class GuildState(val guild: Guild, var name: String, val members: MutableSet<UUID> = mutableSetOf(), val warStats: IntArray = IntArray(3))

    private val players = linkedMapOf<UUID, PlayerState>()
    private val guilds = linkedMapOf<UUID, GuildState>()
    private val keys = listOf(UserStatKeys.POINTS, UserStatKeys.KILLS, UserStatKeys.DEATHS,
        UserStatKeys.ASSISTS, UserStatKeys.LOGOUTS, UserStatKeys.KDR, UserStatKeys.KDA,
        StatKey.integerStatKey("stress-wins"), StatKey.doubleStatKey("stress-balance"))

    @Test
    fun `random mutations must agree with independently rebuilt rankings for the requested duration`() {
        val seed = java.lang.Long.getLong("funnyguilds.rank.stressSeed", 2596L)
        val durationMillis = java.lang.Long.getLong("funnyguilds.rank.stressMillis", 3000L)
        val minimumOperations = Integer.getInteger("funnyguilds.rank.stressMinOperations", 2000)
        require(durationMillis in 1..600_000) { "stressMillis must be between 1 and 600000" }
        require(minimumOperations in 1..10_000_000) { "stressMinOperations must be between 1 and 10000000" }
        val random = Random(seed)

        rankService = FunnyRankService(Executor { it.run() }, config, userManager, guildManager)
        lenient().`when`(funnyGuilds.rankService).thenReturn(rankService)
        config.skipPrivilegedPlayersInRankPositions = true
        config.minMembersToInclude = 2

        repeat(160) { index -> createPlayer(UUID(0, index.toLong() + 1), "Player$index") }
        repeat(12) { index ->
            val guild = Guild(UUID(1, index.toLong() + 1), "Guild$index", "G$index")
            guildManager.addGuild(guild)
            guilds[guild.uuid] = GuildState(guild, guild.name)
        }
        players.values.forEachIndexed { index, player ->
            val guild = guilds.values.elementAt(index % guilds.size)
            guild.guild.addMember(player.user)
            player.guild = guild.guild.uuid
            guild.members.add(player.user.uuid)
        }

        // Initialize every supported top before changing anything to exercise cache invalidation.
        verifyEverything()
        val recent = ArrayDeque<String>()
        val started = System.nanoTime()
        val deadline = started + durationMillis * 1_000_000
        var operation = 0
        try {
            while (System.nanoTime() < deadline || operation < minimumOperations) {
                val player = players.values.elementAt(random.nextInt(players.size))
                val guild = guilds.values.elementAt(random.nextInt(guilds.size))
                val action = random.nextInt(20)
                if (recent.size == 24) recent.removeFirst()
                recent.addLast("$operation: action=$action user=${player.user.uuid} guild=${guild.guild.uuid}")
                when (action) {
                    in 0..4 -> {
                        val value = random.nextInt(5000)
                        player.stats[action] = value
                        setStat(player.user, action, value)
                    }
                    5 -> {
                        detach(player)
                        guild.guild.addMember(player.user)
                        player.guild = guild.guild.uuid
                        guild.members.add(player.user.uuid)
                    }
                    6 -> {
                        detach(player)
                        player.user.removeGuild()
                    }
                    7 -> {
                        player.name = "P${player.user.uuid.leastSignificantBits}_${random.nextInt(100)}"
                        userManager.updateUsername(player.user, player.name)
                    }
                    8 -> {
                        guild.name = "Guild${random.nextInt(100)}"
                        guild.guild.name = guild.name
                    }
                    9 -> {
                        player.exempt = !player.exempt
                        if (player.exempt) player.profile.addPermission("funnyguilds.ranking.exempt")
                        else player.profile.removePermission("funnyguilds.ranking.exempt")
                        rankService.refreshUser(player.user)
                    }
                    10 -> {
                        val uuid = player.user.uuid
                        detach(player)
                        userManager.removeUser(player.user)
                        players.remove(uuid)
                        createPlayer(uuid, "Player${uuid.leastSignificantBits}")
                    }
                    11 -> {
                        guild.members.forEach { players.getValue(it).guild = null }
                        guildManager.removeGuild(guild.guild)
                        val replacement = Guild(guild.guild.uuid, guild.name, guild.guild.tag)
                        guildManager.addGuild(replacement)
                        guilds[replacement.uuid] = GuildState(replacement, replacement.name)
                    }
                    12 -> {
                        val selected = players.values.filter { random.nextInt(12) == 0 }
                        guild.members.toList().forEach { players.getValue(it).guild = null }
                        guild.members.clear()
                        selected.forEach {
                            detach(it)
                            it.guild = guild.guild.uuid
                            guild.members.add(it.user.uuid)
                        }
                        guild.guild.setMembers(selected.map { it.user }.toSet())
                    }
                    13 -> {
                        config.minMembersToInclude = random.nextInt(8)
                        rankService.refreshAll()
                    }
                    14 -> {
                        val change = random.nextInt(41) - 20
                        player.stats[0] = (player.stats[0] + change).coerceAtLeast(0)
                        rankService.queueChangeUserStatValue(player.user, UserStatKeys.POINTS) {
                            (it + change).coerceAtLeast(0)
                        }.join()
                    }
                    15 -> {
                        player.stats[5] = random.nextInt(5000)
                        rankService.setUserStatValue(player.user, StatKey.integerStatKey("stress-wins"), player.stats[5])
                    }
                    16 -> {
                        player.stats[6] = random.nextInt(5000)
                        rankService.setUserStatValue(player.user, StatKey.doubleStatKey("stress-balance"), player.stats[6] / 4.0)
                    }
                    in 17..19 -> {
                        val index = action - 17
                        guild.warStats[index] = random.nextInt(100)
                        when (index) {
                            0 -> guild.guild.warsWon = guild.warStats[index]
                            1 -> guild.guild.warsLost = guild.warStats[index]
                            2 -> guild.guild.warStreak = guild.warStats[index]
                        }
                    }
                }
                operation++
                if (operation % 32 == 0) verifyEverything()
            }
            verifyEverything()
        } catch (failure: Throwable) {
            throw AssertionError("seed=$seed, operation=$operation, stressMillis=$durationMillis, recent=$recent", failure)
        }
        println("Ranking stress passed: seed=$seed, operations=$operation, elapsedMillis=${(System.nanoTime() - started) / 1_000_000}")
        assertTrue(operation >= minimumOperations)
    }

    private fun createPlayer(uuid: UUID, name: String) {
        val profile = FakeUserProfile.offline()
        val user = userManager.createFake(uuid, name, profile)
        players[uuid] = PlayerState(user, name, intArrayOf(config.rankStart, 0, 0, 0, 0, 0, 0), profile)
    }

    private fun detach(player: PlayerState) {
        player.guild?.let { guilds.getValue(it).members.remove(player.user.uuid) }
        player.guild = null
    }

    private fun setStat(user: User, index: Int, value: Int) {
        when (index) {
            0 -> user.rank.points = value
            1 -> user.rank.kills = value
            2 -> user.rank.deaths = value
            3 -> user.rank.assists = value
            4 -> user.rank.logouts = value
        }
    }

    private fun value(player: PlayerState, index: Int): Double {
        if (index < 5) return player.stats[index].toDouble()
        if (index == 7) return player.stats[5].toDouble()
        if (index == 8) return player.stats[6] / 4.0
        val numerator = player.stats[1].toDouble() + if (index == 6) player.stats[3] else 0
        val deaths = player.stats[2]
        return if (deaths == 0) numerator else numerator / deaths
    }

    private fun guildValue(guild: GuildState, index: Int, average: Boolean): Double {
        val members = guild.members.map { players.getValue(it) }
        if (members.isEmpty()) return 0.0
        if (index >= 7) {
            val total = members.sumOf { value(it, index) }
            return if (average) total / members.size else total
        }
        if (average) {
            val result = members.sumOf { value(it, index) } / members.size
            return if (index < 5) result.toInt().toDouble() else result
        }
        if (index < 5) return members.sumOf { value(it, index) }
        val numerator = members.sumOf { it.stats[1].toDouble() + if (index == 6) it.stats[3] else 0 }
        val deaths = members.sumOf { it.stats[2] }
        return if (deaths == 0) numerator else numerator / deaths
    }

    private fun verifyEverything() {
        listOf("wars_won", "wars_lost", "war_streak").forEachIndexed { index, name ->
            val tracker = rankService.getGuildRankTracker(name).get()
            val expected = guilds.values.filter { it.members.isNotEmpty() && it.members.size >= config.minMembersToInclude }.sortedWith(
                compareByDescending<GuildState> { it.warStats[index] }
                    .thenBy(String.CASE_INSENSITIVE_ORDER) { it.name }.thenBy { it.guild.uuid }
            )
            assertEquals(expected.map { it.guild.uuid }, tracker.entitiesInTop().map { it.uuid }, "war order: $name")
            guilds.values.forEach { guild ->
                assertEquals(guild.warStats[index], tracker.valueOfEntity(guild.guild).get().toInt(), "war value: $name")
                val position = expected.indexOf(guild).let { if (it < 0) 0 else it + 1 }
                assertEquals(position, tracker.findPositionByEntity(guild.guild).orElse(0), "war position: $name")
            }
        }
        keys.forEachIndexed { index, key ->
            val userTracker = rankService.getUserRankTracker(key)
            val expectedPlayers = players.values.filter { !it.exempt }.sortedWith(
                compareByDescending<PlayerState> { value(it, index) }
                    .thenBy(String.CASE_INSENSITIVE_ORDER) { it.name }.thenBy { it.user.uuid }
            )
            assertEquals(expectedPlayers.map { it.user.uuid }, userTracker.entitiesInTop().map { it.uuid }, "user order: $key")
            assertEquals(expectedPlayers.size, userTracker.countEntitiesInTop(), "user count: $key")
            players.values.forEach { player ->
                assertEquals(value(player, index), userTracker.valueOfEntity(player.user).get().toDouble(), 1e-9, "user value: $key")
                val expectedPosition = expectedPlayers.indexOf(player).let { if (it < 0) 0 else it + 1 }
                assertEquals(expectedPosition, userTracker.findPositionByEntity(player.user).orElse(0), "user position: $key")
                assertEquals(!player.exempt, userTracker.isEntityIncludedInTop(player.user))
            }
            assertFalse(userTracker.findEntityByPosition(0).isPresent)
            assertFalse(userTracker.findEntityByPosition(expectedPlayers.size + 1).isPresent)
            expectedPlayers.forEachIndexed { position, player ->
                assertEquals(player.user, userTracker.findEntityByPosition(position + 1).orElse(null))
            }
            listOf(false, true).forEach { average ->
                val top = (if (average) "avg_" else "") + key.key().value()
                val tracker = rankService.getGuildRankTracker(top).get()
                val expectedGuilds = guilds.values.filter { it.members.isNotEmpty() && it.members.size >= config.minMembersToInclude }.sortedWith(
                    compareByDescending<GuildState> { guildValue(it, index, average) }
                        .thenBy(String.CASE_INSENSITIVE_ORDER) { it.name }.thenBy { it.guild.uuid }
                )
                assertEquals(expectedGuilds.map { it.guild.uuid }, tracker.entitiesInTop().map { it.uuid }, "guild order: $top")
                assertEquals(expectedGuilds.size, tracker.countEntitiesInTop(), "guild count: $top")
                guilds.values.forEach { guild ->
                    assertEquals(guildValue(guild, index, average), tracker.valueOfEntity(guild.guild).get().toDouble(), 1e-9, "guild value: $top")
                    val position = expectedGuilds.indexOf(guild).let { if (it < 0) 0 else it + 1 }
                    assertEquals(position, tracker.findPositionByEntity(guild.guild).orElse(0), "guild position: $top")
                    assertEquals(guild.members, guild.guild.members.map { it.uuid }.toSet(), "membership")
                }
                expectedGuilds.forEachIndexed { position, guild ->
                    assertEquals(guild.guild, tracker.findEntityByPosition(position + 1).orElse(null))
                }
                assertFalse(tracker.findEntityByPosition(expectedGuilds.size + 1).isPresent)
            }
        }
    }
}
