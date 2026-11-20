package net.dzikoysk.funnyguilds.feature.diplomacy

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.event.SimpleEventHandler
import net.dzikoysk.funnyguilds.event.guild.ally.GuildAcceptAllyInvitationEvent
import net.dzikoysk.funnyguilds.feature.audit.GuildAuditEntry.Type
import net.dzikoysk.funnyguilds.feature.audit.GuildAuditService
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitationList
import net.dzikoysk.funnyguilds.guild.Guild
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.lenient
import org.mockito.Mockito.mockStatic
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GuildDiplomacyServiceTest : FunnyGuildsSpec() {

    @Test
    fun `alliance acceptance ends a one-sided declaration with one withdrawal audit entry per guild`() {
        assertAllianceAcceptance(false)
    }

    @Test
    fun `alliance acceptance ends a mutual war with one withdrawal audit entry per guild`() {
        assertAllianceAcceptance(true)
    }

    private fun assertAllianceAcceptance(mutual: Boolean) {
        val firstOwner = userManager.createFake(UUID.randomUUID(), "FirstOwner")
        val secondOwner = userManager.createFake(UUID.randomUUID(), "SecondOwner")
        val first = Guild("First", "ONE")
        val second = Guild("Second", "TWO")
        first.setOwner(firstOwner)
        second.setOwner(secondOwner)
        guildManager.addGuild(first)
        guildManager.addGuild(second)
        first.addEnemy(second)
        if (mutual) {
            second.addEnemy(first)
        }
        val invitations = AllyInvitationList(guildManager)
        invitations.createInvitation(second, first)
        val audit = GuildAuditService(funnyGuilds)
        lenient().`when`(funnyGuilds.allyInvitationList).thenReturn(invitations)
        lenient().`when`(funnyGuilds.guildAuditService).thenReturn(audit)
        lenient().`when`(funnyGuilds.individualNameTagManager).thenReturn(Optional.empty())

        mockStatic(SimpleEventHandler::class.java).use { events ->
            events.`when`<Boolean> { SimpleEventHandler.handle(any(GuildAcceptAllyInvitationEvent::class.java)) }.thenReturn(true)
            GuildDiplomacyService(funnyGuilds).accept(firstOwner, first, second)
        }

        assertTrue(first.isAlly(second))
        assertTrue(second.isAlly(first))
        assertFalse(first.isEnemy(second))
        assertFalse(second.isEnemy(first))
        assertEquals(1, first.auditEntries.count { it.type() == Type.WAR_WITHDRAWN })
        assertEquals(1, second.auditEntries.count { it.type() == Type.WAR_WITHDRAWN })
    }

}
