package net.dzikoysk.funnyguilds.guild

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.data.MutableEntity
import net.dzikoysk.funnyguilds.user.User
import nl.jqno.equalsverifier.EqualsVerifier
import org.bukkit.Location
import org.junit.jupiter.api.Test
import java.util.*

class GuildTest : FunnyGuildsSpec() {

    @Test
    fun `guild should implement proper equals & hashcode`() {
        val user1 = userManager.createFake(UUID.nameUUIDFromBytes("user1".toByteArray()), "user1")
        val user2 = userManager.createFake(UUID.nameUUIDFromBytes("user2".toByteArray()), "user2")
        val guild1 = Guild("guild1", "TEST1")
        val guild2 = Guild("guild2", "TEST2")
        val region1 = Region("region1", Location(null, 0.0, 0.0, 0.0))
        val region2 = Region("region2", Location(null, 0.0, 0.0, 0.0))

        EqualsVerifier.forClass(Guild::class.java)
                .usingGetClass()
                .withNonnullFields("uuid")
                .withPrefabValues(Guild::class.java, guild1, guild2)
                .withPrefabValues(Region::class.java, region1, region2)
                .withPrefabValues(User::class.java, user1, user2)
                .withPrefabValues(MutableEntity::class.java, user1, user2)
                .withPrefabValues(Location::class.java, Location(null, 0.0, 0.0, 0.0), Location(null, 1.0, 2.0, 3.0))
                .withIgnoredFields("name", "tag", "rank", "lives", "region", "home", "owner")
                .withIgnoredFields("members", "deputies", "allies", "enemies", "alliedPvPGuilds")
                .withIgnoredFields("born", "validity", "protection", "build", "ban", "pvp", "wasChanged")
                .verify()
    }

}