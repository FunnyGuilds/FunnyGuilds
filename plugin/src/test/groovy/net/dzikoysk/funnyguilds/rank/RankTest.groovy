package net.dzikoysk.funnyguilds.rank

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.data.MutableEntity
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.GuildRank
import net.dzikoysk.funnyguilds.user.User
import net.dzikoysk.funnyguilds.user.UserRank
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

final class RankTest extends FunnyGuildsSpec {

    @Test
    void 'user should implement proper equals & hashcode'() {
        User user1 = userManager.create(UUID.nameUUIDFromBytes("user1".getBytes()), "user1")
        User user2 = userManager.create(UUID.nameUUIDFromBytes("user2".getBytes()), "user2")

        EqualsVerifier.forClass(Rank.class)
                .usingGetClass()
                .withRedefinedSubclass(UserRank.class)
                .withPrefabValues(MutableEntity.class, user1, user2)
                .withNonnullFields("entity")
                .withIgnoredFields("position")
                .verify()
    }

    @Test
    void 'guild should implement proper equals & hashcode'() {
        Guild guild1 = new Guild('guild1', 'TEST1')
        Guild guild2 = new Guild('guild2', 'TEST2')

        EqualsVerifier.forClass(Rank.class)
                .usingGetClass()
                .withRedefinedSubclass(GuildRank.class)
                .withPrefabValues(MutableEntity.class, guild1, guild2)
                .withNonnullFields("entity")
                .withIgnoredFields("position")
                .verify()
    }

}
