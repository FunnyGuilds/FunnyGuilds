package net.dzikoysk.funnyguilds.data.database

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
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

class SQLUserIdentityTest : FunnyGuildsSpec() {

    @Test
    fun `SQL conflict propagates through load before regions guilds or user deserialization`() {
        val first = UUID.randomUUID().toString()
        val second = UUID.randomUUID().toString()
        val (connection, statement) = prepareDatabase(rows(first to "Steve", second to "steve"))
        val model = spy(SQLDataModel(funnyGuilds))

        // Schema migration has separate integration tests; here exercise the startup read path.
        mockStatic(SQLDataModel::class.java).use {
            val error = assertFailsWith<IllegalStateException> { model.load() }
            for (detail in listOf(first, second, config.mysql.usersTableName, "STARTUP ABORTED")) {
                assertContains(error.message!!, detail)
            }
        }

        assertEquals(0, userManager.countUsers())
        verify(model, never()).loadRegions()
        verify(model, never()).loadGuilds()
        verify(statement, never()).executeUpdate()
        verify(statement).close()
        verify(connection, times(2)).close()
        verify(funnyGuilds, never()).funnyServer
    }

    @Test
    fun `valid SQL snapshot loads all users and stats with one select`() {
        val first = UUID.randomUUID()
        val second = UUID.randomUUID()
        val (connection, statement) = prepareDatabase(rows(first.toString() to "Steve", second.toString() to "Alex"))
        val server = mock(FunnyServer::class.java)
        `when`(server.getPlayer(any(UUID::class.java))).thenReturn(Option.none())
        `when`(funnyGuilds.funnyServer).thenReturn(server)

        SQLDataModel(funnyGuilds).loadUsers()

        assertEquals(2, userManager.countUsers())
        assertEquals("Steve", userManager.findByUuid(first).get().name)
        assertEquals("Alex", userManager.findByUuid(second).get().name)
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
            SQLNamedStatement("SELECT * FROM users", emptyMap()).executeQuery(connection) { throw failure }
        }

        assertSame(failure, error)
        verify(statement).close()
        // The strict overload leaves the caller-owned connection open.
        verify(connection, never()).close()
    }

    private fun prepareDatabase(result: CachedRowSet): Pair<Connection, PreparedStatement> {
        val connection = mock(Connection::class.java)
        val statement = mock(PreparedStatement::class.java)
        val database = mock(Database::class.java)
        // Also used by the strict-query test, which intentionally supplies the connection directly.
        org.mockito.Mockito.lenient().`when`(funnyGuilds.database).thenReturn(Option.of(database))
        org.mockito.Mockito.lenient().`when`(database.connection).thenReturn(connection)
        `when`(connection.prepareStatement(anyString())).thenReturn(statement)
        `when`(statement.executeQuery()).thenReturn(result)
        return connection to statement
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
