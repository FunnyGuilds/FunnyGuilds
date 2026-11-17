package net.dzikoysk.funnyguilds.listener

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.config.message.MessageService
import net.dzikoysk.funnyguilds.config.message.FunnyMessageDispatcher
import net.dzikoysk.funnyguilds.damage.DamageManager
import net.dzikoysk.funnyguilds.event.FunnyEvent
import net.dzikoysk.funnyguilds.event.rank.CombatPointsChangeEvent
import net.dzikoysk.funnyguilds.event.rank.DeathsChangeEvent
import net.dzikoysk.funnyguilds.event.rank.KillsChangeEvent
import net.dzikoysk.funnyguilds.event.rank.PointsChangeEvent
import net.dzikoysk.funnyguilds.rank.RankSystem
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyServer
import net.dzikoysk.funnyguilds.user.FakeUserProfile
import net.dzikoysk.funnyguilds.user.User
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.damage.DamageSource
import org.bukkit.entity.EnderCrystal
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.RegisteredListener
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.MockedConstruction
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.Mockito.RETURNS_DEFAULTS
import org.mockito.Mockito.RETURNS_SELF
import org.mockito.Mockito.lenient
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockConstruction
import org.mockito.Mockito.`when`
import java.util.Optional
import java.util.UUID
import java.net.InetSocketAddress

class PlayerDeathCrystalTest : FunnyGuildsSpec() {

    private lateinit var damageManager: DamageManager
    private lateinit var listener: PlayerDeath
    private lateinit var deathHandler: RegisteredListener
    private val events = mutableListOf<FunnyEvent>()
    private var cancelPoints = false
    private lateinit var items: MockedConstruction<ItemStack>

    @BeforeEach
    fun prepareCombat() {
        items = mockConstruction(ItemStack::class.java) { item, context ->
            lenient().`when`(item.type).thenReturn(context.arguments()[0] as Material)
        }
        config.rankSystem = RankSystem.Type.STATIC
        config.staticAttackerChange = 20
        config.staticVictimChange = 10
        config.rankFarmingProtect = false
        config.deathMessageReceivers = net.dzikoysk.funnyguilds.config.PluginConfiguration.DeathMessageReceivers.PARTICIPANTS
        damageManager = DamageManager()
        val dispatcher = mock(FunnyMessageDispatcher::class.java, RETURNS_SELF)
        val messages = mock(MessageService::class.java) {
            if (it.method.name == "getMessage") dispatcher else RETURNS_DEFAULTS.answer(it)
        }
        listener = PlayerDeath(config).also {
            it.config = config
            it.userManager = userManager
            it.plugin = funnyGuilds
            it.messageService = messages
            it.funnyServer = mock(FunnyServer::class.java)
        }
        PlayerDeath::class.java.getDeclaredField("damageManager").apply {
            isAccessible = true
            set(listener, damageManager)
        }
        deathHandler = registeredHandler(listener, "onDeath", PlayerDeathEvent::class.java, funnyGuilds)
        val eventManager = Bukkit.getPluginManager()
        lenient().doAnswer { invocation ->
            val event = invocation.arguments[0]
            if (event is FunnyEvent) {
                events.add(event)
                if (cancelPoints && event is PointsChangeEvent) {
                    event.isCancelled = true
                }
            }
            null
        }.`when`(eventManager).callEvent(any())
    }

    @AfterEach
    fun closeItems() {
        items.close()
    }

    @Test
    fun `direct crystal kill works with assists and last attacker fallback disabled`() {
        config.assistEnable = false
        config.considerLastAttackerAsKiller = false
        val (attacker, attackerUser) = player("Detonator")
        val (victim, victimUser) = player("Victim")

        deathHandler.callEvent(death(victim, attacker))

        assertEquals(1, attackerUser.rank.kills)
        assertEquals(1020, attackerUser.rank.points)
        assertEquals(1, victimUser.rank.deaths)
        assertEquals(990, victimUser.rank.points)
        assertEquals(1, events.filterIsInstance<KillsChangeEvent>().size)
        assertEquals(1, events.filterIsInstance<DeathsChangeEvent>().size)
        assertEquals(1, events.filterIsInstance<CombatPointsChangeEvent>().size)
        assertEquals(Material.END_CRYSTAL, items.constructed().single().type)
    }

