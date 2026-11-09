package net.dzikoysk.funnyguilds.data.database

import net.dzikoysk.funnyguilds.data.database.element.SQLTable
import net.dzikoysk.funnyguilds.data.database.element.SQLType
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.Connection
import java.sql.DriverManager
import java.util.UUID

@Disabled("Requires a working Docker environment")
internal class MariaDBSQLDataModelMigrationTest : SQLDataModelMigrationTest() {
    companion object {
        @Container
        @JvmStatic
        val mariadb: MariaDBContainer<*> = MariaDBContainer("mariadb:11.4")
    }
    override val container: JdbcDatabaseContainer<*> get() = mariadb
}

@Disabled("Requires a working Docker environment")
internal class MySQLSQLDataModelMigrationTest : SQLDataModelMigrationTest() {
    companion object {
        @Container
        @JvmStatic
        val mysql: MySQLContainer<Nothing> =
            object : MySQLContainer<Nothing>("mysql:8.0") {
                override fun getDriverClassName(): String = "org.mariadb.jdbc.Driver"
                override fun getJdbcUrl(): String = "jdbc:mariadb://$host:${getMappedPort(MYSQL_PORT)}/$databaseName"
            }.apply {
                withCommand("--default-authentication-plugin=mysql_native_password")
            }
    }
    override val container: JdbcDatabaseContainer<*> get() = mysql
}

@Testcontainers
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

    private fun readColumns(tableName: String): Set<String> =
        SQLDataModel.fetchExistingColumns(connection, tableName)

    private fun uniqueTableName(): String = "users_${UUID.randomUUID().toString().substring(0, 8)}"

}
