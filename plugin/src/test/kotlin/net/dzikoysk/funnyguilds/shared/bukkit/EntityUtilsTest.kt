package net.dzikoysk.funnyguilds.shared.bukkit

import org.bukkit.damage.DamageSource
import org.bukkit.entity.EnderCrystal
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class EntityUtilsTest {

    @Test
    fun `crystal damage resolves the causing player instead of the direct crystal`() {
        val player = mock(Player::class.java)
        val source = source(mock(EnderCrystal::class.java), player)
        assertEquals(player, EntityUtils.getCrystalAttacker(source).orElseThrow())
    }

    @Test
    fun `crystal without a player cause does not invent a detonator`() {
        val source = source(mock(EnderCrystal::class.java), null)
        assertTrue(EntityUtils.getCrystalAttacker(source).isEmpty)
    }

    @Test
    fun `non-player crystal cause is not credited`() {
        val source = source(mock(EnderCrystal::class.java), mock(Entity::class.java))
        assertTrue(EntityUtils.getCrystalAttacker(source).isEmpty)
    }

    @Test
    fun `TNT player attribution is not changed by crystal support`() {
        val source = mock(DamageSource::class.java)
        val tnt = mock(TNTPrimed::class.java)
        `when`(source.directEntity).thenReturn(tnt)
        val event = mock(EntityDamageByEntityEvent::class.java)
        `when`(event.damageSource).thenReturn(source)
        `when`(event.damager).thenReturn(tnt)
        assertTrue(EntityUtils.getDamageAttacker(event).isEmpty)
    }

    @Test
    fun `ordinary melee retains the direct player`() {
        val player = mock(Player::class.java)
        assertEquals(player, EntityUtils.getAttacker(player).orElseThrow())
    }

    @Test
    fun `ordinary projectile retains its shooter`() {
        val player = mock(Player::class.java)
        val projectile = mock(Projectile::class.java)
        `when`(projectile.shooter).thenReturn(player)
        assertEquals(player, EntityUtils.getAttacker(projectile).orElseThrow())
    }

    @Test
    fun `missing direct source is not a crystal kill`() {
        val source = mock(DamageSource::class.java)
        assertTrue(EntityUtils.getCrystalAttacker(source).isEmpty)
    }

    private fun source(direct: Entity, causing: Entity?): DamageSource =
        mock(DamageSource::class.java).also {
            `when`(it.directEntity).thenReturn(direct)
            `when`(it.causingEntity).thenReturn(causing)
        }
}
