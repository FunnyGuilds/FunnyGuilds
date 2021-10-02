package net.dzikoysk.funnyguilds

import groovy.transform.CompileStatic
import net.dzikoysk.funnyguilds.config.IntegerRange
import net.dzikoysk.funnyguilds.config.MessageConfiguration
import net.dzikoysk.funnyguilds.config.PluginConfiguration
import net.dzikoysk.funnyguilds.feature.notification.bossbar.provider.BossBarProvider
import net.dzikoysk.funnyguilds.guild.GuildManager
import net.dzikoysk.funnyguilds.rank.RankManager
import net.dzikoysk.funnyguilds.user.User
import net.dzikoysk.funnyguilds.user.UserManager
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.junit.jupiter.MockitoExtension

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.lenient
import static org.mockito.Mockito.mockStatic

@CompileStatic
@ExtendWith(MockitoExtension.class)
class FunnyGuildsSpec extends BukkitSpec {

    protected static MockedStatic<FunnyGuilds> mockedFunnyGuilds
    protected static MockedStatic<BossBarProvider> mockedBossBarProvider

    @Mock
    public FunnyGuilds funnyGuilds

    protected PluginConfiguration config = new PluginConfiguration()
    protected MessageConfiguration messages = new MessageConfiguration()

    protected RankManager rankManager
    protected UserManager userManager
    protected GuildManager guildManager

    @BeforeAll
    static void openMockedFunnyGuilds() {
        mockedFunnyGuilds = mockStatic(FunnyGuilds.class)
        mockedBossBarProvider = mockStatic(BossBarProvider.class)
    }

    @BeforeEach
    void prepareFunnyGuilds() {
        lenient().when(funnyGuilds.getPluginConfiguration()).thenReturn(config)
        lenient().when(funnyGuilds.getMessageConfiguration()).thenReturn(messages)

        rankManager = new RankManager(config)
        userManager = new UserManager()
        guildManager = new GuildManager(funnyGuilds);

        lenient().when(funnyGuilds.getRankManager()).thenReturn(rankManager)
        lenient().when(funnyGuilds.getUserManager()).thenReturn(userManager)
        lenient().when(funnyGuilds.getGuildManager()).thenReturn(guildManager)

        mockedFunnyGuilds.when({ FunnyGuilds.getInstance() }).thenReturn(funnyGuilds)
        mockedBossBarProvider.when(() -> BossBarProvider.getBossBar(any(User.class))).thenReturn(null)
    }

    @BeforeEach
    void preparePluginConfiguration() {
        Map<IntegerRange, Integer> parsedData = new HashMap<>()

        IntegerRange.parseIntegerRange(config.eloConstants_, false)
                .forEach((range, number) -> parsedData.put(range, Integer.parseInt(number)))

        config.eloConstants = parsedData
    }

    @AfterAll
    static void closeMockedFunnyGuilds() {
        mockedFunnyGuilds.close()
        mockedBossBarProvider.close()
    }

}
