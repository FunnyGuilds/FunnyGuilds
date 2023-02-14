package net.dzikoysk.funnyguilds

import com.dzikoysk.sqiffy.Slf4JSqiffyLogger
import com.dzikoysk.sqiffy.Sqiffy
import com.dzikoysk.sqiffy.createHikariDataSource
import com.dzikoysk.sqiffy.shared.createTestDatabaseFile
import net.dzikoysk.funnyguilds.server.FakeServerContext
import net.dzikoysk.funnyguilds.user.UserModule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.slf4j.LoggerFactory
import kotlin.io.path.absolutePathString

internal abstract class IntegrationTestSpecification {

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
                UserModule()
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