package net.dzikoysk.funnyguilds.feature.invitation.guild

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.guild.Guild
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GuildInvitationListTest : FunnyGuildsSpec() {

    @Test
    fun `revoking an invitation affects only its guild`() {
        val invitations = GuildInvitationList(userManager, guildManager)
        val target = userManager.createFake(UUID.randomUUID(), "candidate")
        val first = Guild("First", "AAA")
        val second = Guild("Second", "BBB")
        invitations.createInvitation(first, target)
        invitations.createInvitation(second, target)

        invitations.expireInvitation(first, target)

        assertFalse(invitations.hasInvitation(first, target))
        assertTrue(invitations.hasInvitation(second, target))

        invitations.expireAllFor(target)

        assertTrue(invitations.getInvitationsFor(target).isEmpty())
    }
}
