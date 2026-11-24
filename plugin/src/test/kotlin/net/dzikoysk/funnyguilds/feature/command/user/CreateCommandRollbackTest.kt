package net.dzikoysk.funnyguilds.feature.command.user

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.FunnyGuildsLogger
import net.dzikoysk.funnyguilds.config.MockPluginConfiguration
import net.dzikoysk.funnyguilds.config.message.FunnyMessageDispatcher
import net.dzikoysk.funnyguilds.config.message.MessageService
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet
import net.dzikoysk.funnyguilds.config.sections.items.ItemsConfiguration
import net.dzikoysk.funnyguilds.data.tasks.DatabaseDeleteGuildAsyncTask
import net.dzikoysk.funnyguilds.data.tasks.DatabaseUpdateGuildAsyncTask
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException
import net.dzikoysk.funnyguilds.feature.hooks.HookManager
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook
import net.dzikoysk.funnyguilds.feature.items.GuildItemRequirementChecker
import net.dzikoysk.funnyguilds.feature.items.GuildItemSetService
import net.dzikoysk.funnyguilds.feature.items.ItemRequirementResult
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.GuildManager
import net.dzikoysk.funnyguilds.guild.RegionManager
import net.dzikoysk.funnyguilds.shared.FunnyTask
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils
import net.dzikoysk.funnyguilds.user.User
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldBorder
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFactory
import org.bukkit.inventory.PlayerInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.PluginManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyDouble
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InOrder
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.RETURNS_SELF
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CreateCommandRollbackTest {

    private lateinit var mockedBukkit: MockedStatic<Bukkit>
    private lateinit var mockedFunnyGuilds: MockedStatic<FunnyGuilds>
    private lateinit var mockedVaultHook: MockedStatic<VaultHook>
    private lateinit var mockedItemUtils: MockedStatic<ItemUtils>

    private lateinit var command: CreateCommand
    private lateinit var config: MockPluginConfiguration
    private lateinit var guildManager: GuildManager
    private lateinit var player: Player
    private lateinit var user: User
    private lateinit var scheduledTasks: MutableList<FunnyTask>

    @BeforeEach
    fun prepare() {
        mockedBukkit = mockStatic(Bukkit::class.java)
        mockedBukkit.`when`<ItemFactory> { Bukkit.getItemFactory() }.thenReturn(mock(ItemFactory::class.java))
        mockedBukkit.`when`<PluginManager> { Bukkit.getPluginManager() }.thenReturn(mock(PluginManager::class.java))

        mockedItemUtils = mockStatic(ItemUtils::class.java)
        mockedItemUtils.`when`<ItemStack?> { ItemUtils.parseItem(anyString()) }.thenReturn(null)

        val plugin = mock(FunnyGuilds::class.java)
        val logger = mock(FunnyGuildsLogger::class.java)
        mockedFunnyGuilds = mockStatic(FunnyGuilds::class.java)
        mockedFunnyGuilds.`when`<FunnyGuilds> { FunnyGuilds.getInstance() }.thenReturn(plugin)
        mockedFunnyGuilds.`when`<FunnyGuildsLogger> { FunnyGuilds.getPluginLogger() }.thenReturn(logger)

        mockedVaultHook = mockStatic(VaultHook::class.java)
        mockedVaultHook.`when`<Boolean> { VaultHook.isEconomyHooked() }.thenReturn(true)
        mockedVaultHook.`when`<EconomyResponse> { VaultHook.withdrawFromPlayerBank(any(), anyDouble()) }
            .thenReturn(EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.SUCCESS, null))
        mockedVaultHook.`when`<EconomyResponse> { VaultHook.depositToPlayerBank(any(), anyDouble()) }
            .thenReturn(EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.SUCCESS, null))

        config = MockPluginConfiguration()
        mockedFunnyGuilds.`when`<FunnyGuilds> { FunnyGuilds.getInstance() }.thenReturn(plugin)
        `when`(plugin.pluginConfiguration).thenReturn(config)
        config.blockedWorlds = emptyList()
        config.regionsEnabled = false
        config.giveRewardsForFirstGuild = false

        guildManager = GuildManager(config)

        val world = mock(World::class.java)
        val inventory = mock(org.bukkit.inventory.PlayerInventory::class.java)
        player = mock(Player::class.java)
        user = mock(User::class.java)
        val spawnLocation = org.bukkit.Location(world, 0.0, 64.0, 0.0)
        val playerLocation = org.bukkit.Location(world, 200.0, 64.0, 200.0)

        `when`(world.minHeight).thenReturn(-64)
        `when`(world.maxHeight).thenReturn(320)
        `when`(world.spawnLocation).thenReturn(spawnLocation)
        `when`(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(mock(org.bukkit.block.Block::class.java))
        val anyBlock = mock(org.bukkit.block.Block::class.java)
        val blockLocation = org.bukkit.Location(world, 200.0, 64.0, 200.0)
        `when`(anyBlock.location).thenReturn(blockLocation)
        `when`(anyBlock.state).thenReturn(mock(org.bukkit.block.BlockState::class.java))
        `when`(world.getBlockAt(any(org.bukkit.Location::class.java))).thenReturn(anyBlock)
        val border = mock(WorldBorder::class.java)
        `when`(world.worldBorder).thenReturn(border)
        `when`(border.size).thenReturn(1_000_000.0)
        `when`(border.center).thenReturn(spawnLocation)

        `when`(player.world).thenReturn(world)
        `when`(player.location).thenReturn(playerLocation)
        `when`(player.inventory).thenReturn(inventory)
        `when`(inventory.storageContents).thenReturn(arrayOfNulls<ItemStack>(36))
        `when`(player.level).thenReturn(10)
        `when`(player.hasPermission(anyString())).thenReturn(false)

        `when`(user.hasGuild()).thenReturn(false)

        scheduledTasks = mutableListOf()
        doAnswer(addTaskTo(scheduledTasks)).`when`(plugin).scheduleFunnyTasks(any(FunnyTask::class.java))

        val activeSet = GuildItemSet()
        activeSet.requirements.itemsEnabled = false
        activeSet.requirements.rankEnabled = false
        activeSet.requirements.levelEnabled = true
        activeSet.requiredLevel = 5
        activeSet.requirements.moneyEnabled = true
        activeSet.requiredMoney = 100.0

        val itemsConfiguration = ItemsConfiguration()
        val guildItemSetService = mock(GuildItemSetService::class.java)
        `when`(guildItemSetService.getSetForPlayer(any(), any())).thenReturn(activeSet)

        val guildItemRequirementChecker = mock(GuildItemRequirementChecker::class.java)
        `when`(guildItemRequirementChecker.check(any(), any(), any(), any()))
            .thenReturn(ItemRequirementResult(true, true, true, emptyMap()))

        val dispatcher = mock(FunnyMessageDispatcher::class.java, Mockito.withSettings().defaultAnswer(RETURNS_SELF))
        val messageService = mock(MessageService::class.java)
        `when`(messageService.getMessage(any())).thenReturn(dispatcher)

        command = CreateCommand()
        command.plugin = plugin
        command.logger = logger
        command.config = config
        command.itemsConfiguration = itemsConfiguration
        command.guildItemSetService = guildItemSetService
        command.guildItemRequirementChecker = guildItemRequirementChecker
        command.guildManager = guildManager
        command.regionManager = RegionManager(config)
        command.messageService = messageService
    }

    @AfterEach
    fun cleanUp() {
        HookManager.WORLD_EDIT = Optional.empty()
        mockedItemUtils.close()
        mockedVaultHook.close()
        mockedFunnyGuilds.close()
        mockedBukkit.close()
    }

    @Test
    fun `a failure after successful withdrawal refunds money, restores the level and removes the guild`() {
        doThrow(IllegalStateException("boom")).`when`(user).setGuild(any(Guild::class.java))

        val exception = assertFailsWith<IllegalStateException> { command.execute(player, user, arrayOf("tag", "name")) }
        assertEquals("boom", exception.message)

        verify(player, times(1)).level = 5
        val order: InOrder = inOrder(player)
        order.verify(player).level = 5
        order.verify(player).level = 10

        mockedVaultHook.verify { VaultHook.depositToPlayerBank(player, 100.0) }
        verify(user).removeGuild()
        assertTrue(guildManager.getGuilds().isEmpty())
        assertEquals(0, scheduledTasks.filterIsInstance<DatabaseUpdateGuildAsyncTask>().size)
        assertEquals(1, scheduledTasks.filterIsInstance<DatabaseDeleteGuildAsyncTask>().size)
    }

    @Test
    fun `a successful creation is charged once and schedules no compensation`() {
        command.execute(player, user, arrayOf("tag", "name"))

        mockedVaultHook.verify { VaultHook.withdrawFromPlayerBank(player, 100.0) }
        mockedVaultHook.verify(
            MockedStatic.Verification { VaultHook.depositToPlayerBank(any(), anyDouble()) },
            Mockito.times(0)
        )

        verify(user).setGuild(any(Guild::class.java))
        verify(user, never()).removeGuild()
        assertEquals(1, guildManager.getGuilds().size)
        assertEquals(1, scheduledTasks.filterIsInstance<DatabaseUpdateGuildAsyncTask>().size)
        assertEquals(0, scheduledTasks.filterIsInstance<DatabaseDeleteGuildAsyncTask>().size)
        verify(player, times(1)).level = 5
    }

    @Test
    fun `schematic creation without a WorldEdit hook aborts and rolls back`() {
        config.regionsEnabled = true
        config.heart.guildSchematicFileName = "guild.schem"
        config.heart.guildSchematicFile = java.io.File.createTempFile("guild", ".schem")
        config.heart.pasteSchematicOnCreation = true
        HookManager.WORLD_EDIT = Optional.empty()

        assertFailsWith<InternalValidationException> { command.execute(player, user, arrayOf("tag", "name")) }

        mockedVaultHook.verify { VaultHook.depositToPlayerBank(player, 100.0) }
        verify(player, times(1)).level = 10
        verify(user).removeGuild()
        assertTrue(guildManager.getGuilds().isEmpty())
        assertEquals(0, scheduledTasks.filterIsInstance<DatabaseUpdateGuildAsyncTask>().size)
        assertEquals(1, scheduledTasks.filterIsInstance<DatabaseDeleteGuildAsyncTask>().size)
    }

    @Test
    fun `a failed schematic paste aborts and rolls back`() {
        config.regionsEnabled = true
        config.heart.guildSchematicFileName = "guild.schem"
        config.heart.guildSchematicFile = java.io.File.createTempFile("guild", ".schem")
        config.heart.pasteSchematicOnCreation = true
        val worldEditHook = mock(net.dzikoysk.funnyguilds.feature.hooks.worldedit.WorldEditHook::class.java)
        `when`(worldEditHook.pasteSchematic(any(), any(), any(Boolean::class.java), any())).thenReturn(false)
        HookManager.WORLD_EDIT = Optional.of(worldEditHook)

        assertFailsWith<InternalValidationException> { command.execute(player, user, arrayOf("tag", "name")) }

        mockedVaultHook.verify { VaultHook.depositToPlayerBank(player, 100.0) }
        verify(player, times(1)).level = 10
        verify(user).removeGuild()
        assertTrue(guildManager.getGuilds().isEmpty())
        assertEquals(0, scheduledTasks.filterIsInstance<DatabaseUpdateGuildAsyncTask>().size)
        assertEquals(1, scheduledTasks.filterIsInstance<DatabaseDeleteGuildAsyncTask>().size)
    }

    private fun addTaskTo(tasks: MutableList<FunnyTask>) = Answer<Void> { invocation: InvocationOnMock ->
        tasks.add(invocation.getArgument(0))
        null
    }

}
