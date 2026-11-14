package net.dzikoysk.funnyguilds.data

import net.dzikoysk.funnyguilds.FunnyGuildsLogger
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoExtension
import java.sql.SQLException
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class DataModelLoaderTest {

    @Mock
    lateinit var logger: FunnyGuildsLogger

    @Mock
    lateinit var model: DataModel

    @Test
    fun `successful load returns the same model without saving it`() {
        val result = DataModelLoader(logger).load { model }

        assertTrue(result.isOk())
        assertSame(model, result.get())
        verify(model).load()
        verifyNoMoreInteractions(model)
        verifyNoInteractions(logger)
    }

    @Test
    fun `validation failure returns the shutdown reason without duplicate logs or saving partial data`() {
        val failure = IllegalStateException("User data validation failed")
        doThrow(failure).`when`(model).load()

        val result = DataModelLoader(logger).load { model }

        assertTrue(result.isErr())
        assertEquals(failure.message, result.getError())
        verify(model).load()
        verifyNoMoreInteractions(model)
        verifyNoInteractions(logger)
    }

    @Test
    fun `validation failure while creating the model also prevents startup`() {
        val failure = IllegalStateException("Invalid user data directory")

        val result = DataModelLoader(logger).load { throw failure }

        assertTrue(result.isErr())
        assertEquals(failure.message, result.getError())
        verifyNoInteractions(model, logger)
    }

    @Test
    fun `SQL failure is logged once and returns a failure instead of saving partial data`() {
        val failure = SQLException("Connection lost")
        doThrow(failure).`when`(model).load()

        val result = DataModelLoader(logger).load { model }

        assertTrue(result.isErr())
        assertEquals("Critical error has been encountered!", result.getError())
        verify(logger).error("Could not load data from database", failure)
        verifyNoMoreInteractions(logger)
        verify(model).load()
        verifyNoMoreInteractions(model)
    }

    @Test
    fun `wrapped read failure retains its cause in the diagnostic log`() {
        val failure = IllegalStateException("Could not read user identities", SQLException("Connection lost"))
        doThrow(failure).`when`(model).load()

        val result = DataModelLoader(logger).load { model }

        assertTrue(result.isErr())
        assertEquals("Critical error has been encountered!", result.getError())
        verify(logger).error("Could not load data", failure)
        verifyNoMoreInteractions(logger)
        verify(model).load()
        verifyNoMoreInteractions(model)
    }

    @Test
    fun `unexpected factory failure is logged and no model is loaded`() {
        val failure = IllegalArgumentException("Invalid model configuration")

        val result = DataModelLoader(logger).load { throw failure }

        assertTrue(result.isErr())
        assertEquals("Critical error has been encountered!", result.getError())
        verify(logger).error("Could not load data from database", failure)
        verifyNoMoreInteractions(logger)
        verifyNoInteractions(model)
    }
}
