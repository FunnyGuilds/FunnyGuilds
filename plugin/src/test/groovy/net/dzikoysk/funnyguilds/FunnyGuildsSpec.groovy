package net.dzikoysk.funnyguilds

import groovy.transform.CompileStatic
import net.dzikoysk.funnyguilds.config.MessageConfiguration
import net.dzikoysk.funnyguilds.config.PluginConfiguration
import net.dzikoysk.funnyguilds.feature.notification.bossbar.provider.BossBarProvider
import net.dzikoysk.funnyguilds.rank.RankManager
import net.dzikoysk.funnyguilds.user.User
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

    @Mock
    public FunnyGuilds funnyGuilds

    protected PluginConfiguration config = new PluginConfiguration()
    protected MessageConfiguration messages = new MessageConfiguration()

    protected RankManager rankManager = new RankManager();

    @BeforeEach
    void prepareFunnyGuilds() {
        lenient().when(funnyGuilds.getPluginConfiguration()).thenReturn(config)
        lenient().when(funnyGuilds.getMessageConfiguration()).thenReturn(messages)
        lenient().when(funnyGuilds.getRankManager()).thenReturn(rankManager)

        MockedStatic<FunnyGuilds> mockedFunnyGuilds = mockStatic(FunnyGuilds.class)
        mockedFunnyGuilds.when({ FunnyGuilds.getInstance() }).thenReturn(funnyGuilds)

        MockedStatic<BossBarProvider> mockedBossBarProvider = mockStatic(BossBarProvider.class)
        mockedBossBarProvider.when(() -> BossBarProvider.getBossBar(any(User.class))).thenReturn(null)
    }

}
