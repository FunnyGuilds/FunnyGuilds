package net.dzikoysk.funnyguilds.rank

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.data.MutableEntity
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.GuildRank
import net.dzikoysk.funnyguilds.user.UserRank
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import java.util.*

class RankTest : FunnyGuildsSpec() {

    @Test
    fun `user_should_implement_proper_equals_&_hashcode`() {
        val user1 = userManager.createFake(UUID.nameUUIDFromBytes("user1".toByteArray()), "user1")
        val user2 = userManager.createFake(UUID.nameUUIDFromBytes("user2".toByteArray()), "user2")

        EqualsVerifier.forClass(Rank::class.java)
                .usingGetClass()
                .withRedefinedSubclass(UserRank::class.java)
                .withPrefabValues(MutableEntity::class.java, user1, user2).withNonnullFields("entity")
                .withIgnoredFields("position")
                .verify()
    }

    @Test
    fun `guild_should_implement_proper_equals_&_hashcode`() {
        val guild1 = Guild("guild1", "TEST1")
        val guild2 = Guild("guild2", "TEST2")

        EqualsVerifier.forClass(Rank::class.java)
                .usingGetClass()
                .withRedefinedSubclass(GuildRank::class.java)
                .withPrefabValues(MutableEntity::class.java, guild1, guild2)
                .withNonnullFields("entity")
                .withIgnoredFields("position")
                .verify()
    }

}