package net.dzikoysk.funnyguilds.data.database

import net.dzikoysk.funnyguilds.data.database.element.SQLTable
import net.dzikoysk.funnyguilds.data.database.element.SQLType
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.testcontainers.DockerClientFactory
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.containers.MySQLContainer
import java.sql.Connection
import java.sql.DriverManager
import java.util.UUID

@Disabled("Requires a working Docker environment")
internal class MariaDBSQLDataModelMigrationTest : SQLDataModelMigrationTest() {

    companion object {
        private lateinit var mariadb: MariaDBContainer<*>

        @JvmStatic
        @BeforeAll
        fun startContainer() {
            Assumptions.assumeTrue(
                DockerClientFactory.instance().isDockerAvailable,
                "Docker is not available, skipping MariaDB migration test"
            )
            mariadb = MariaDBContainer("mariadb:11.4")
            mariadb.start()
        }

        @JvmStatic
        @AfterAll
        fun stopContainer() {
            if (::mariadb.isInitialized) {
                mariadb.stop()
            }
        }
    }

    override val container: JdbcDatabaseContainer<*> get() = mariadb
}

@Disabled("Requires a working Docker environment")
internal class MySQLSQLDataModelMigrationTest : SQLDataModelMigrationTest() {

    companion object {
        private lateinit var mysql: MySQLContainer<Nothing>

        @JvmStatic
        @BeforeAll
        fun startContainer() {
            Assumptions.assumeTrue(
                DockerClientFactory.instance().isDockerAvailable,
                "Docker is not available, skipping MySQL migration test"
            )
            mysql = object : MySQLContainer<Nothing>("mysql:8.0") {
                override fun getDriverClassName(): String = "org.mariadb.jdbc.Driver"
                override fun getJdbcUrl(): String = "jdbc:mariadb://$host:${getMappedPort(MYSQL_PORT)}/$databaseName"
            }.apply {
                withCommand("--default-authentication-plugin=mysql_native_password")
            }
            mysql.start()
        }

        @JvmStatic
        @AfterAll
        fun stopContainer() {
            if (::mysql.isInitialized) {
                mysql.stop()
            }
        }
    }

    override val container: JdbcDatabaseContainer<*> get() = mysql
}

internal abstract class SQLDataModelMigrationTest {

    protected abstract val container: JdbcDatabaseContainer<*>

    protected lateinit var connection: Connection

