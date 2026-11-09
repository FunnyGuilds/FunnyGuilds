package net.dzikoysk.funnyguilds.data.util

import org.junit.jupiter.api.Test
import java.util.Locale
import java.util.UUID
import kotlin.test.assertContains
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class UserIdentityValidatorTest {

    @Test
    fun `rejects exact and case insensitive collisions with both sources`() {
        for (name in listOf("Steve", "steve")) {
            val validator = UserIdentityValidator()
            val first = UUID.randomUUID().toString()
            val second = UUID.randomUUID().toString()
            validator.accept(first, "Steve", "first.yml")

            val error = assertFailsWith<IllegalStateException> { validator.accept(second, name, "second.yml") }
            for (detail in listOf(first, second, "first.yml", "second.yml", "STARTUP ABORTED", "back up")) {
                assertContains(error.message!!, detail)
            }
        }
    }

    @Test
    fun `accepts identical records and unrelated identities`() {
        val validator = UserIdentityValidator()
        val uuid = UUID.randomUUID().toString()
        validator.accept(uuid, "Steve", "first")
        validator.accept(uuid, "Steve", "duplicate")
        validator.accept(UUID.randomUUID().toString(), "Alex", "other")
    }

    @Test
    fun `normalization does not depend on server locale`() {
        val original = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("tr"))
            val validator = UserIdentityValidator()
            validator.accept(UUID.randomUUID().toString(), "IPlayer", "first")
            assertFailsWith<IllegalStateException> {
                validator.accept(UUID.randomUUID().toString(), "iplayer", "second")
            }
        } finally {
            Locale.setDefault(original)
        }
    }

    @Test
    fun `invalid UUID and missing names are data errors rather than UUID conflicts`() {
        for ((uuid, name) in listOf(null to "Steve", "not-a-uuid" to "Steve", "1-1-1-1-1" to "Steve",
                UUID.randomUUID().toString() to null, UUID.randomUUID().toString() to " ")) {
            val error = assertFailsWith<IllegalStateException> { UserIdentityValidator().accept(uuid, name, "source.yml") }
            assertContains(error.message!!, "source.yml")
            assertFalse(error.message!!.contains("IDENTITY CONFLICT"))
        }
    }

    @Test
    fun `rejects conflicting stored names for one UUID separately`() {
        val validator = UserIdentityValidator()
        val uuid = UUID.randomUUID().toString()
        validator.accept(uuid, "Steve", "first")
        val error = assertFailsWith<IllegalStateException> { validator.accept(uuid, "Alex", "second") }
        assertContains(error.message!!, "Multiple usernames for UUID")
    }
}
