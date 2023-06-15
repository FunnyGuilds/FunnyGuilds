package net.dzikoysk.funnyguilds.server.spigot

import com.dzikoysk.sqiffy.Slf4JSqiffyLogger
import com.dzikoysk.sqiffy.Sqiffy
import com.dzikoysk.sqiffy.shared.createHikariDataSource
import com.dzikoysk.sqiffy.shared.createTestDatabaseFile
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.guild.GuildModule
import net.dzikoysk.funnyguilds.user.UserModule
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.LoggerFactory
import kotlin.io.path.absolutePathString

class FunnyGuildsSpigotPlugin : JavaPlugin() {

    private lateinit var funnyGuilds: FunnyGuilds

    override fun onLoad() {
        this.funnyGuilds = FunnyGuilds(
            database = Sqiffy.createDatabase(
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

        this.funnyGuilds.getServerPlugin().onLoad(SpigotServerContext(this, server))
    }

    override fun onEnable() {
        this.funnyGuilds.getServerPlugin().onEnable(SpigotServerContext(this, server))
    }

    override fun onDisable() {
        this.funnyGuilds.getServerPlugin().onDisable(SpigotServerContext(this, server))
    }

}