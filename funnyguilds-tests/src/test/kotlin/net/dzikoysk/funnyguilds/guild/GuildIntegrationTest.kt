package net.dzikoysk.funnyguilds.guild

import net.dzikoysk.funnyguilds.IntegrationTestSpecification
import net.dzikoysk.funnyguilds.user.model.UserId.Companion.toUserId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GuildIntegrationTest : IntegrationTestSpecification() {

    @Test
    fun `should create guild`() {
        // given: a player with user profile and guild name
        val player = createFakePlayerWithUserProfile()
        val guildName = "MONKE"

        // when: the player creates a guild using command
        val context = server.callCommand(player, "create $guildName")

        // then: guild should be created
        assertThat(context.messages).hasSize(1)
        assertThat(context.messages.first()).contains(guildName)

        val userGuild = funnyGuilds.getComponent<GuildService>().getGuild(player.toUserId())
        assertThat(userGuild).isNotNull
        assertThat(userGuild?.name).isEqualTo(guildName)
    }

}