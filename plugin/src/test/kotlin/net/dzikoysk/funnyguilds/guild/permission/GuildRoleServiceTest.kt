package net.dzikoysk.funnyguilds.guild.permission

import net.dzikoysk.funnyguilds.FunnyGuildsSpec
import net.dzikoysk.funnyguilds.data.util.GuildRoleSerializer
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException
import net.dzikoysk.funnyguilds.feature.protection.GuildProtectionPermission
import net.dzikoysk.funnyguilds.guild.Guild
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GuildRoleServiceTest : FunnyGuildsSpec() {

    @Test
    fun `allow deny and inherit reach the same protection permission used by the listener`() {
        val owner = userManager.createFake(UUID.randomUUID(), "leader")
        val member = userManager.createFake(UUID.randomUUID(), "member")
        val guild = Guild("Test Guild", "TEST")
        guild.setOwner(owner)
        guild.addMember(member)
        val service = GuildRoleService()
        val checker = GuildPermissionChecker.create(funnyGuilds)
        val permission = GuildProtectionPermission.BUCKET_EMPTY
        val event = org.mockito.Mockito.mock(org.bukkit.event.Event::class.java)

        service.setRolePermission(guild, owner, GuildRolePolicy.MEMBER, permission, false)
        service.setOverride(guild, owner, member, permission, true)
        assertTrue(checker.handleProtectionPermission(guild, member, permission, event))

        service.setOverride(guild, owner, member, permission, false)
        assertFalse(checker.getProtectionPermissionResult(guild, member, permission, event).isOk)

        service.setOverride(guild, owner, member, permission, null)
        assertEquals(GuildPermissionDecision(false, GuildPermissionDecision.Source.BUILT_IN_ROLE),
            checker.getStoredDecision(guild, member, permission))
    }

    @Test
    fun `custom roles and exceptions persist and reflect a return to the built in role`() {
        val owner = userManager.createFake(UUID.randomUUID(), "leader")
        val member = userManager.createFake(UUID.randomUUID(), "member")
        val guild = Guild("Test Guild", "TEST")
        guild.setOwner(owner)
        guild.addMember(member)
        val service = GuildRoleService()
        val role = UUID.randomUUID()
        val check = GuildPermissionChecker.create(funnyGuilds)
        val permission = GuildCommandPermission.PVP

        service.createRole(guild, owner, role, "Diplomat")
        service.setRolePermission(guild, owner, role, permission, true)
        service.assign(guild, owner, member, role)
        assertEquals(GuildPermissionDecision(true, GuildPermissionDecision.Source.CUSTOM_ROLE),
            check.getStoredDecision(guild, member, permission))

        service.setOverride(guild, owner, member, permission, false)
        val saved = GuildRoleSerializer.deserialize(GuildRoleSerializer.serialize(guild.rolePolicy))
        assertEquals(guild.rolePolicy, saved)
        guild.setRolePolicy(saved)
        assertEquals(GuildPermissionDecision(false, GuildPermissionDecision.Source.PLAYER),
            check.getStoredDecision(guild, member, permission))

        service.setOverride(guild, owner, member, permission, null)
        service.deleteRole(guild, owner, role)
        assertEquals(GuildRolePolicy.MEMBER, service.selectedRole(guild, member))
        assertFalse(check.getStoredDecision(guild, member, permission).allowed())
        assertTrue(check.getStoredDecision(guild, owner, permission).allowed())
    }

    @Test
    fun `a deputy needs an explicit scope and cannot assign a role with a hidden grant`() {
        val owner = userManager.createFake(UUID.randomUUID(), "leader")
        val deputy = userManager.createFake(UUID.randomUUID(), "deputy")
        val member = userManager.createFake(UUID.randomUUID(), "member")
        val guild = Guild("Test Guild", "TEST")
        guild.setOwner(owner)
        guild.addMember(deputy)
        guild.addDeputy(deputy)
        guild.addMember(member)
        val service = GuildRoleService()
        val role = UUID.randomUUID()
        service.setRolePermission(guild, owner, GuildRolePolicy.DEPUTY, GuildCommandPermission.MANAGE_PERMISSIONS, true)
        service.createRole(guild, owner, role, "Diplomat")
        service.setRolePermission(guild, owner, role, GuildCommandPermission.WAR, true)
        service.setOverride(guild, owner, member, GuildCommandPermission.WAR, false)
        assertFailsWith<InternalValidationException> { service.assign(guild, deputy, member, role) }
        assertFailsWith<InternalValidationException> {
            service.setOverride(guild, deputy, member, GuildProtectionPermission.BLOCK_BREAK, false)
        }

        service.setDelegation(guild, owner, GuildProtectionPermission.BLOCK_BREAK, true)
        service.setOverride(guild, deputy, member, GuildProtectionPermission.BLOCK_BREAK, false)
        assertFalse(GuildPermissionChecker.create(funnyGuilds)
            .getStoredDecision(guild, member, GuildProtectionPermission.BLOCK_BREAK).allowed())
        assertFailsWith<InternalValidationException> {
            service.setOverride(guild, deputy, deputy, GuildProtectionPermission.BLOCK_BREAK, true)
        }
    }
}
