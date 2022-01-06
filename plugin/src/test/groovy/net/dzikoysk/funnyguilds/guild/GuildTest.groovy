package net.dzikoysk.funnyguilds.guild

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.data.MutableEntity
import net.dzikoysk.funnyguilds.user.User
import nl.jqno.equalsverifier.EqualsVerifier
import org.bukkit.Location
import org.bukkit.World
import org.junit.jupiter.api.Test

import java.lang.ref.ReferenceQueue

final class GuildTest extends FunnyGuildsSpec {

    @Test
    void 'guild should implement proper equals & hashcode'() {
        User user1 = userManager.create(UUID.nameUUIDFromBytes("user1".getBytes()), "user1")
        User user2 = userManager.create(UUID.nameUUIDFromBytes("user2".getBytes()), "user2")
        Guild guild1 = new Guild("guild1")
        Guild guild2 = new Guild("guild2")
        def region1 = new Region("region1")
        def region2 = new Region("region2")


        EqualsVerifier.forClass(Guild.class)
                .usingGetClass()
                .withNonnullFields("uuid")
                .withPrefabValues(Guild.class, guild1, guild2)
                .withPrefabValues(Region.class, region1, region2)
                .withPrefabValues(User.class, user1, user2)
                .withPrefabValues(MutableEntity.class, user1, user2)
                .withPrefabValues(Location.class, new Location(null, 0, 0, 0), new Location(null, 1, 2, 3))
                .withIgnoredFields("name", "tag", "rank", "lives", "region", "home", "owner")
                .withIgnoredFields("members", "deputies", "allies", "enemies", "alliedFFGuilds")
                .withIgnoredFields("born", "validity", "validityDate", "protection", "build", "ban", "pvp", "wasChanged")
                .verify()
    }

}
