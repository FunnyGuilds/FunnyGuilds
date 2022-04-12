package net.dzikoysk.funnyguilds.user

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.data.MutableEntity
import net.dzikoysk.funnyguilds.guild.Guild
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import java.lang.ref.WeakReference
import java.util.*

class UserTest : FunnyGuildsSpec() {

    @Test
    fun `user_should_implement_proper_equals_&_hashcode`() {
        val user1 = userManager.createFake(UUID.nameUUIDFromBytes("user1".toByteArray()), "user1")
        val user2 = userManager.createFake(UUID.nameUUIDFromBytes("user2".toByteArray()), "user2")
        val guild1 = Guild("guild1", "TEST1")
        val guild2 = Guild("guild2", "TEST2")

        EqualsVerifier.forClass(User::class.java)
                .usingGetClass()
                .withNonnullFields("uuid")
                .withPrefabValues(Guild::class.java, guild1, guild2)
                .withPrefabValues(UserCache::class.java, user1.cache, user2.cache)
                .withPrefabValues(MutableEntity::class.java, user1, user2)
                .withPrefabValues(WeakReference::class.java, WeakReference<Any?>(null), WeakReference<Any?>(null))
                .withIgnoredFields("name", "cache", "rank", "playerRef", "guild", "ban", "profile", "bossBarProvider", "wasChanged")
                .verify()
    }


}