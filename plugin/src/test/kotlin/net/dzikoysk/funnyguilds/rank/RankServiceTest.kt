package net.dzikoysk.funnyguilds.rank

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel
import net.dzikoysk.funnyguilds.data.flat.seralizer.FlatUserSerializer
import net.dzikoysk.funnyguilds.data.util.YamlWrapper
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.user.UserStatKeys
import net.dzikoysk.funnyguilds.user.FakeUserProfile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mockito.lenient
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.bukkit.Bukkit
import java.util.Optional
import java.nio.file.Path
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class RankServiceTest : FunnyGuildsSpec() {

    private fun useExecutor(executor: Executor): FunnyRankService {
        val service = FunnyRankService(executor, config, userManager, guildManager)
        rankService = service
        lenient().`when`(funnyGuilds.rankService).thenReturn(service)
        return service
    }

    @Test
    fun `new users start with configured points and zero counters`() {
        config.rankStart = 1234
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        assertEquals(1234, user.rank.points)
        assertEquals(0, user.rank.kills)
        assertEquals(0.0, rankService.getUserStatValue(user, UserStatKeys.KDR).get())
        assertEquals(1, rankService.getUserTopPosition(user, UserStatKeys.POINTS).get())
    }

    @Test
    fun `a guild candidate does not join its owner before creation succeeds`() {
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        val candidate = Guild("Candidate", "TAG")
        candidate.setOwner(user)
        assertFalse(user.hasGuild())
        assertEquals(config.rankStart, candidate.rank.points)
        guildManager.addGuild(candidate)
        assertEquals(candidate, user.guild.get())
        assertEquals(config.rankStart, candidate.rank.points)
    }

    @Test
    fun `permission changes update exemptions without a stat change or a full rebuild`() {
        `when`(Bukkit.getOnlinePlayers()).thenReturn(emptyList())
        config.skipPrivilegedPlayersInRankPositions = true
        val profile = FakeUserProfile.offline()
        val user = userManager.createFake(UUID.randomUUID(), "Player", profile)
        val task = RankExemptionRefreshTask(config, userManager, rankService)
        profile.addPermission("funnyguilds.ranking.exempt")
        task.run()
        assertFalse(rankService.getUserTopPosition(user, UserStatKeys.POINTS).isPresent)
        assertEquals(config.rankStart, user.rank.points)
        profile.removePermission("funnyguilds.ranking.exempt")
        task.run()
        assertEquals(1, rankService.getUserTopPosition(user, UserStatKeys.POINTS).get())
    }

    @Test
    fun `offline permission polling is bounded instead of scanning every player in one tick`() {
        `when`(Bukkit.getOnlinePlayers()).thenReturn(emptyList())
        config.skipPrivilegedPlayersInRankPositions = true
        val lookups = AtomicInteger()
        repeat(100) { index ->
            val profile = object : FakeUserProfile(false, false, 0) {
                override fun hasPermission(permission: String): Boolean {
                    lookups.incrementAndGet()
                    return false
                }
            }
            userManager.createFake(UUID.randomUUID(), "Player$index", profile)
        }
        lookups.set(0)
        val task = RankExemptionRefreshTask(config, userManager, rankService)
        task.run()
        assertEquals(32, lookups.get())
        repeat(3) { task.run() }
        assertEquals(100, lookups.get())
    }

    @Test
    fun `stat changes are marked dirty and written by the existing file serializer`(@TempDir directory: Path) {
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guild.addMember(user)
        user.markUnchanged()
        guild.markUnchanged()
        user.rank.points = 1500
        user.rank.kills = 12
        user.rank.deaths = 3
        user.rank.assists = 6
        user.rank.logouts = 2
        assertTrue(user.wasChanged())
        assertTrue(guild.wasChanged())

        val file = directory.resolve("player.yml").toFile()
        val model = mock(FlatDataModel::class.java)
        `when`(model.getUserFile(user)).thenReturn(Optional.of(file))
        `when`(funnyGuilds.dataModel).thenReturn(model)
        assertTrue(FlatUserSerializer.serialize(user))
        val saved = YamlWrapper(file)
        assertEquals(listOf(1500, 12, 3, 6, 2), listOf("points", "kills", "deaths", "assists", "logouts").map { saved.getInt(it) })
        assertFalse(user.wasChanged())
        user.rank.updatePoints { it + 1 }
        assertTrue(user.wasChanged())
    }

    @Test
    fun `guild ratios preserve total ratio and average of member ratios as different values`() {
        val first = userManager.createFake(UUID.randomUUID(), "First")
        val second = userManager.createFake(UUID.randomUUID(), "Second")
        first.rank.kills = 10
        first.rank.deaths = 1
        first.rank.assists = 2
        second.rank.kills = 2
        second.rank.deaths = 2
        second.rank.assists = 2
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guild.addMember(first)
        guild.addMember(second)

        assertEquals(4.0f, guild.rank.kdr)
        assertEquals(5.5f, guild.rank.averageKDR)
        assertEquals(16.0f / 3.0f, guild.rank.kda)
        assertEquals(7.0f, guild.rank.averageKDA)
        guild.removeMember(first)
        assertEquals(1.0f, guild.rank.kdr)
        guild.removeMember(second)
        assertEquals(0, guild.rank.averagePoints)
        assertFalse(rankService.getGuildRankTracker("avg_points").get().findPositionByEntity(guild).isPresent)
    }

    @Test
    fun `membership updates refresh both guilds and removing a guild clears its positions`() {
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        val first = guildManager.addGuild(Guild("First", "ONE"))
        val second = guildManager.addGuild(Guild("Second", "TWO"))
        val tracker = rankService.getGuildRankTracker("avg_points").get()
        first.addMember(user)
        assertEquals(first, tracker.findEntityByPosition(1).get())
        user.setGuild(second)
        assertTrue(first.members.isEmpty())
        assertEquals(second, tracker.findEntityByPosition(1).get())
        assertEquals(1, tracker.countEntitiesInTop())
        guildManager.removeGuild(second)
        assertEquals(0, tracker.countEntitiesInTop())
        assertFalse(user.hasGuild())
        assertFalse(tracker.valueOfEntity(second).isPresent)
    }

    @Test
    fun `an unchanged value does not mark a user dirty`() {
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        user.markUnchanged()
        user.rank.points = config.rankStart
        assertFalse(user.wasChanged())
    }

    @Test
    fun `a guild row is marked dirty only when a tracked aggregate actually moves`() {
        val first = userManager.createFake(UUID.randomUUID(), "First")
        val second = userManager.createFake(UUID.randomUUID(), "Second")
        val third = userManager.createFake(UUID.randomUUID(), "Third")
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guild.addMember(first)
        guild.addMember(second)
        guild.addMember(third)
        first.rank.logouts = 1
        second.rank.logouts = 1
        third.rank.logouts = 1
        val logoutsTop = rankService.getGuildRankTracker("avg_logouts")
        assertTrue(logoutsTop.isPresent)
        guild.markUnchanged()
        // (1 + 1 + 2) / 3 truncates back to the same average of 1, so the guild row stays clean.
        first.rank.logouts = 2
        assertFalse(guild.wasChanged())
        guild.markUnchanged()
        // (1 + 1 + 10) / 3 = 4 moves the average, so the guild row is marked for the saver.
        first.rank.logouts = 10
        assertTrue(guild.wasChanged())
    }

    @Test
    fun `failed queued update does not strand later futures and closing flushes pending changes`() {
        val scheduled = mutableListOf<Runnable>()
        val service = useExecutor(Executor { scheduled.add(it) })
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        val failed = service.queueChangeUserStatValue(user, UserStatKeys.POINTS) {
            throw IllegalStateException("expected")
        }
        val successful = service.queueChangeUserStatValue(user, UserStatKeys.POINTS) { it + 10 }
        assertFalse(successful.isDone)
        assertEquals(1, scheduled.size)
        service.close()
        assertTrue(failed.isCompletedExceptionally)
        assertEquals(config.rankStart + 10, successful.join())
        assertEquals(config.rankStart + 10, user.rank.points)
        assertTrue(service.queueSetUserStatValue(user, UserStatKeys.POINTS, 0).isCompletedExceptionally)
        scheduled.forEach { it.run() }
    }

    @Test
    fun `a rejected scheduler completes queued futures exceptionally`() {
        val service = useExecutor(Executor { throw RejectedExecutionException("expected") })
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        assertTrue(service.queueSetUserStatValue(user, UserStatKeys.POINTS, 1).isCompletedExceptionally)
    }

    @Test
    fun `queued updates cannot resurrect a removed user`() {
        val scheduled = mutableListOf<Runnable>()
        val service = useExecutor(Executor { scheduled.add(it) })
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        val update = service.queueSetUserStatValue(user, UserStatKeys.POINTS, 123)
        userManager.removeUser(user)
        scheduled.forEach { it.run() }
        assertTrue(update.isCompletedExceptionally)
        assertEquals(0, service.getUserRankTracker(UserStatKeys.POINTS).countEntitiesInTop())
    }

    @Test
    @Timeout(30)
    fun `parallel producers do not lose updates or expose duplicate ranking entries`() {
        val workers = Executors.newFixedThreadPool(4)
        val producers = Executors.newFixedThreadPool(4)
        try {
            config.skipPrivilegedPlayersInRankPositions = true
            val service = useExecutor(workers)
            val serverThread = Thread.currentThread()
            val profile = object : FakeUserProfile(false, false, 0) {
                override fun hasPermission(permission: String): Boolean {
                    assertEquals(serverThread, Thread.currentThread(), "permission lookups must stay on the server thread")
                    return false
                }
            }
            val user = userManager.createFake(UUID.randomUUID(), "Player", profile)
            val other = userManager.createFake(UUID.randomUUID(), "Other")
            val guild = guildManager.addGuild(Guild("Guild", "TAG"))
            guild.addMember(user)
            guild.addMember(other)
            val guildTracker = service.getGuildRankTracker("points").get()
            val tracker = service.getUserRankTracker(UserStatKeys.POINTS)
            val tasks = (1..4).map {
                CompletableFuture.runAsync({
                    repeat(1000) {
                        service.queueChangeUserStatValue(user, UserStatKeys.POINTS) { it + 1 }.join()
                        val snapshot = tracker.entitiesInTop().map { it.uuid }
                        assertEquals(2, snapshot.size)
                        assertEquals(2, snapshot.toSet().size)
                    }
                }, producers)
            }
            CompletableFuture.allOf(*tasks.toTypedArray()).get(25, TimeUnit.SECONDS)
            assertEquals(config.rankStart + 4000, user.rank.points)
            assertEquals(config.rankStart * 2 + 4000, guildTracker.valueOfEntity(guild).get().toInt())
            assertEquals(user, tracker.findEntityByPosition(1).get())
            service.close()
        } finally {
            producers.shutdownNow()
            workers.shutdownNow()
            assertTrue(producers.awaitTermination(5, TimeUnit.SECONDS))
            assertTrue(workers.awaitTermination(5, TimeUnit.SECONDS))
        }
    }

    @Test
    fun `derived stats reject direct writes`() {
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        assertThrows(IllegalArgumentException::class.java) {
            rankService.setUserStatValue(user, UserStatKeys.KDR, 1.0)
        }
    }
}
