package net.dzikoysk.funnyguilds.data.util

import org.junit.jupiter.api.Test
import java.util.Locale
import java.util.UUID
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserIdentityValidatorTest {

    @Test
    fun `rejects exact and case insensitive collisions with both sources`() {
        for (name in listOf("Steve", "steve")) {
            val validator = UserIdentityValidator()
            val first = UUID.randomUUID().toString()
            val second = UUID.randomUUID().toString()
            validator.accept(first, "Steve", "first.yml")

            val result = validator.accept(second, name, "second.yml")
            assertTrue(result.isErr())
            for (detail in listOf(first, second, "first.yml", "second.yml", "UUID conflict")) {
                assertContains(result.getError(), detail)
            }
            assertFalse(result.getError().contains('\n'))
        }
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
    fun `normalization does not depend on server locale`() {
        val original = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("tr"))
            val validator = UserIdentityValidator()
            validator.accept(UUID.randomUUID().toString(), "IPlayer", "first")
            assertTrue(validator.accept(UUID.randomUUID().toString(), "iplayer", "second").isErr())
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
    fun `rejects conflicting stored names for one UUID separately`() {
        val validator = UserIdentityValidator()
        val uuid = UUID.randomUUID().toString()
        validator.accept(uuid, "Steve", "first")
        val result = validator.accept(uuid, "Alex", "second")
        assertTrue(result.isErr())
        assertContains(result.getError(), "Multiple usernames for UUID")
        // A failed validation must not reserve the rejected name in the validator.
        assertTrue(validator.accept(UUID.randomUUID().toString(), "Alex", "third").isOk())
    }
}
