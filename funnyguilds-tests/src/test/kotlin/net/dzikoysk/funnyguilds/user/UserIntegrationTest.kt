package net.dzikoysk.funnyguilds.user

import net.dzikoysk.funnyguilds.IntegrationTestSpecification
import net.dzikoysk.funnyguilds.server.entity.FakePlayer
import net.dzikoysk.funnyguilds.server.event.FunnyJoinEvent
import net.dzikoysk.funnyguilds.user.UserId.Companion.toUserId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class UserIntegrationTest : IntegrationTestSpecification() {

    @Test
    fun `should create user on join`() {
        // given: a player
        val player = FakePlayer()

        // when: the player joins a server
        server.callEvent(
            FunnyJoinEvent(
                player = player
            )
        )

        // then: user profile should be created
        val user = funnyGuilds.getComponent<UserService>().getUser(player.toUserId())
        assertThat(user).isNotNull
        assertThat(user?.id).isEqualTo(player.uniqueId)
        assertThat(user?.name).isEqualTo(player.name)
    }

}