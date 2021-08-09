package net.dzikoysk.funnyguilds

import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration
import net.dzikoysk.funnyguilds.element.notification.bossbar.provider.BossBarProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.junit.jupiter.MockitoExtension

import static org.mockito.Mockito.lenient
import static org.mockito.Mockito.mockStatic

@ExtendWith(MockitoExtension.class)
class FunnyGuildsSpec extends BukkitSpec {

    @Mock
    public FunnyGuilds funnyGuilds

    protected def config = new PluginConfiguration()
    protected def messages = new MessageConfiguration()

    @BeforeEach
    void prepareFunnyGuilds() {
        lenient().when(funnyGuilds.getPluginConfiguration()).thenReturn(config)
        lenient().when(funnyGuilds.getMessageConfiguration()).thenReturn(messages)

        MockedStatic<FunnyGuilds> mockedFunnyGuilds = mockStatic(FunnyGuilds.class)
        mockedFunnyGuilds.when(FunnyGuilds::getInstance).thenReturn(funnyGuilds)

        MockedStatic<BossBarProvider> mockedBossBarProvider = mockStatic(BossBarProvider.class)
        mockedBossBarProvider.when(BossBarProvider::getBossBar).thenReturn(null)
    }

}
