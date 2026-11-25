package net.dzikoysk.funnyguilds.feature.base

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.config.MockPluginConfiguration
import net.dzikoysk.funnyguilds.config.PluginConfiguration
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils
import net.dzikoysk.funnyguilds.user.User
import net.dzikoysk.funnyguilds.user.UserManager
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.scheduler.BukkitTask
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InOrder
import org.mockito.MockedStatic
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import java.time.Duration
import java.util.UUID
import kotlin.test.assertFailsWith

class GuildBaseServiceTest {

    private val plugin = mock(FunnyGuilds::class.java)
    private lateinit var mockedFunnyGuilds: MockedStatic<FunnyGuilds>
    private lateinit var itemUtils: MockedStatic<ItemUtils>
    private lateinit var config: PluginConfiguration
    private lateinit var player: Player
    private lateinit var inventory: PlayerInventory
    private lateinit var member: User
    private lateinit var guild: Guild
    private lateinit var service: GuildBaseService

    @BeforeEach
    fun prepare() {
        mockedFunnyGuilds = mockStatic(FunnyGuilds::class.java)
        mockedFunnyGuilds.`when`<FunnyGuilds> { FunnyGuilds.getInstance() }.thenReturn(plugin)
        itemUtils = mockStatic(ItemUtils::class.java)
        itemUtils.`when`<ItemStack> { ItemUtils.parseItem(anyString()) }.thenReturn(null)

        config = MockPluginConfiguration()
        config.regionsEnabled = true
        config.baseEnable = true

        `when`(plugin.pluginConfiguration).thenReturn(config)

        player = mock(Player::class.java)
        inventory = mock(PlayerInventory::class.java)
        `when`(player.inventory).thenReturn(inventory)

        member = UserManager(config).createFake(UUID.randomUUID(), "member")
        guild = Guild("Test guild", "TEST")
        guild.setHome(Location(null, 0.0, 64.0, 0.0))

        service = GuildBaseService(plugin)
    }

    @AfterEach
    fun closeStatics() {
        mockedFunnyGuilds.close()
        itemUtils.close()
    }

    @Test
    fun `failed validation does not close the inventory`() {
        member.cache.setTeleportation(mock(BukkitTask::class.java))

        assertFailsWith<InternalValidationException> { service.teleport(player, member, guild) }

        verify(player, never()).closeInventory()
        verifyNoInteractions(inventory)
    }

    @Test
    fun `missing required items do not close the inventory`() {
        config.baseItems = listOf()
        itemUtils.`when`<Boolean> { ItemUtils.playerHasEnoughItems(any(), any(), any()) }.thenReturn(false)

        service.teleport(player, member, guild)

        verify(player, never()).closeInventory()
        verifyNoInteractions(inventory)
    }

    @Test
    fun `passed validations and item checks close the inventory before charging the cost`() {
        val diamond = mock(ItemStack::class.java)
        `when`(diamond.clone()).thenReturn(diamond)
        config.baseItems = listOf(diamond)
        config.baseDelay = Duration.ZERO
        itemUtils.`when`<Boolean> { ItemUtils.playerHasEnoughItems(any(), any(), any()) }.thenReturn(true)
        itemUtils.`when`<Array<ItemStack>> { ItemUtils.toArray(any()) }.thenAnswer { invocation ->
            (invocation.getArgument(0) as Collection<ItemStack>).toTypedArray()
        }
        `when`(inventory.removeItem(diamond)).thenReturn(hashMapOf())
        `when`(inventory.addItem(diamond)).thenReturn(hashMapOf())

        service.teleport(player, member, guild)

        val order: InOrder = inOrder(player, inventory)
        order.verify(player).closeInventory()
        order.verify(inventory).removeItem(diamond)
        order.verify(inventory).addItem(diamond)
    }

}
