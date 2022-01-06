package net.dzikoysk.funnyguilds.user

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.data.MutableEntity
import net.dzikoysk.funnyguilds.guild.Guild
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

import java.lang.ref.WeakReference

final class UserTest extends FunnyGuildsSpec {

    @Test
    void 'user should implement proper equals & hashcode'() {
        User user1 = userManager.create(UUID.nameUUIDFromBytes("user1".getBytes()), "user1")
        User user2 = userManager.create(UUID.nameUUIDFromBytes("user2".getBytes()), "user2")
        Guild guild1 = new Guild("")
        Guild guild2 = new Guild("")


        EqualsVerifier.forClass(User.class)
                .usingGetClass()
                .withNonnullFields("uuid")
                .withPrefabValues(Guild.class, guild1, guild2)
                .withPrefabValues(UserCache.class, user1.getCache(), user2.getCache())
                .withPrefabValues(MutableEntity.class, user1, user2)
                .withPrefabValues(WeakReference.class, new WeakReference<>(null), new WeakReference<>(null))
                .withIgnoredFields("name", "cache", "rank", "playerRef", "guild", "ban", "bossBarProvider", "wasChanged")
                .verify()
    }

}
