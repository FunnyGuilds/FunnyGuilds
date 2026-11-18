package net.dzikoysk.funnyguilds.listener

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.damage.DamageManager
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.user.User
import org.bukkit.damage.DamageSource
import org.bukkit.entity.EnderCrystal
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.plugin.RegisteredListener
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.Mockito.lenient
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.UUID

class EntityDamageCrystalTest : FunnyGuildsSpec() {

    private lateinit var damageManager: DamageManager
    private lateinit var listener: EntityDamage
    private lateinit var damageHandler: RegisteredListener
    private lateinit var damageMonitor: RegisteredListener
    private lateinit var attacker: Player
    private lateinit var victim: Player
    private lateinit var attackerUser: User
    private lateinit var victimUser: User

    @BeforeEach
    fun prepareDamage() {
        attacker = player()
        victim = player()
        attackerUser = userManager.createFake(attacker.uniqueId, "Detonator")
        victimUser = userManager.createFake(victim.uniqueId, "Victim")
        damageManager = DamageManager()
        listener = EntityDamage().also {
            it.config = config
            it.userManager = userManager
        }
        EntityDamage::class.java.getDeclaredField("damageManager").apply {
            isAccessible = true
            set(listener, damageManager)
        }
        damageHandler = registeredHandler(listener, "onDamage", EntityDamageByEntityEvent::class.java, funnyGuilds)
        damageMonitor = registeredHandler(listener, "onDamageMonitor", EntityDamageByEntityEvent::class.java, funnyGuilds)
    }

    @Test
    fun `crystal explosion records the detonator's damage`() {
        hit(event())
        assertEquals(8.0, history().getTotalDamage(attackerUser))
    }

    @Test
    fun `damage is recorded only once after protection listeners`() {
        val event = event()
        damageHandler.callEvent(event)
        assertEquals(0.0, history().totalDamage)
        damageMonitor.callEvent(event)
        assertEquals(8.0, history().totalDamage)
    }

    @Test
    fun `HIGHEST cancellation does not add crystal damage`() {
        val event = event()
        val protection = RegisteredListener(
            object : Listener {},
            { _, dispatchedEvent -> (dispatchedEvent as EntityDamageByEntityEvent).isCancelled = true },
            EventPriority.HIGHEST,
            funnyGuilds,
            true
        )
        listOf(damageHandler, damageMonitor, protection)
            .sortedBy { it.priority }
            .forEach { it.callEvent(event) }

        assertEquals(0.0, history().totalDamage)
    }

    @Test
    fun `already cancelled damage skips both handlers`() {
        val event = event()
        event.isCancelled = true

        hit(event)

        verify(event, never()).damageSource
        assertEquals(0.0, history().totalDamage)
    }

    @Test
    fun `crystal friendly fire is cancelled and not registered`() {
        val guild = guildManager.addGuild(Guild("Guild", "TAG"))
        guild.setOwner(attackerUser)
        guild.addMember(victimUser)
        attackerUser.setGuild(guild)
        victimUser.setGuild(guild)
        guild.setPvP(false)
        val event = event()
        hit(event)
        assertTrue(event.isCancelled)
        assertEquals(0.0, history().totalDamage)
    }

    @Test
    fun `crystal damage between allies respects their PvP settings`() {
        val first = guildManager.addGuild(Guild("First", "ONE"))
        val second = guildManager.addGuild(Guild("Second", "TWO"))
        first.setOwner(attackerUser)
        second.setOwner(victimUser)
        attackerUser.setGuild(first)
        victimUser.setGuild(second)
        first.addAlly(second)
        second.addAlly(first)
        config.damageAlly = false
        val event = event()
        hit(event)
        assertTrue(event.isCancelled)
        assertEquals(0.0, history().totalDamage)
    }

    @Test
    fun `suicide does not create an assist history`() {
        hit(event(causing = victim))
        assertEquals(0.0, history().totalDamage)
    }

    @Test
    fun `disabled assists do not record damage`() {
        config.assistEnable = false
        hit(event())
        assertEquals(0.0, history().totalDamage)
    }

    @Test
    fun `unregistered detonator does not create a user or damage history`() {
        hit(event(causing = player()))
        assertEquals(2, userManager.countUsers())
        assertEquals(0.0, history().totalDamage)
    }

    @Test
    fun `ordinary melee still records raw damage`() {
        hit(event(direct = attacker))
        assertEquals(8.0, history().getTotalDamage(attackerUser))
    }

    @Test
    fun `ordinary projectile still records its shooter's damage`() {
        val arrow = mock(Projectile::class.java)
        `when`(arrow.shooter).thenReturn(attacker)
        hit(event(direct = arrow))
        assertEquals(8.0, history().getTotalDamage(attackerUser))
    }

    private fun history() = damageManager.getDamageState(victimUser.uuid)

    private fun hit(event: EntityDamageByEntityEvent) {
        damageHandler.callEvent(event)
        damageMonitor.callEvent(event)
    }

    private fun player(): Player = mock(Player::class.java).also {
        lenient().`when`(it.uniqueId).thenReturn(UUID.randomUUID())
    }

    private fun event(direct: Entity = mock(EnderCrystal::class.java), causing: Entity? = attacker): EntityDamageByEntityEvent {
        val source = mock(DamageSource::class.java)
        lenient().`when`(source.directEntity).thenReturn(direct)
        lenient().`when`(source.causingEntity).thenReturn(causing)
        val event = mock(EntityDamageByEntityEvent::class.java)
        var cancelled = false
        lenient().`when`(event.damager).thenReturn(direct)
        lenient().`when`(event.entity).thenReturn(victim)
        lenient().`when`(event.damageSource).thenReturn(source)
        lenient().`when`(event.damage).thenReturn(8.0)
        lenient().`when`(event.isCancelled).thenAnswer { cancelled }
        lenient().doAnswer { cancelled = it.arguments[0] as Boolean; null }.`when`(event).setCancelled(anyBoolean())
        return event
    }
}
