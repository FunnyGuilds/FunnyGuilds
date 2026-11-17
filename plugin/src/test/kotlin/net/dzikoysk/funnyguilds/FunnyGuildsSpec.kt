package net.dzikoysk.funnyguilds

import net.dzikoysk.funnyguilds.config.MockPluginConfiguration
import net.dzikoysk.funnyguilds.config.NumberRange
import net.dzikoysk.funnyguilds.config.PluginConfiguration
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration
import net.dzikoysk.funnyguilds.config.message.MessageService
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration
import net.dzikoysk.funnyguilds.guild.GuildManager
import net.dzikoysk.funnyguilds.guild.RegionManager
import net.dzikoysk.funnyguilds.rank.RankService
import net.dzikoysk.funnyguilds.rank.placeholders.RankPlaceholdersService
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils
import net.dzikoysk.funnyguilds.user.UserManager
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.anyString
import org.mockito.Mockito.clearInvocations
import org.mockito.Mockito.lenient
import org.mockito.Mockito.mockStatic
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Locale
import java.util.logging.Logger

@ExtendWith(MockitoExtension::class)
open class FunnyGuildsSpec : BukkitSpec() {

    private lateinit var mockedFunnyGuilds: MockedStatic<FunnyGuilds>
    private lateinit var mockedItemUtils: MockedStatic<ItemUtils>

    @BeforeEach
    fun openMockedFunnyGuilds() {
        mockedFunnyGuilds = mockStatic(FunnyGuilds::class.java)
        mockedItemUtils = mockStatic(ItemUtils::class.java)
    }

    @Mock
    lateinit var funnyGuilds: FunnyGuilds

    private val funnyGuildsLogger = TestLogger(Logger.getLogger("TestLogger"))

    protected lateinit var config: PluginConfiguration
    private lateinit var tablistConfig: TablistConfiguration
    protected lateinit var messageService: MessageService

    protected lateinit var userManager: UserManager
    protected lateinit var guildManager: GuildManager
    protected lateinit var rankService: RankService
    private lateinit var regionManager: RegionManager

    protected lateinit var rankPlaceholdersService: RankPlaceholdersService

    @BeforeEach
    fun prepareFunnyGuilds() {
        mockedFunnyGuilds.`when`<FunnyGuildsLogger> { FunnyGuilds.getPluginLogger() }.thenReturn(funnyGuildsLogger)

        mockedItemUtils.`when`<ItemStack> { ItemUtils.parseItem(anyString()) }.thenReturn(null)

        config = MockPluginConfiguration()
        tablistConfig = TablistConfiguration()
        messageService = MessageService(null)
        messageService.defaultLocale = Locale.forLanguageTag("pl")
        messageService.registerRepository(Locale.forLanguageTag("pl"), MessageConfiguration())

        preparePluginConfiguration()

        lenient().`when`(funnyGuilds.pluginConfiguration).thenReturn(config)
        lenient().`when`(funnyGuilds.tablistConfiguration).thenReturn(tablistConfig)
        lenient().`when`(funnyGuilds.messageService).thenReturn(messageService)
        lenient().`when`(funnyGuilds.name).thenReturn("FunnyGuilds")

        userManager = UserManager(config)
        guildManager = GuildManager(config)
        regionManager = RegionManager(config)

        lenient().`when`(funnyGuilds.userManager).thenReturn(userManager)
        lenient().`when`(funnyGuilds.guildManager).thenReturn(guildManager)
        rankService = RankService.createRankService(funnyGuilds)
        lenient().`when`(funnyGuilds.rankService).thenReturn(rankService)
        lenient().`when`(funnyGuilds.regionManager).thenReturn(regionManager)

        rankPlaceholdersService = RankPlaceholdersService(
            messageService,
            config,
            messageService,
            rankService
        )

        lenient().`when`(funnyGuilds.rankPlaceholdersService).thenReturn(rankPlaceholdersService)
        mockedFunnyGuilds.`when`<FunnyGuilds> { FunnyGuilds.getInstance() }.thenReturn(funnyGuilds)
        // Wiring the mock above records interactions on it; clear them so verify() in a test
        // only sees the interactions made by that test itself.
        clearInvocations(funnyGuilds)
    }

    private fun preparePluginConfiguration() {
        val parsedData = mutableMapOf<NumberRange, Int>()

        NumberRange.parseIntegerRange(config.eloConstants_, false)
            .forEach { (range, number) -> parsedData[range] = number.toInt() }

        config.eloConstants = parsedData
    }

    @AfterEach
    fun closeMockedFunnyGuilds() {
        mockedFunnyGuilds.close()
        mockedItemUtils.close()
    }

}
