package net.dzikoysk.funnyguilds.feature.purchase

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.config.sections.GuildPurchaseConfiguration.Mode
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.guild.GuildExtendValidityEvent
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.permission.GuildPermissionChecker
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GuildPurchaseServiceTest : FunnyGuildsSpec() {

    @Test
    fun `empty legacy option exposes seven sorted periods and explicit choices still work`() {
        val purchases = GuildPurchaseService(funnyGuilds)
        config.validityTime = Duration.ofDays(14)
        config.validityPeriods = listOf()
        assertEquals(listOf(Duration.ofHours(12)) + listOf(1L, 3L, 5L, 7L, 14L, 31L).map(Duration::ofDays),
            purchases.periods())

        config.validityPeriods = listOf("2d")
        assertEquals(listOf(Duration.ofDays(2), Duration.ofDays(14)), purchases.periods())
    }

    @Test
    fun `validity prices scale by period and round up to the provider precision`() {
        val price = PurchasePrice.scale(listOf(), 1000.0, Duration.ofDays(3), Duration.ofDays(1), 2)
        val hourly = PurchasePrice.scale(listOf(), 10.0, Duration.ofHours(1), Duration.ofDays(1), 2)

        assertEquals(BigDecimal("3000.00"), price.money)
        assertEquals(BigDecimal("0.42"), hourly.money)
    }

    @Test
    fun `rejected Vault withdrawal restores the reserved items and keeps the expiry`() {
        val player = mock(Player::class.java)
        val inventory = mock(PlayerInventory::class.java)
        val diamonds = mock(ItemStack::class.java)
        val checker = mock(GuildPermissionChecker::class.java)
        val actor = userManager.createFake(UUID.randomUUID(), "buyer")
        val guild = Guild("Test guild", "TEST")
        guildManager.addGuild(guild)
        actor.setGuild(guild)
        val previous = Instant.now().plus(Duration.ofDays(1))
        guild.setValidity(previous)

        config.validityWhen = Duration.ZERO
        config.validityPayment.mode = Mode.BOTH
        config.validityPayment.money = 100.0
        config.validityItems = listOf(diamonds)
        `when`(diamonds.clone()).thenReturn(diamonds)
        `when`(diamonds.amount).thenReturn(8)
        `when`(diamonds.isSimilar(diamonds)).thenReturn(true)
        `when`(player.isOnline).thenReturn(true)
        `when`(player.hasPermission("funnyguilds.validity")).thenReturn(true)
        `when`(player.inventory).thenReturn(inventory)
        `when`(inventory.storageContents).thenReturn(arrayOf(diamonds))
        `when`(inventory.removeItem(diamonds)).thenReturn(hashMapOf())
        `when`(inventory.addItem(diamonds)).thenReturn(hashMapOf())
        `when`(funnyGuilds.guildPermissionChecker).thenReturn(checker)
        `when`(checker.getPermissionValue(guild, actor, GuildCommandPermission.EXTEND_VALIDITY)).thenReturn(Optional.of(true))

        mockStatic(VaultHook::class.java).use { vault ->
            vault.`when`<Boolean> { VaultHook.isEconomyHooked() }.thenReturn(true)
            vault.`when`<Int> { VaultHook.moneyScale() }.thenReturn(2)
            vault.`when`<Boolean> { VaultHook.canAfford(player, 100.0) }.thenReturn(true)
            vault.`when`<EconomyResponse> { VaultHook.withdrawFromPlayerBank(player, 100.0) }
                .thenReturn(EconomyResponse(0.0, 100.0, EconomyResponse.ResponseType.FAILURE, "Rejected"))
            mockStatic(SimpleEventHandler::class.java).use { events ->
                events.`when`<Boolean> { SimpleEventHandler.handle(any(GuildExtendValidityEvent::class.java)) }.thenReturn(true)
                val purchases = GuildPurchaseService(funnyGuilds)
                val quote = purchases.quoteRenewal(guild, config.validityTime)

                assertFailsWith<InternalValidationException> { purchases.renew(player, actor, guild, quote) }
            }
        }

        verify(inventory).removeItem(diamonds)
        verify(inventory).addItem(diamonds)
        assertEquals(previous, guild.validity)
    }
}
