package net.dzikoysk.funnyguilds.listener

import java.util.UUID
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.config.message.FunnyMessageDispatcher
import net.dzikoysk.funnyguilds.config.message.MessageService
import net.dzikoysk.funnyguilds.event.guild.GuildPreRegionTransitionEvent
import net.dzikoysk.funnyguilds.event.guild.GuildRegionEnterEvent
import net.dzikoysk.funnyguilds.event.guild.GuildRegionLeaveEvent
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.Region
import net.dzikoysk.funnyguilds.listener.region.PlayerMove
import net.dzikoysk.funnyguilds.nms.heart.GuildEntityHelper
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.scheduler.BukkitScheduler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.RETURNS_SELF
import org.mockito.Mockito.lenient
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class PlayerRegionTransitionTest : FunnyGuildsSpec() {

    private val player = mock(Player::class.java)
    private val scheduler = mock(BukkitScheduler::class.java)
    private val dispatcher = mock(FunnyMessageDispatcher::class.java, RETURNS_SELF)
    private val listener = PlayerMove()
    private val world = mock(World::class.java)
    private val events = mutableListOf<Event>()
    private val asyncNotifications = mutableListOf<Runnable>()
    private val delayedHearts = mutableListOf<Runnable>()
    private var veto = false
    private var cancelLegacyLeave = false

    @BeforeEach
    fun prepareListener() {
        val playerId = UUID.randomUUID()
        userManager.createFake(playerId, "Steve")
        lenient().`when`(player.uniqueId).thenReturn(playerId)
        lenient().`when`(player.name).thenReturn("Steve")

        val server = mock(Server::class.java)
        lenient().`when`(funnyGuilds.server).thenReturn(server)
        lenient().`when`(server.scheduler).thenReturn(scheduler)
        lenient().`when`(scheduler.runTaskAsynchronously(eq(funnyGuilds), any(Runnable::class.java))).thenAnswer {
            asyncNotifications.add(it.getArgument(1))
            null
        }
        lenient().`when`(scheduler.runTaskLater(eq(funnyGuilds), any(Runnable::class.java), eq(40L))).thenAnswer {
            delayedHearts.add(it.getArgument(1))
            null
        }

        val messageService = mock(MessageService::class.java)
        lenient().`when`(messageService.getMessage(any())).thenReturn(dispatcher)
        val pluginManager = Bukkit.getPluginManager()
        lenient().doAnswer {
            val event: Event = it.getArgument(0)
            events.add(event)
            if (veto && event is GuildPreRegionTransitionEvent) {
                event.isCancelled = true
            }
            if (cancelLegacyLeave && event is GuildRegionLeaveEvent) {
                event.isCancelled = true
            }
            null
        }.`when`(pluginManager).callEvent(any(Event::class.java))

        config.heart.createEntityType = EntityType.END_CRYSTAL
        listener.plugin = funnyGuilds
        listener.logger = FunnyGuilds.getPluginLogger()
        listener.config = config
        listener.userManager = userManager
        listener.regionManager = funnyGuilds.regionManager
        listener.guildEntityHelper = mock(GuildEntityHelper::class.java)
        listener.messageService = messageService
    }

    @Test
    fun `vertical movement enters and leaves a region`() {
        val guild = addRegion(world)
        val outside = Location(world, 0.0, 0.0, 0.0)
        val inside = outside.clone().add(0.0, 1.0, 0.0)

        val enter = PlayerMoveEvent(player, outside, inside)
        listener.onMove(enter)
        val preEnter = assertIs<GuildPreRegionTransitionEvent>(events.single())
        assertEquals(guild, preEnter.toGuild.orElseThrow())
        assertTrue(preEnter.fromGuild.isEmpty)
        assertFalse(preEnter.isAsynchronous)
        listener.onMoveAccepted(enter)
        asyncNotifications.removeFirst().run()
        assertTrue(assertIs<GuildRegionEnterEvent>(events.last()).isAsynchronous)

        val leave = PlayerMoveEvent(player, inside, outside)
        listener.onMove(leave)
        val preLeave = assertIs<GuildPreRegionTransitionEvent>(events.last())
        assertEquals(guild, preLeave.fromGuild.orElseThrow())
        assertTrue(preLeave.toGuild.isEmpty)
        listener.onMoveAccepted(leave)
        asyncNotifications.removeFirst().run()
        assertTrue(assertIs<GuildRegionLeaveEvent>(events.last()).isAsynchronous)
        verify(dispatcher, times(3)).send()
    }

    @Test
    fun `veto cancels teleport between worlds at the same coordinates without side effects`() {
        val otherWorld = mock(World::class.java)
        val source = addRegion(world, "FROM")
        val destination = addRegion(otherWorld, "TO")
        val teleport = teleportTo(otherWorld)
        veto = true

        listener.onTeleport(teleport)
        assertTrue(teleport.isCancelled)
        val attempt = assertIs<GuildPreRegionTransitionEvent>(events.single())
        assertEquals(source, attempt.fromGuild.orElseThrow())
        assertEquals(destination, attempt.toGuild.orElseThrow())

        listener.onTeleportAccepted(teleport)
        assertEquals(1, events.size)
        verifyNoInteractions(scheduler, dispatcher, listener.guildEntityHelper)
    }

    @Test
    fun `accepted teleport queues one async notification batch with leave before enter`() {
        val otherWorld = mock(World::class.java)
        val source = addRegion(world, "FROM")
        val destination = addRegion(otherWorld, "TO")
        val teleport = teleportTo(otherWorld)

        listener.onTeleport(teleport)
        listener.onTeleportAccepted(teleport)
        assertFalse(teleport.isCancelled)
        assertEquals(1, events.size)
        cancelLegacyLeave = true
        asyncNotifications.single().run()

        val leave = assertIs<GuildRegionLeaveEvent>(events[1])
        val enter = assertIs<GuildRegionEnterEvent>(events[2])
        assertEquals(source, leave.guild)
        assertEquals(destination, enter.guild)
        assertTrue(leave.isCancelled)
        assertFalse(teleport.isCancelled)
        assertTrue(leave.isAsynchronous)
        assertTrue(enter.isAsynchronous)

        `when`(player.isOnline).thenReturn(true)
        `when`(player.location).thenReturn(teleport.from)
        delayedHearts.single().run()
        verifyNoInteractions(listener.guildEntityHelper)
    }

    @Test
    fun `another listener can cancel movement before notifications`() {
        addRegion(world)
        val move = PlayerMoveEvent(player, Location(world, 0.0, 0.0, 0.0), Location(world, 0.0, 1.0, 0.0))
        listener.onMove(move)
        move.isCancelled = true
        listener.onMoveAccepted(move)

        assertIs<GuildPreRegionTransitionEvent>(events.single())
        verifyNoInteractions(scheduler, dispatcher)
    }

    @Test
    fun `rapid transitions remain ordered even when async tasks are taken in reverse order`() {
        val otherWorld = mock(World::class.java)
        val first = addRegion(world, "FROM")
        val second = addRegion(otherWorld, "TO")
        val inside = Location(world, 0.0, 64.0, 0.0)
        val outside = Location(world, 10.0, 64.0, 0.0)

        listener.onMoveAccepted(PlayerMoveEvent(player, outside, inside))
        listener.onMoveAccepted(PlayerMoveEvent(player, inside, outside))
        listener.onTeleportAccepted(PlayerTeleportEvent(
            player, outside, Location(otherWorld, 0.0, 64.0, 0.0), PlayerTeleportEvent.TeleportCause.PLUGIN
        ))
        asyncNotifications.asReversed().forEach(Runnable::run)

        assertEquals(3, events.size)
        assertEquals(first, assertIs<GuildRegionEnterEvent>(events[0]).guild)
        assertEquals(first, assertIs<GuildRegionLeaveEvent>(events[1]).guild)
        assertEquals(second, assertIs<GuildRegionEnterEvent>(events[2]).guild)
        assertEquals(1, asyncNotifications.size)
    }

    private fun addRegion(regionWorld: World, tag: String = "TAG"): Guild {
        lenient().`when`(regionWorld.maxHeight).thenReturn(256)
        val guild = Guild(tag, tag)
        listener.regionManager.addRegion(Region(guild, Location(regionWorld, 0.0, 64.0, 0.0), 4))
        return guild
    }

    private fun teleportTo(destination: World) = PlayerTeleportEvent(
        player, Location(world, 0.0, 64.0, 0.0), Location(destination, 0.0, 64.0, 0.0),
        PlayerTeleportEvent.TeleportCause.PLUGIN
    )
}