    @BeforeEach
    fun openConnection() {
        connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)
    }

    @AfterEach
    fun closeConnection() {
        if (::connection.isInitialized) {
            connection.close()
        }
    }

    @Test
    fun `migration is idempotent and creates expected columns`() {
        val tableName = uniqueTableName()
        val table = usersTable(tableName)

        SQLDataModel.migrateSchema(connection, table)
        SQLDataModel.migrateSchema(connection, table)
        SQLDataModel.migrateSchema(connection, table)

        assertEquals(setOf("uuid", "name", "points"), readColumns(tableName))
    }

    @Test
    fun `adds missing column on subsequent run`() {
        val tableName = uniqueTableName()
        val initial = SQLTable(tableName).apply {
            add("uuid", SQLType.VARCHAR, 36, true)
            add("name", SQLType.TEXT, true)
            setPrimaryKey("uuid")
        }
        SQLDataModel.migrateSchema(connection, initial)
        assertEquals(setOf("uuid", "name"), readColumns(tableName))

        SQLDataModel.migrateSchema(connection, usersTable(tableName))

        val columns = readColumns(tableName)
        assertTrue("points" in columns, "expected 'points' column to be added, got $columns")
    }

    private fun usersTable(name: String): SQLTable = SQLTable(name).apply {
        add("uuid", SQLType.VARCHAR, 36, true)
        add("name", SQLType.TEXT, true)
        add("points", SQLType.INT, true)
        setPrimaryKey("uuid")
    }

    @Test
    fun `UUID preflight distinguishes case variants and detects an exact duplicate username`() {
        val table = usersTable(uniqueTableName())
        SQLDataModel.migrateSchema(connection, table)
        val first = UUID.randomUUID().toString()
        val second = UUID.randomUUID().toString()
        val third = UUID.randomUUID().toString()
        connection.prepareStatement("INSERT INTO ${table.nameGraveAccent} (`uuid`, `name`, `points`) VALUES (?, ?, 0)").use { insert ->
            for ((uuid, name) in listOf(first to "Steve", second to "steve")) {
                insert.setString(1, uuid)
                insert.setString(2, name)
                insert.executeUpdate()
            }
        }
        SQLBasicUtils.getSelectUuidConflict(table).executeQueryOrThrow(connection) {
            assertTrue(!it.next(), "Case variants must be accepted even with a case-insensitive database collation")
        }
        connection.prepareStatement("INSERT INTO ${table.nameGraveAccent} (`uuid`, `name`, `points`) VALUES (?, 'Steve', 0)").use {
            it.setString(1, third)
            it.executeUpdate()
        }
        SQLBasicUtils.getSelectUuidConflict(table).executeQueryOrThrow(connection) {
            assertTrue(it.next())
            assertEquals("Steve", it.getString("name"))
            assertEquals(setOf(first, third), setOf(it.getString("first_uuid"), it.getString("second_uuid")))
            assertTrue(!it.next())
        }
    }

    @Test
    fun `guilds migration adds the tnt protection bypass column`() {
        val tableName = uniqueTableName("guilds")
        val oldGuildsTable = guildsTable(tableName, withTntProtectionBypass = false)

        SQLDataModel.migrateSchema(connection, oldGuildsTable)
        assertTrue(
            "tnt_protection_bypass" !in readColumns(tableName),
            "test setup: old guilds schema should not contain 'tnt_protection_bypass'"
        )

        val guildUuid = UUID.randomUUID().toString()
        connection.prepareStatement(
            "INSERT INTO ${oldGuildsTable.nameGraveAccent} " +
                "(`uuid`, `name`, `tag`, `owner`, `home`, `region`, `regions`, `members`, `points`, `lives`, `heart_lives`, `ban`, `born`, `validity`, `pvp`) " +
                "VALUES (?, 'old-guild', 'OLDG', 'Steve', '', 'old-region', '', '', 0, 0, 0, 0, 0, 0, false)"
        ).use { insert ->
            insert.setString(1, guildUuid)
            insert.executeUpdate()
        }

        SQLDataModel.migrateSchema(connection, guildsTable(tableName))

        val columns = readColumns(tableName)
        assertTrue("tnt_protection_bypass" in columns, "expected 'tnt_protection_bypass' column to be added, got $columns")

        connection.prepareStatement("SELECT `tnt_protection_bypass` FROM ${oldGuildsTable.nameGraveAccent} WHERE `uuid` = ?").use { select ->
            select.setString(1, guildUuid)
            select.executeQuery().use { resultSet ->
                assertTrue(resultSet.next(), "inserted guild row should still exist after migration")
                assertNull(resultSet.getString("tnt_protection_bypass"), "pre-existing guild row should have no TNT protection bypass")
            }
        }
    }

    private fun guildsTable(name: String, withTntProtectionBypass: Boolean = true): SQLTable = SQLTable(name).apply {
        add("uuid", SQLType.VARCHAR, 100, true)
        add("name", SQLType.TEXT, true)
        add("tag", SQLType.TEXT, true)
        add("owner", SQLType.TEXT, true)
        add("home", SQLType.TEXT, true)
        add("region", SQLType.TEXT, true)
        add("regions", SQLType.TEXT, true)
        add("members", SQLType.TEXT, true)
        add("points", SQLType.INT, true)
        add("lives", SQLType.INT, true)
        add("heart_lives", SQLType.INT, true)
        add("ban", SQLType.BIGINT, true)
        add("born", SQLType.BIGINT, true)
        add("validity", SQLType.BIGINT, true)
        add("pvp", SQLType.BOOLEAN, true)
        add("protection", SQLType.BIGINT)
        add("allies", SQLType.TEXT)
        add("enemies", SQLType.TEXT)
        add("info", SQLType.TEXT)
        add("deputy", SQLType.TEXT)
        if (withTntProtectionBypass) {
            add("tnt_protection_bypass", SQLType.BIGINT)
        }
        add("wars_won", SQLType.INT)
        add("wars_lost", SQLType.INT)
        add("last_war", SQLType.TEXT)
        add("war_streak", SQLType.INT)
        add("last_life_buy", SQLType.BIGINT)
        setPrimaryKey("uuid")
    }

    private fun readColumns(tableName: String): Set<String> =
        SQLDataModel.fetchExistingColumns(connection, tableName)

    private fun uniqueTableName(prefix: String = "users"): String = "${prefix}_${UUID.randomUUID().toString().substring(0, 8)}"

}
