package net.dzikoysk.funnyguilds.data.database

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.TestLogger
import net.dzikoysk.funnyguilds.data.DataModelLoader
import net.dzikoysk.funnyguilds.data.database.element.SQLNamedStatement
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyServer
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.never
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import panda.std.Option
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Types
import java.util.UUID
import javax.sql.rowset.CachedRowSet
import javax.sql.rowset.RowSetMetaDataImpl
import javax.sql.rowset.RowSetProvider
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SQLUserIdentityTest : FunnyGuildsSpec() {

    @Test
    fun `SQL conflict propagates through load before regions guilds or user deserialization`() {
        config.preventUuidConflicts = true
        val first = UUID.randomUUID().toString()
        val second = UUID.randomUUID().toString()
        val (connection, statement, preflight) = prepareDatabase(rows(), conflicts("Steve", first, second))
        val model = spy(SQLDataModel(funnyGuilds))

        // Schema migration has separate integration tests; here exercise the startup read path.
        mockStatic(SQLDataModel::class.java).use {
            val result = DataModelLoader(FunnyGuilds.getPluginLogger()).load { model }
            assertTrue(result.isErr())
            assertContains(result.getError(), "User data validation failed")
            val message = (FunnyGuilds.getPluginLogger() as TestLogger).errors.single()
            for (detail in listOf(first, second, config.mysql.usersTableName, "UUID conflict")) {
                assertContains(message, detail)
            }
        }

        assertEquals(0, userManager.countUsers())
        verify(model, never()).loadRegions()
        verify(model, never()).loadGuilds()
        verify(statement, never()).executeUpdate()
        verify(statement, never()).executeQuery()
        verify(preflight).close()
        verify(connection, times(2)).close()
        verify(funnyGuilds, never()).funnyServer
    }

    @Test
    fun `disabled validation loads users with one select and allows reused names`() {
        assertTrue(!config.preventUuidConflicts)
        val first = UUID.randomUUID()
        val second = UUID.randomUUID()
        val (connection, statement) = prepareDatabase(rows(first.toString() to "Steve", second.toString() to "Steve"))
        val server = mock(FunnyServer::class.java)
        `when`(server.getPlayer(any(UUID::class.java))).thenReturn(Option.none())
        `when`(funnyGuilds.funnyServer).thenReturn(server)

        SQLDataModel(funnyGuilds).loadUsers()

        assertEquals(2, userManager.countUsers())
        assertEquals("Steve", userManager.findByUuid(first).get().name)
        assertEquals("Steve", userManager.findByUuid(second).get().name)
        assertEquals(1234, userManager.findByUuid(first).get().rank.points)
        verify(connection).prepareStatement(anyString())
        verify(statement).executeQuery()
        verify(statement, never()).executeUpdate()
    }

    @Test
    fun `SQL read failure retains its cause and creates no users`() {
        val connection = mock(Connection::class.java)
        val database = mock(Database::class.java)
        `when`(funnyGuilds.database).thenReturn(Option.of(database))
        `when`(database.connection).thenReturn(connection)
        val failure = SQLException("Connection lost")
        `when`(connection.prepareStatement(anyString())).thenThrow(failure)

        val error = assertFailsWith<IllegalStateException> { SQLDataModel(funnyGuilds).loadUsers() }

        assertSame(failure, error.cause)
        assertEquals(0, userManager.countUsers())
        verify(connection).close()
    }

    @Test
    fun `strict query propagates callback errors and closes JDBC resources`() {
        val (connection, statement) = prepareDatabase(rows(UUID.randomUUID().toString() to "Steve"))
        val failure = IllegalStateException("Validation failed")

        val error = assertFailsWith<IllegalStateException> {
            SQLNamedStatement("SELECT * FROM users", emptyMap()).executeQueryOrThrow(connection) { throw failure }
        }

        assertSame(failure, error)
        verify(statement).close()
        // The strict overload leaves the caller-owned connection open.
        verify(connection, never()).close()
    }

    @Test
    fun `enabled preflight uses exact name grouping before loading valid offline mode case variants`() {
        config.preventUuidConflicts = true
        val (connection, statement, preflight) = prepareDatabase(rows(
            UUID.randomUUID().toString() to "Steve", UUID.randomUUID().toString() to "steve"))
        val server = mock(FunnyServer::class.java)
        `when`(server.getPlayer(any(UUID::class.java))).thenReturn(Option.none())
        `when`(funnyGuilds.funnyServer).thenReturn(server)

        SQLDataModel(funnyGuilds).loadUsers()

        assertEquals(2, userManager.countUsers())
        assertEquals("Steve", userManager.findByName("Steve").get().name)
        assertEquals("steve", userManager.findByName("steve").get().name)
        val order = org.mockito.Mockito.inOrder(preflight, statement)
        order.verify(preflight).executeQuery()
        order.verify(statement).executeQuery()
        verify(connection).prepareStatement(org.mockito.ArgumentMatchers.contains("GROUP BY BINARY `name`"))
        verify(connection, times(2)).prepareStatement(anyString())
    }

    private fun prepareDatabase(result: CachedRowSet, conflict: CachedRowSet = conflicts()): Triple<Connection, PreparedStatement, PreparedStatement> {
        val connection = mock(Connection::class.java)
        val statement = mock(PreparedStatement::class.java)
        val preflight = mock(PreparedStatement::class.java)
        val database = mock(Database::class.java)
        // Also used by the strict-query test, which intentionally supplies the connection directly.
        org.mockito.Mockito.lenient().`when`(funnyGuilds.database).thenReturn(Option.of(database))
        org.mockito.Mockito.lenient().`when`(database.connection).thenReturn(connection)
        `when`(connection.prepareStatement(anyString())).thenAnswer {
            if (it.getArgument<String>(0).contains("GROUP BY BINARY")) preflight else statement
        }
        org.mockito.Mockito.lenient().`when`(statement.executeQuery()).thenReturn(result)
        org.mockito.Mockito.lenient().`when`(preflight.executeQuery()).thenReturn(conflict)
        return Triple(connection, statement, preflight)
    }

    private fun conflicts(name: String? = null, first: String = "", second: String = ""): CachedRowSet {
        val metadata = RowSetMetaDataImpl().apply {
            columnCount = 3
            listOf("name", "first_uuid", "second_uuid").forEachIndexed { index, column ->
                setColumnName(index + 1, column)
                setColumnType(index + 1, Types.VARCHAR)
            }
        }
        return RowSetProvider.newFactory().createCachedRowSet().apply {
            setMetaData(metadata)
            if (name != null) {
                moveToInsertRow()
                updateString("name", name)
                updateString("first_uuid", first)
                updateString("second_uuid", second)
                insertRow()
                moveToCurrentRow()
            }
            beforeFirst()
        }
    }

    private fun rows(vararg identities: Pair<String, String>): CachedRowSet {
        val columns = listOf("uuid", "name", "points", "kills", "deaths", "assists", "logouts", "ban", "reason", "last_ip")
        val metadata = RowSetMetaDataImpl().apply {
            columnCount = columns.size
            columns.forEachIndexed { index, name ->
                setColumnName(index + 1, name)
                setColumnType(index + 1, if (name in listOf("uuid", "name", "reason", "last_ip")) Types.VARCHAR else Types.BIGINT)
            }
        }
        return RowSetProvider.newFactory().createCachedRowSet().apply {
            setMetaData(metadata)
            for ((uuid, name) in identities) {
                moveToInsertRow()
                updateString("uuid", uuid)
                updateString("name", name)
                for (column in listOf("points", "kills", "deaths", "assists", "logouts", "ban")) {
                    updateLong(column, if (column == "points") 1234 else 0)
                }
                updateNull("reason")
                updateNull("last_ip")
                insertRow()
                moveToCurrentRow()
            }
            beforeFirst()
        }
    }
}
