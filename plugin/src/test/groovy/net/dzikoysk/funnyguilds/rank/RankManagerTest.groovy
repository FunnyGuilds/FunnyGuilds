package net.dzikoysk.funnyguilds.rank

import groovy.transform.CompileStatic
import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.guild.Guild
import net.dzikoysk.funnyguilds.guild.GuildRank
import net.dzikoysk.funnyguilds.user.UserManager
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class RankManagerTest extends FunnyGuildsSpec {

    @Test
    void 'should correctly update the guilds and users ranking'() {
        def userManager = new UserManager()
        def manager = new RankManager()

        def guild1 = new Guild("test1")
        def guild2 = new Guild("test2")
        def user1 = userManager.create(UUID.randomUUID(), "user1")
        def user2 = userManager.create(UUID.randomUUID(), "user2")

        guild1.rank = new GuildRank(guild1)
        guild1.addMember(user1)
        user1.rank.points = 101

        guild2.rank = new GuildRank(guild2)
        guild2.addMember(user2)
        user2.rank.points = 100

        manager.update(guild1)
        manager.update(guild2)
        manager.update(user1)
        manager.update(user2)

        assertEquals guild1, manager.getGuild(1)
        assertEquals guild2, manager.getGuild(2)
        assertEquals user1, manager.getUser(1)
        assertEquals user2, manager.getUser(2)

        user1.rank.points = 100
        user2.rank.points = 101

        manager.update(guild1)
        manager.update(guild2)
        manager.update(user1)
        manager.update(user2)

        assertEquals guild2, manager.getGuild(1)
        assertEquals guild1, manager.getGuild(2)
        assertEquals user2, manager.getUser(1)
        assertEquals user1, manager.getUser(2)
    }

}
