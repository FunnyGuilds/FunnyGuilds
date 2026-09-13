package net.dzikoysk.funnyguilds.data.database

import net.dzikoysk.funnyguilds.data.database.element.SQLTable
import net.dzikoysk.funnyguilds.data.database.element.SQLType
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.DockerClientFactory
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.containers.MySQLContainer
import java.sql.Connection
import java.sql.DriverManager
import java.util.UUID

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

        Assertions.assertEquals(setOf("uuid", "name", "points"), readColumns(tableName))
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
        Assertions.assertEquals(setOf("uuid", "name"), readColumns(tableName))

        SQLDataModel.migrateSchema(connection, usersTable(tableName))

        val columns = readColumns(tableName)
        Assertions.assertTrue("points" in columns, "expected 'points' column to be added, got $columns")
    }

    private fun usersTable(name: String): SQLTable = SQLTable(name).apply {
        add("uuid", SQLType.VARCHAR, 36, true)
        add("name", SQLType.TEXT, true)
        add("points", SQLType.INT, true)
        setPrimaryKey("uuid")
    }

    private fun readColumns(tableName: String): Set<String> =
        SQLDataModel.fetchExistingColumns(connection, tableName)

    private fun uniqueTableName(): String = "users_${UUID.randomUUID().toString().substring(0, 8)}"

}