    @Test
    fun `lethal crystal source takes precedence over a previous vanilla killer`() {
        val (previous, previousUser) = player("Previous")
        val (attacker, attackerUser) = player("Detonator")
        val (victim, _) = player("Victim")
        lenient().`when`(victim.killer).thenReturn(previous)

        deathHandler.callEvent(death(victim, attacker))

        assertEquals(1, attackerUser.rank.kills)
        assertEquals(0, previousUser.rank.kills)
    }

    @Test
    fun `placing a crystal does not grant an assist`() {
        val (_, placerUser) = player("Placer")
        val (attacker, attackerUser) = player("Detonator")
        val (victim, _) = player("Victim")

        deathHandler.callEvent(death(victim, attacker))

        assertEquals(1, attackerUser.rank.kills)
        assertEquals(0, placerUser.rank.assists)
        assertEquals(1000, placerUser.rank.points)
    }

    @Test
    fun `placing player can assist only through previous damage`() {
        val (_, placerUser) = player("Placer")
        val (attacker, attackerUser) = player("Detonator")
        val (victim, victimUser) = player("Victim")
        damageManager.getDamageState(victimUser.uuid).addDamage(placerUser, 8.0)
        damageManager.getDamageState(victimUser.uuid).addDamage(attackerUser, 8.0)

        deathHandler.callEvent(death(victim, attacker))

        assertEquals(1, placerUser.rank.assists)
        assertEquals(1005, placerUser.rank.points)
        assertEquals(1, attackerUser.rank.kills)
    }

    @Test
    fun `one crystal can credit the detonator for multiple victims`() {
        val (attacker, attackerUser) = player("Detonator")
        val (first, _) = player("First")
        val (second, _) = player("Second")
        val crystal = mock(EnderCrystal::class.java)
        deathHandler.callEvent(death(first, attacker, crystal))
        deathHandler.callEvent(death(second, attacker, crystal))
        assertEquals(2, attackerUser.rank.kills)
        assertEquals(1040, attackerUser.rank.points)
    }

    @Test
    fun `cancelled death does not change any ranks`() {
        val (attacker, attackerUser) = player("Detonator")
        val (victim, victimUser) = player("Victim")
        val event = death(victim, attacker)
        `when`(event.isCancelled).thenReturn(true)
        deathHandler.callEvent(event)
        assertEquals(1000, attackerUser.rank.points)
        assertEquals(0, victimUser.rank.deaths)
        assertTrue(events.isEmpty())
    }

    @Test
    fun `suicide does not award a kill`() {
        val (victim, victimUser) = player("Victim")
        deathHandler.callEvent(death(victim, victim))
        assertEquals(0, victimUser.rank.kills)
        assertEquals(1000, victimUser.rank.points)
        assertEquals(1, victimUser.rank.deaths)
    }

    @Test
    fun `unknown or offline detonator does not crash or award a kill`() {
        val unknown = mock(Player::class.java)
        lenient().`when`(unknown.uniqueId).thenReturn(UUID.randomUUID())
        lenient().`when`(unknown.isOnline).thenReturn(true)
        val (victim, victimUser) = player("Victim")
        deathHandler.callEvent(death(victim, unknown))
        val (offline, offlineUser) = player("Offline")
        `when`(offline.isOnline).thenReturn(false)
        deathHandler.callEvent(death(victim, offline))
        assertEquals(0, offlineUser.rank.kills)
        assertEquals(1000, victimUser.rank.points)
        assertEquals(2, victimUser.rank.deaths)
    }

    @Test
    fun `offline crystal detonator is not selected again through vanilla killer`() {
        config.considerLastAttackerAsKiller = false
        val (attacker, attackerUser) = player("Offline")
        val (victim, victimUser) = player("Victim")
        `when`(attacker.isOnline).thenReturn(false)
        lenient().`when`(victim.killer).thenReturn(attacker)

        deathHandler.callEvent(death(victim, attacker))

        assertEquals(0, attackerUser.rank.kills)
        assertEquals(1000, attackerUser.rank.points)
        assertEquals(1, victimUser.rank.deaths)
    }

