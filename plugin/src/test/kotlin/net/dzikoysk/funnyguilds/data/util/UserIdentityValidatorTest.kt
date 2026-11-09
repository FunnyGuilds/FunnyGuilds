package net.dzikoysk.funnyguilds.data.util

import org.junit.jupiter.api.Test
import java.util.Locale
import java.util.UUID
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserIdentityValidatorTest {

    @Test
    fun `rejects exact name collisions with both sources`() {
        val validator = UserIdentityValidator()
        val first = UUID.randomUUID().toString()
        val second = UUID.randomUUID().toString()
        validator.accept(first, "Steve", "first.yml")

        val result = validator.accept(second, "Steve", "second.yml")
        assertTrue(result.isErr())
        for (detail in listOf(first, second, "first.yml", "second.yml", "UUID conflict")) {
            assertContains(result.getError(), detail)
        }
        assertFalse(result.getError().contains('\n'))
    }

    @Test
    fun `accepts identical records and unrelated identities`() {
        val validator = UserIdentityValidator()
        val uuid = UUID.randomUUID().toString()
        assertTrue(validator.accept(uuid, "Steve", "first").isOk())
        assertTrue(validator.accept(uuid, "Steve", "duplicate").isOk())
        assertTrue(validator.accept(UUID.randomUUID().toString(), "Alex", "other").isOk())
    }

    @Test
    fun `case variants remain distinct regardless of server locale`() {
        val original = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("tr"))
            val validator = UserIdentityValidator()
            validator.accept(UUID.randomUUID().toString(), "IPlayer", "first")
            assertTrue(validator.accept(UUID.randomUUID().toString(), "iplayer", "second").isOk())
        } finally {
            Locale.setDefault(original)
        }
    }

    @Test
    fun `invalid UUID and missing names are data errors rather than UUID conflicts`() {
        for ((uuid, name) in listOf(null to "Steve", "not-a-uuid" to "Steve", "1-1-1-1-1" to "Steve",
                UUID.randomUUID().toString() to null, UUID.randomUUID().toString() to " ")) {
            val result = UserIdentityValidator().accept(uuid, name, "source.yml")
            assertTrue(result.isErr())
            assertContains(result.getError(), "source.yml")
            assertFalse(result.getError().contains("UUID conflict"))
        }
    }

    @Test
    fun `different stored names for one UUID are not a multiple UUID conflict`() {
        val validator = UserIdentityValidator()
        val uuid = UUID.randomUUID().toString()
        validator.accept(uuid, "Steve", "first")
        val result = validator.accept(uuid, "Alex", "second")
        assertTrue(result.isOk())
    }
}
