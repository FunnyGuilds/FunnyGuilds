package net.dzikoysk.funnyguilds.rank

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel
import net.dzikoysk.funnyguilds.data.flat.seralizer.FlatUserSerializer
import net.dzikoysk.funnyguilds.data.util.YamlWrapper
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.GuildStatKeys
import net.dzikoysk.funnyguilds.user.UserStatKeys
import net.dzikoysk.funnyguilds.user.FakeUserProfile
import net.kyori.adventure.key.Key
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
import org.bukkit.entity.Player
import java.util.Optional
import java.nio.file.Path
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class RankServiceTest : FunnyGuildsSpec() {

    private fun useExecutor(executor: Executor): FunnyRankService {
        val service = FunnyRankService(executor, config, userManager, guildManager)
        rankService = service
        lenient().`when`(funnyGuilds.rankService).thenReturn(service)
        return service
    }

    /**
     * Reads a guild aggregate the way the removed GuildRank adapter did: the value already tracked
     * for the guild, or a fresh computation for guilds the service does not track yet (for example
     * an unregistered candidate). Derived ratios are only ever read, never written.
     */
    private fun <T : Number, R : Number> guildStatValue(guild: Guild, key: StatKey<T>, aggregation: GuildStatAggregationType<R>): R =
        rankService.getGuildStatValue(guild, key, aggregation)
            .orElseGet { aggregation.computeValue(rankService, guild, key) }

    @Test
    fun `new users start with configured points and zero counters`() {
        config.rankStart = 1234
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        assertEquals(1234, rankService.getUserStatValue(user, UserStatKeys.POINTS).orElse(0))
        assertEquals(0, rankService.getUserStatValue(user, UserStatKeys.KILLS).orElse(0))
        assertEquals(0.0, rankService.getUserStatValue(user, UserStatKeys.KDR).get())
        assertEquals(1, rankService.getUserTopPosition(user, UserStatKeys.POINTS).get())
    }

    @Test
    fun `a guild candidate does not join its owner before creation succeeds`() {
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        val candidate = Guild("Candidate", "TAG")
        candidate.setOwner(user)
        assertFalse(user.hasGuild())
        assertEquals(config.rankStart, guildStatValue(candidate, UserStatKeys.POINTS, GuildStatAggregationType.TOTAL_LONG).toInt())
        guildManager.addGuild(candidate)
        assertEquals(candidate, user.guild.get())
        assertEquals(config.rankStart, guildStatValue(candidate, UserStatKeys.POINTS, GuildStatAggregationType.TOTAL_LONG).toInt())
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
        assertEquals(config.rankStart, rankService.getUserStatValue(user, UserStatKeys.POINTS).orElse(0))
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
        guildManager.addMember(guild, user)
        user.markUnchanged()
        guild.markUnchanged()
        rankService.setUserStatValue(user, UserStatKeys.POINTS, 1500)
        rankService.setUserStatValue(user, UserStatKeys.KILLS, 12)
        rankService.setUserStatValue(user, UserStatKeys.DEATHS, 3)
        rankService.setUserStatValue(user, UserStatKeys.ASSISTS, 6)
        rankService.setUserStatValue(user, UserStatKeys.LOGOUTS, 2)
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
        rankService.changeUserStatValue(user, UserStatKeys.POINTS) { it + 1 }
        assertTrue(user.wasChanged())
    }

    @Test
    fun `guild ratios preserve total ratio and average of member ratios as different values`() {
        val first = userManager.createFake(UUID.randomUUID(), "First")
        val second = userManager.createFake(UUID.randomUUID(), "Second")
        rankService.setUserStatValue(first, UserStatKeys.KILLS, 10)
        rankService.setUserStatValue(first, UserStatKeys.DEATHS, 1)
        rankService.setUserStatValue(first, UserStatKeys.ASSISTS, 2)
        rankService.setUserStatValue(second, UserStatKeys.KILLS, 2)
        rankService.setUserStatValue(second, UserStatKeys.DEATHS, 2)
        rankService.setUserStatValue(second, UserStatKeys.ASSISTS, 2)
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guildManager.addMember(guild, first)
        guildManager.addMember(guild, second)

        assertEquals(4.0f, guildStatValue(guild, UserStatKeys.KDR, GuildStatAggregationType.TOTAL_DOUBLE).toFloat())
        assertEquals(5.5f, guildStatValue(guild, UserStatKeys.KDR, GuildStatAggregationType.AVERAGE).toFloat())
        assertEquals(16.0f / 3.0f, guildStatValue(guild, UserStatKeys.KDA, GuildStatAggregationType.TOTAL_DOUBLE).toFloat())
        assertEquals(7.0f, guildStatValue(guild, UserStatKeys.KDA, GuildStatAggregationType.AVERAGE).toFloat())
        guildManager.removeMember(guild, first)
        assertEquals(1.0f, guildStatValue(guild, UserStatKeys.KDR, GuildStatAggregationType.TOTAL_DOUBLE).toFloat())
        guildManager.removeMember(guild, second)
        assertEquals(0, guildStatValue(guild, UserStatKeys.POINTS, GuildStatAggregationType.AVERAGE_INT))
        assertFalse(rankService.getGuildRankTracker("avg_points").get().findPositionByEntity(guild).isPresent)
    }

    @Test
    fun `guild ratios refresh when deaths change without changing the member ratio`() {
        val contributor = userManager.createFake(UUID.randomUUID(), "Contributor")
        val other = userManager.createFake(UUID.randomUUID(), "Other")
        rankService.setUserStatValue(contributor, UserStatKeys.KILLS, 4)
        rankService.setUserStatValue(contributor, UserStatKeys.DEATHS, 1)
        rankService.setUserStatValue(contributor, UserStatKeys.ASSISTS, 2)
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guildManager.addMember(guild, contributor)
        guildManager.addMember(guild, other)
        assertEquals(4.0f, guildStatValue(guild, UserStatKeys.KDR, GuildStatAggregationType.TOTAL_DOUBLE).toFloat())
        assertEquals(6.0f, guildStatValue(guild, UserStatKeys.KDA, GuildStatAggregationType.TOTAL_DOUBLE).toFloat())

        rankService.setUserStatValue(other, UserStatKeys.DEATHS, 1)

        assertEquals(0.0, rankService.getUserStatValue(other, UserStatKeys.KDR).get())
        assertEquals(0.0, rankService.getUserStatValue(other, UserStatKeys.KDA).get())
        assertEquals(2.0f, guildStatValue(guild, UserStatKeys.KDR, GuildStatAggregationType.TOTAL_DOUBLE).toFloat())
        assertEquals(3.0f, guildStatValue(guild, UserStatKeys.KDA, GuildStatAggregationType.TOTAL_DOUBLE).toFloat())
    }

    @Test
    fun `membership updates refresh both guilds and removing a guild clears its positions`() {
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        val first = guildManager.addGuild(Guild("First", "ONE"))
        val second = guildManager.addGuild(Guild("Second", "TWO"))
        val tracker = rankService.getGuildRankTracker("avg_points").get()
        guildManager.addMember(first, user)
        assertEquals(first, tracker.findEntityByPosition(1).get())
        guildManager.removeMember(first, user)
        guildManager.addMember(second, user)
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
        rankService.setUserStatValue(user, UserStatKeys.POINTS, config.rankStart)
        assertFalse(user.wasChanged())
    }

    @Test
    fun `a guild row is marked dirty only when a tracked aggregate actually moves`() {
        val first = userManager.createFake(UUID.randomUUID(), "First")
        val second = userManager.createFake(UUID.randomUUID(), "Second")
        val third = userManager.createFake(UUID.randomUUID(), "Third")
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guildManager.addMember(guild, first)
        guildManager.addMember(guild, second)
        guildManager.addMember(guild, third)
        rankService.setUserStatValue(first, UserStatKeys.LOGOUTS, 1)
        rankService.setUserStatValue(second, UserStatKeys.LOGOUTS, 1)
        rankService.setUserStatValue(third, UserStatKeys.LOGOUTS, 1)
        val logoutsTop = rankService.getGuildRankTracker("avg_logouts")
        assertTrue(logoutsTop.isPresent)
        guild.markUnchanged()
        // (1 + 1 + 2) / 3 truncates back to the same average of 1, so the guild row stays clean.
        rankService.setUserStatValue(first, UserStatKeys.LOGOUTS, 2)
        assertFalse(guild.wasChanged())
        guild.markUnchanged()
        // (1 + 1 + 10) / 3 = 4 moves the average, so the guild row is marked for the saver.
        rankService.setUserStatValue(first, UserStatKeys.LOGOUTS, 10)
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
        assertEquals(config.rankStart + 10, rankService.getUserStatValue(user, UserStatKeys.POINTS).orElse(0))
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
            guildManager.addMember(guild, user)
            guildManager.addMember(guild, other)
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
            assertEquals(config.rankStart + 4000, rankService.getUserStatValue(user, UserStatKeys.POINTS).orElse(0))
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
    fun `total long aggregation keeps sums beyond the Int range exact`() {
        val first = userManager.createFake(UUID.randomUUID(), "First")
        val second = userManager.createFake(UUID.randomUUID(), "Second")
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guildManager.addMember(guild, first)
        guildManager.addMember(guild, second)

        rankService.setUserStatValue(first, UserStatKeys.KILLS, Int.MAX_VALUE)
        rankService.setUserStatValue(second, UserStatKeys.KILLS, Int.MAX_VALUE)
        // MAX_VALUE + MAX_VALUE leaves the Int range; the Long total stays exact, and the guild
        // top selected through forTop carries the same exact Long.
        assertEquals(2L * Int.MAX_VALUE, GuildStatAggregationType.TOTAL_LONG.computeValue(rankService, guild, UserStatKeys.KILLS))
        assertEquals(2L * Int.MAX_VALUE, rankService.getGuildRankTracker("kills").get().valueOfEntity(guild).get() as Long)

        rankService.setUserStatValue(first, UserStatKeys.KILLS, Int.MIN_VALUE)
        rankService.setUserStatValue(second, UserStatKeys.KILLS, Int.MIN_VALUE)
        // The tracker exists by now, so these writes flow through the incremental update path.
        assertEquals(2L * Int.MIN_VALUE, GuildStatAggregationType.TOTAL_LONG.computeValue(rankService, guild, UserStatKeys.KILLS))
        assertEquals(2L * Int.MIN_VALUE, rankService.getGuildRankTracker("kills").get().valueOfEntity(guild).get() as Long)
    }

    @Test
    fun `guild top ordering stays exact for Long totals above the double precision limit`() {
        // 2^53 and 2^53 + 1 collapse to the same double, so a Double.compare-based ranking would
        // treat the totals as tied and the name tie-break would put AAA first. Long.compare must
        // keep the guild with the exactly bigger total on position 1.
        assertEquals(0, (1L shl 53).toDouble().compareTo(((1L shl 53) + 1).toDouble()))
        val hugeTotal = GuildStatAggregationType.create(
                Key.key("arena", "huge-total"), Long::class.javaObjectType
        ) { _, guild, _ -> if (guild.tag == "ZZZ") (1L shl 53) + 1 else 1L shl 53 }

        val low = userManager.createFake(UUID.randomUUID(), "Low")
        val high = userManager.createFake(UUID.randomUUID(), "High")
        val aaa = guildManager.addGuild(Guild("AAA", "AAA"))
        val zzz = guildManager.addGuild(Guild("ZZZ", "ZZZ"))
        guildManager.addMember(aaa, low)
        guildManager.addMember(zzz, high)

        val tracker = rankService.getGuildRankTracker(UserStatKeys.KILLS, hugeTotal)
        assertEquals(zzz, tracker.findEntityByPosition(1).get())
        assertEquals(aaa, tracker.findEntityByPosition(2).get())
        assertEquals(1, tracker.findPositionByEntity(zzz).get())
    }

    @Test
    fun `guild KDR and KDA divide exact raw sums beyond the Int range`() {
        val first = userManager.createFake(UUID.randomUUID(), "First")
        val second = userManager.createFake(UUID.randomUUID(), "Second")
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guildManager.addMember(guild, first)
        guildManager.addMember(guild, second)

        // Raw sums are exact Longs: (MAX + MAX) kills and assists over 3 deaths. A saturated Int
        // total would divide 2^31 - 1 by 3 instead.
        rankService.setUserStatValue(first, UserStatKeys.KILLS, Int.MAX_VALUE)
        rankService.setUserStatValue(second, UserStatKeys.KILLS, Int.MAX_VALUE)
        rankService.setUserStatValue(first, UserStatKeys.DEATHS, 1)
        rankService.setUserStatValue(second, UserStatKeys.DEATHS, 2)
        rankService.setUserStatValue(first, UserStatKeys.ASSISTS, Int.MAX_VALUE)
        rankService.setUserStatValue(second, UserStatKeys.ASSISTS, 0)

        val exactKills = 2L * Int.MAX_VALUE
        assertEquals((exactKills / 3.0).toFloat(), guildStatValue(guild, UserStatKeys.KDR, GuildStatAggregationType.TOTAL_DOUBLE).toFloat())
        assertEquals(((exactKills + Int.MAX_VALUE) / 3.0).toFloat(), guildStatValue(guild, UserStatKeys.KDA, GuildStatAggregationType.TOTAL_DOUBLE).toFloat())
        // Averaging still means the mean of member ratios, not the ratio of sums.
        val averageOfRatios = ((Int.MAX_VALUE / 1.0) + (Int.MAX_VALUE / 2.0)) / 2
        assertEquals(averageOfRatios.toFloat(), guildStatValue(guild, UserStatKeys.KDR, GuildStatAggregationType.AVERAGE).toFloat())
    }

    @Test
    fun `native guild counters read back exactly under the Long total aggregation`() {
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guild.warsWon = 7

        assertEquals(7L, GuildStatAggregationType.TOTAL_LONG.computeValue(rankService, guild, GuildStatKeys.WARS_WON))
        assertEquals(7L, rankService.getGuildRankTracker(GuildStatKeys.WARS_WON, GuildStatAggregationType.TOTAL_LONG).valueOfEntity(guild).get())
    }

    @Test
    fun `average int aggregation divides the exact long sum with truncation toward zero`() {
        val first = userManager.createFake(UUID.randomUUID(), "First")
        val second = userManager.createFake(UUID.randomUUID(), "Second")
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guild.addMember(first)
        guild.addMember(second)

        // (MAX_VALUE + MAX_VALUE) / 2 = MAX_VALUE exactly; Int arithmetic would wrap to -1.
        rankService.setUserStatValue(first, UserStatKeys.KILLS, Int.MAX_VALUE)
        rankService.setUserStatValue(second, UserStatKeys.KILLS, Int.MAX_VALUE)
        assertEquals(Int.MAX_VALUE, GuildStatAggregationType.AVERAGE_INT.computeValue(rankService, guild, UserStatKeys.KILLS))

        // (MIN_VALUE + MIN_VALUE) / 2 = MIN_VALUE exactly; Int arithmetic would wrap to 0.
        rankService.setUserStatValue(first, UserStatKeys.KILLS, Int.MIN_VALUE)
        rankService.setUserStatValue(second, UserStatKeys.KILLS, Int.MIN_VALUE)
        assertEquals(Int.MIN_VALUE, GuildStatAggregationType.AVERAGE_INT.computeValue(rankService, guild, UserStatKeys.KILLS))

        // Legacy truncation toward zero: -3 / 2 is -1, not -2.
        rankService.setUserStatValue(first, UserStatKeys.KILLS, -5)
        rankService.setUserStatValue(second, UserStatKeys.KILLS, 2)
        assertEquals(-3L, GuildStatAggregationType.TOTAL_LONG.computeValue(rankService, guild, UserStatKeys.KILLS))
        assertEquals(-1, GuildStatAggregationType.AVERAGE_INT.computeValue(rankService, guild, UserStatKeys.KILLS))
    }

    @Test
    fun `a queued absolute set overwrites a newer synchronous write with its captured value`() {
        val scheduled = mutableListOf<Runnable>()
        val service = useExecutor(Executor { scheduled.add(it) })
        val user = userManager.createFake(UUID.randomUUID(), "Player")

        val queued = service.queueSetUserStatValue(user, UserStatKeys.POINTS, 555)
        service.setUserStatValue(user, UserStatKeys.POINTS, 100)
        assertEquals(100, rankService.getUserStatValue(user, UserStatKeys.POINTS).orElse(0))
        assertFalse(queued.isDone)

        scheduled.forEach { it.run() }
        assertEquals(555, queued.join())
        assertEquals(555, rankService.getUserStatValue(user, UserStatKeys.POINTS).orElse(0))
    }

    @Test
    @Timeout(30)
    fun `a synchronous write lands between queued updates instead of waiting for the whole batch`() {
        val scheduled = mutableListOf<Runnable>()
        val service = useExecutor(Executor { scheduled.add(it) })
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        val firstUpdateStarted = CompletableFuture<Void>()
        val releaseFirstUpdate = CompletableFuture<Void>()
        val syncWriteLanded = AtomicBoolean(false)
        val batchFinishedAfterSync = AtomicBoolean(false)
        val batchDone = CompletableFuture<Void>()

        val firstQueued = service.queueChangeUserStatValue(user, UserStatKeys.POINTS) { current ->
            firstUpdateStarted.complete(null)
            // Bounded wait: a regression that keeps the monitor through the drain fails this
            // update instead of deadlocking the test.
            releaseFirstUpdate.get(10, TimeUnit.SECONDS)
            current
        }
        repeat(300) { service.queueChangeUserStatValue(user, UserStatKeys.POINTS) { it + 1 } }
        service.queueChangeUserStatValue(user, UserStatKeys.POINTS) { current ->
            batchFinishedAfterSync.set(syncWriteLanded.get())
            batchDone.complete(null)
            current
        }
        assertEquals(1, scheduled.size, "queued updates must share a single active drain")

        val interloper = Thread {
            try {
                firstUpdateStarted.get(10, TimeUnit.SECONDS)
                service.setUserStatValue(user, UserStatKeys.POINTS, 555)
                syncWriteLanded.set(true)
            } catch (failure: Exception) {
                // Leave the flag unset; the ordering assertions below report the failure.
            }
        }
        val drain = Thread { scheduled[0].run() }
        drain.start()
        interloper.start()
        firstUpdateStarted.get(10, TimeUnit.SECONDS)
        releaseFirstUpdate.complete(null)
        batchDone.get(25, TimeUnit.SECONDS)
        interloper.join(TimeUnit.SECONDS.toMillis(5))

        assertTrue(firstQueued.isDone && !firstQueued.isCompletedExceptionally)
        assertTrue(syncWriteLanded.get(), "the synchronous write must complete without waiting for the batch")
        assertTrue(batchFinishedAfterSync.get(), "the synchronous write must land between queued updates, not after the batch")
        assertTrue(rankService.getUserStatValue(user, UserStatKeys.POINTS).orElse(0) >= 555)
    }

    @Test
    @Timeout(30)
    fun `close completes an update that was polled but not started yet`() {
        val scheduled = mutableListOf<Runnable>()
        val service = useExecutor(Executor { scheduled.add(it) })
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        val pollObserved = CompletableFuture<Void>()
        val runUpdate = CompletableFuture<Void>()

        // Park the drain with the service monitor free, between the poll and the action: exactly the
        // window where close() used to drain the queue and flip to closed before the polled update
        // ever ran, failing an update that had already been accepted.
        service.afterPollHook = Runnable {
            if (pollObserved.complete(null)) {
                runUpdate.get(10, TimeUnit.SECONDS)
            }
        }
        val firstQueued = service.queueChangeUserStatValue(user, UserStatKeys.POINTS) { it + 5 }
        val secondQueued = service.queueChangeUserStatValue(user, UserStatKeys.POINTS) { it + 7 }
        assertEquals(1, scheduled.size)

        val drain = Thread { scheduled[0].run() }
        drain.start()
        pollObserved.get(10, TimeUnit.SECONDS)

        val closer = Thread { service.close() }
        closer.start()
        // The second update can only be completed by close's inline drain, so this proves close ran
        // its whole synchronized block while the first update sat parked and unstarted.
        assertEquals(config.rankStart + 7, secondQueued.get(10, TimeUnit.SECONDS))
        assertFalse(firstQueued.isDone, "close must wait for the parked update instead of failing it as closed")

        runUpdate.complete(null)
        assertEquals(config.rankStart + 12, firstQueued.get(10, TimeUnit.SECONDS))
        closer.join(TimeUnit.SECONDS.toMillis(10))
        assertFalse(closer.isAlive, "close must not return before the accepted update completed")
        assertEquals(config.rankStart + 12, rankService.getUserStatValue(user, UserStatKeys.POINTS).orElse(0))
        assertTrue(service.queueSetUserStatValue(user, UserStatKeys.POINTS, 0).isCompletedExceptionally)
        assertThrows(IllegalStateException::class.java) { service.setUserStatValue(user, UserStatKeys.POINTS, 0) }
        service.afterPollHook = null
    }

    @Test
    @Timeout(30)
    fun `close waits for an update that is already running before completing`() {
        val scheduled = mutableListOf<Runnable>()
        val service = useExecutor(Executor { scheduled.add(it) })
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        val updateStarted = CompletableFuture<Void>()
        val releaseUpdate = CompletableFuture<Void>()

        val running = service.queueChangeUserStatValue(user, UserStatKeys.POINTS) { current ->
            updateStarted.complete(null)
            releaseUpdate.get(10, TimeUnit.SECONDS)
            current + 5
        }
        assertEquals(1, scheduled.size)
        val drain = Thread { scheduled[0].run() }
        drain.start()
        updateStarted.get(10, TimeUnit.SECONDS)

        val closer = Thread { service.close() }
        closer.start()
        assertTrue(closer.isAlive, "close must not complete while an accepted update is still running")
        releaseUpdate.complete(null)

        assertEquals(config.rankStart + 5, running.get(10, TimeUnit.SECONDS))
        closer.join(TimeUnit.SECONDS.toMillis(10))
        assertFalse(closer.isAlive)
        assertTrue(service.queueSetUserStatValue(user, UserStatKeys.POINTS, 0).isCompletedExceptionally)
        assertThrows(IllegalStateException::class.java) { service.setUserStatValue(user, UserStatKeys.POINTS, 0) }
    }

    @Test
    @Timeout(30)
    fun `close from within an update function completes the queue without deadlocking`() {
        val scheduled = mutableListOf<Runnable>()
        val service = useExecutor(Executor { scheduled.add(it) })
        val user = userManager.createFake(UUID.randomUUID(), "Player")

        // close() cannot wait for the update that invoked it without waiting for its own caller, so
        // that update completes when its function returns - and its write then lands after close,
        // failing like any write rejected by the closed state. The queue behind it is still drained.
        val selfClosing = service.queueChangeUserStatValue(user, UserStatKeys.POINTS) { current ->
            service.close()
            current
        }
        val follower = service.queueChangeUserStatValue(user, UserStatKeys.POINTS) { it + 7 }
        assertEquals(1, scheduled.size)

        val drain = Thread { scheduled[0].run() }
        drain.start()
        assertThrows(ExecutionException::class.java) { selfClosing.get(10, TimeUnit.SECONDS) }
        assertEquals(config.rankStart + 7, follower.get(10, TimeUnit.SECONDS), "close must complete updates queued behind the closing one")
        drain.join(TimeUnit.SECONDS.toMillis(10))
        assertFalse(drain.isAlive)
        assertThrows(IllegalStateException::class.java) { service.setUserStatValue(user, UserStatKeys.POINTS, 0) }
    }

    @Test
    fun `online players refresh immediately and their stale offline queue entries do not consume the check budget`() {
        config.skipPrivilegedPlayersInRankPositions = true
        val onlineProfile = FakeUserProfile.offline()
        val onlineUser = userManager.createFake(UUID.randomUUID(), "Online", onlineProfile)
        val lookups = AtomicInteger()
        repeat(33) { index ->
            val profile = object : FakeUserProfile(false, false, 0) {
                override fun hasPermission(permission: String): Boolean {
                    lookups.incrementAndGet()
                    return false
                }
            }
            userManager.createFake(UUID.randomUUID(), "Offline$index", profile)
        }
        onlineProfile.addPermission("funnyguilds.ranking.exempt")
        val player = mock(Player::class.java)
        `when`(player.uniqueId).thenReturn(onlineUser.uuid)
        `when`(Bukkit.getOnlinePlayers()).thenReturn(listOf(player))

        lookups.set(0)
        val task = RankExemptionRefreshTask(config, userManager, rankService)
        task.run()

        // The immediate branch must have picked up the exemption of the online player even though
        // his UUID also sits in the freshly seeded offline queue.
        assertFalse(rankService.getUserTopPosition(onlineUser, UserStatKeys.POINTS).isPresent)
        // 33 offline users are pending; all 32 budgeted checks must go to them, none lost to
        // skipping the online player's stale queue entry.
        assertEquals(32, lookups.get())
        task.run()
        assertEquals(33, lookups.get())
    }

    @Test
    fun `derived stats reject direct writes`() {
        val user = userManager.createFake(UUID.randomUUID(), "Player")
        assertThrows(IllegalArgumentException::class.java) {
            rankService.setUserStatValue(user, UserStatKeys.KDR, 1.0)
        }
    }
}
