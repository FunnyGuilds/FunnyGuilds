package net.dzikoysk.funnyguilds

import com.dzikoysk.sqiffy.Slf4JSqiffyLogger
import com.dzikoysk.sqiffy.Sqiffy
import com.dzikoysk.sqiffy.createHikariDataSource
import com.dzikoysk.sqiffy.shared.createTestDatabaseFile
import net.dzikoysk.funnyguilds.guild.GuildModule
import net.dzikoysk.funnyguilds.server.FakeServerContext
import net.dzikoysk.funnyguilds.user.UserId.Companion.toUserId
import net.dzikoysk.funnyguilds.user.UserModule
import net.dzikoysk.funnyguilds.user.UserService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.slf4j.LoggerFactory
import kotlin.io.path.absolutePathString
import net.dzikoysk.funnyguilds.server.entity.FakePlayer as FakePlayer

internal sealed class FakeFunnyGuildsRunner {

    lateinit var server: FakeServerContext
    lateinit var funnyGuilds: FunnyGuilds

    @BeforeEach
    fun setup() {
        this.server = FakeServerContext()

        this.funnyGuilds = FunnyGuilds(
            sqiffy = Sqiffy(
                dataSource = createHikariDataSource(
                    driver = "org.h2.Driver",
                    url = "jdbc:h2:${createTestDatabaseFile("test-database").absolutePathString()};MODE=MYSQL",
                    threadPool = 1
                ),
                logger = Slf4JSqiffyLogger(LoggerFactory.getLogger(Sqiffy::class.java))
            ),
            modules = listOf(
                UserModule(),
                GuildModule()
            ),
        )

        val serverPlugin = this.funnyGuilds.getServerPlugin()
        serverPlugin.onLoad(server)
        serverPlugin.onEnable(server)
    }

    @AfterEach
    fun cleanup() {
        funnyGuilds.getServerPlugin().onLoad(server)
        funnyGuilds.sqiffy.close()
    }

}

internal abstract class IntegrationTestSpecification : FakeFunnyGuildsRunner() {

    fun createFakePlayerWithUserProfile(name: String? = null): FakePlayer {
        val player = FakePlayer(name ?: "FakePlayer")
        funnyGuilds.getComponent<UserService>().createUser(player.toUserId(), player.name)
        return player
    }

}