    @Test
    fun `offline crystal detonator does not fall back to a previous vanilla killer`() {
        config.considerLastAttackerAsKiller = false
        val (previous, previousUser) = player("Previous")
        val (attacker, attackerUser) = player("Offline")
        val (victim, victimUser) = player("Victim")
        `when`(attacker.isOnline).thenReturn(false)
        lenient().`when`(victim.killer).thenReturn(previous)

        deathHandler.callEvent(death(victim, attacker))

        assertEquals(0, attackerUser.rank.kills)
        assertEquals(0, previousUser.rank.kills)
        assertEquals(1, victimUser.rank.deaths)
    }

    @Test
    fun `last attacker fallback keeps its own weapon instead of a crystal`() {
        config.considerLastAttackerAsKiller = true
        val (attacker, attackerUser) = player("Previous")
        val (offline, offlineUser) = player("Offline")
        val (victim, victimUser) = player("Victim")
        `when`(offline.isOnline).thenReturn(false)
        damageManager.getDamageState(victimUser.uuid).addDamage(attackerUser, 4.0)
        `when`(listener.funnyServer.getPlayer(attackerUser.uuid)).thenReturn(Optional.of(attacker))

        deathHandler.callEvent(death(victim, offline))

        assertEquals(1, attackerUser.rank.kills)
        assertEquals(0, offlineUser.rank.kills)
        assertTrue(items.constructed().isEmpty())
    }

    @Test
    fun `cancelled points events preserve the existing kills and deaths contract`() {
        cancelPoints = true
        val (attacker, attackerUser) = player("Detonator")
        val (victim, victimUser) = player("Victim")
        deathHandler.callEvent(death(victim, attacker))
        assertEquals(1000, attackerUser.rank.points)
        assertEquals(1000, victimUser.rank.points)
        assertEquals(1, attackerUser.rank.kills)
        assertEquals(1, victimUser.rank.deaths)
    }

    @Test
    fun `non-crystal death preserves vanilla killer attribution`() {
        val (attacker, attackerUser) = player("Attacker")
        val (victim, _) = player("Victim")
        `when`(victim.killer).thenReturn(attacker)
        val event = mock(PlayerDeathEvent::class.java)
        `when`(event.entity).thenReturn(victim)
        `when`(event.damageSource).thenReturn(mock(DamageSource::class.java))
        deathHandler.callEvent(event)
        assertEquals(1, attackerUser.rank.kills)
    }

    @Test
    fun `last attacker disappearing between online check and lookup is safe`() {
        config.considerLastAttackerAsKiller = true
        val (_, attackerUser) = player("Attacker")
        val (victim, victimUser) = player("Victim")
        damageManager.getDamageState(victimUser.uuid).addDamage(attackerUser, 4.0)
        `when`(listener.funnyServer.getPlayer(attackerUser.uuid)).thenReturn(Optional.empty())
        val event = mock(PlayerDeathEvent::class.java)
        `when`(event.entity).thenReturn(victim)
        `when`(event.damageSource).thenReturn(mock(DamageSource::class.java))
        deathHandler.callEvent(event)
        assertEquals(0, attackerUser.rank.kills)
        assertEquals(1, victimUser.rank.deaths)
        assertTrue(damageManager.getDamageState(victimUser.uuid).lastDamage.isEmpty)
    }

    @Test
    fun `unattributed death clears previous assist damage`() {
        val (_, attackerUser) = player("Attacker")
        val (victim, victimUser) = player("Victim")
        damageManager.getDamageState(victimUser.uuid).addDamage(attackerUser, 4.0)
        val event = mock(PlayerDeathEvent::class.java)
        `when`(event.entity).thenReturn(victim)
        `when`(event.damageSource).thenReturn(mock(DamageSource::class.java))
        deathHandler.callEvent(event)
        assertTrue(damageManager.getDamageState(victimUser.uuid).lastDamage.isEmpty)
        assertEquals(0, attackerUser.rank.kills)
    }

