@file:Suppress("RemoveRedundantQualifierName")

package net.dzikoysk.funnyguilds.guild.model

import com.dzikoysk.sqiffy.Constraint
import com.dzikoysk.sqiffy.ConstraintType.FOREIGN_KEY
import com.dzikoysk.sqiffy.DataType.UUID_BINARY
import com.dzikoysk.sqiffy.DataType.VARCHAR
import com.dzikoysk.sqiffy.Definition
import com.dzikoysk.sqiffy.DefinitionVersion
import com.dzikoysk.sqiffy.Property
import com.dzikoysk.sqiffy.Sqiffy
import net.dzikoysk.funnyguilds.FunnyGuildsVersion.V_5_0_0
import net.dzikoysk.funnyguilds.user.UserDefinition
import net.dzikoysk.funnyguilds.user.UserId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

enum class MembershipRole {
    OWNER,
    DEPUTY,
    MEMBER
}

@Definition([
    DefinitionVersion(
        version = V_5_0_0,
        name = "guild_memberships",
        properties = [
            Property(name = "guildId", type = UUID_BINARY),
            Property(name = "userId", type = UUID_BINARY),
            Property(name = "role", type = VARCHAR, details = "16"), // TODO: Support enums in Sqiffy
        ],
        constraints = [
            Constraint(type = FOREIGN_KEY, on = "guildId", name = "fk_guild_memberships_guildId", referenced = GuildDefinition::class, references = "id"),
            Constraint(type = FOREIGN_KEY, on = "userId", name = "fk_guild_memberships_userId", referenced = UserDefinition::class, references = "id")
        ]
    )
])
object MembershipDefinition

interface MembershipRepository {

    fun createMembership(guildId: GuildId, userId: UserId, role: MembershipRole): Membership

    fun findMembershipByUser(userId: UserId): Membership?

    fun deleteMembership(membership: Membership)

}

class SqlMembershipRepository(private val sqiffy: Sqiffy) : MembershipRepository {

    override fun createMembership(guildId: GuildId, userId: UserId, role: MembershipRole): Membership =
        sqiffy.transaction {
            val membership = Membership(
                guildId = guildId.value,
                userId = userId.value,
                role = role.name
            )

            val result = MembershipTable.insert {
                it[MembershipTable.guildId] = membership.guildId
                it[MembershipTable.userId] = membership.userId
                it[MembershipTable.role] = membership.role
            }

            require(result.resultedValues?.size == 1) { "Failed to insert membership: $membership" }
            membership
        }

    override fun findMembershipByUser(userId: UserId): Membership? =
        sqiffy.transaction {
            MembershipTable.select { MembershipTable.userId eq userId.value }
                .firstOrNull()
                ?.let {
                    Membership(
                        guildId = it[MembershipTable.guildId],
                        userId = it[MembershipTable.userId],
                        role = it[MembershipTable.role]
                    )
                }
        }

    override fun deleteMembership(membership: Membership) {
        sqiffy.transaction {
            MembershipTable.deleteWhere {
                MembershipTable.guildId eq membership.guildId
                MembershipTable.userId eq membership.userId
            }
        }
    }

}