    @Test
    fun `crystal kills still respect anti-farming history`() {
        val (attacker, attackerUser) = player("Detonator")
        val (victim, _) = player("Victim")
        deathHandler.callEvent(death(victim, attacker))
        config.rankFarmingProtect = true
        deathHandler.callEvent(death(victim, attacker))
        assertEquals(1, attackerUser.rank.kills)
        assertEquals(1020, attackerUser.rank.points)
    }

    @Test
    fun `crystal kills still respect same IP protection`() {
        config.rankIPProtect = true
        val (attacker, attackerUser) = player("Detonator")
        val (victim, victimUser) = player("Victim")
        val address = InetSocketAddress("127.0.0.1", 25565)
        `when`(attacker.address).thenReturn(address)
        `when`(victim.address).thenReturn(address)
        deathHandler.callEvent(death(victim, attacker))
        assertEquals(0, attackerUser.rank.kills)
        assertEquals(1000, victimUser.rank.points)
    }

    @Test
    fun `crystal and melee use identical ELO calculations`() = assertSameAlgorithm(RankSystem.Type.ELO)

    @Test
    fun `crystal and melee use identical percentage calculations`() = assertSameAlgorithm(RankSystem.Type.PERCENT)

    @Test
    fun `crystal and melee use identical static calculations`() = assertSameAlgorithm(RankSystem.Type.STATIC)

    private fun assertSameAlgorithm(type: RankSystem.Type) {
        config.rankSystem = type
        config.assistEnable = false
        val configuredListener = PlayerDeath(config).also {
            it.config = config
            it.userManager = userManager
            it.plugin = funnyGuilds
            it.messageService = listener.messageService
            it.funnyServer = listener.funnyServer
        }
        PlayerDeath::class.java.getDeclaredField("damageManager").apply {
            isAccessible = true
            set(configuredListener, damageManager)
        }
        val configuredHandler = registeredHandler(configuredListener, "onDeath", PlayerDeathEvent::class.java, funnyGuilds)
        val (crystalAttacker, crystalUser) = player("Crystal")
        val (crystalVictim, crystalVictimUser) = player("CrystalVictim")
        val (meleeAttacker, meleeUser) = player("Melee")
        val (meleeVictim, meleeVictimUser) = player("MeleeVictim")
        crystalVictimUser.rank.points = 1500
        meleeVictimUser.rank.points = 1500
        configuredHandler.callEvent(death(crystalVictim, crystalAttacker))
        `when`(meleeVictim.killer).thenReturn(meleeAttacker)
        val meleeEvent = mock(PlayerDeathEvent::class.java)
        `when`(meleeEvent.entity).thenReturn(meleeVictim)
        `when`(meleeEvent.damageSource).thenReturn(mock(DamageSource::class.java))
        configuredHandler.callEvent(meleeEvent)
        assertEquals(meleeUser.rank.points, crystalUser.rank.points)
        assertEquals(meleeVictimUser.rank.points, crystalVictimUser.rank.points)
        assertEquals(1, crystalUser.rank.kills)
    }

    private fun player(name: String): Pair<Player, User> {
        val id = UUID.randomUUID()
        val player = mock(Player::class.java, RETURNS_DEEP_STUBS)
        lenient().`when`(player.uniqueId).thenReturn(id)
        lenient().`when`(player.isOnline).thenReturn(true)
        lenient().`when`(player.killer).thenReturn(null)
        return player to userManager.createFake(id, name, FakeUserProfile.online())
    }

    private fun death(victim: Player, attacker: Player, crystal: EnderCrystal = mock(EnderCrystal::class.java)): PlayerDeathEvent {
        val source = mock(DamageSource::class.java)
        lenient().`when`(source.directEntity).thenReturn(crystal)
        lenient().`when`(source.causingEntity).thenReturn(attacker)
        return mock(PlayerDeathEvent::class.java).also {
            lenient().`when`(it.entity).thenReturn(victim)
            lenient().`when`(it.damageSource).thenReturn(source)
        }
    }
}
