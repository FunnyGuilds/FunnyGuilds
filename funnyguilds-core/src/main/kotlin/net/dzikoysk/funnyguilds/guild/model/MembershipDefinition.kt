package net.dzikoysk.funnyguilds.guild.model

import com.dzikoysk.sqiffy.SqiffyDatabase
import com.dzikoysk.sqiffy.definition.Constraint
import com.dzikoysk.sqiffy.definition.ConstraintType.FOREIGN_KEY
import com.dzikoysk.sqiffy.definition.DataType.UUID_TYPE
import com.dzikoysk.sqiffy.definition.DataType.VARCHAR
import com.dzikoysk.sqiffy.definition.Definition
import com.dzikoysk.sqiffy.definition.DefinitionVersion
import com.dzikoysk.sqiffy.definition.Property
import com.dzikoysk.sqiffy.dsl.and
import com.dzikoysk.sqiffy.dsl.eq
import net.dzikoysk.funnyguilds.FunnyGuildsVersion.V_5_0_0
import net.dzikoysk.funnyguilds.user.model.UserDefinition
import net.dzikoysk.funnyguilds.user.model.UserId

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
            Property(name = "guildId", type = UUID_TYPE),
            Property(name = "userId", type = UUID_TYPE),
            Property(name = "role", type = VARCHAR, details = "16"), // TODO: Support enums in Sqiffy
        ],
        constraints = [
            Constraint(type = FOREIGN_KEY, on = ["guildId"], name = "fk_guild_memberships_guildId", referenced = GuildDefinition::class, references = "id"),
            Constraint(type = FOREIGN_KEY, on = ["userId"], name = "fk_guild_memberships_userId", referenced = UserDefinition::class, references = "id")
        ]
    )
])
object MembershipDefinition

interface MembershipRepository {

    fun createMembership(guildId: GuildId, userId: UserId, role: MembershipRole): Membership

    fun findMembershipByUser(userId: UserId): Membership?

    fun deleteMembership(membership: Membership)

}

class SqlMembershipRepository(private val database: SqiffyDatabase) : MembershipRepository {

    override fun createMembership(guildId: GuildId, userId: UserId, role: MembershipRole): Membership =
        database
            .insert(MembershipTable) {
                it[MembershipTable.guildId] = guildId.value
                it[MembershipTable.userId] = userId.value
                it[MembershipTable.role] = role.name
            }
            .execute()
            .let {
                Membership(
                    guildId = guildId.value,
                    userId = userId.value,
                    role = role.name
                )
            }

    override fun findMembershipByUser(userId: UserId): Membership? =
        database.select(MembershipTable)
            .where { MembershipTable.userId eq userId.value }
            .map {
                Membership(
                    guildId = it[MembershipTable.guildId],
                    userId = it[MembershipTable.userId],
                    role = it[MembershipTable.role]
                )
            }
            .firstOrNull()

    override fun deleteMembership(membership: Membership) {
        database.delete(MembershipTable)
            .where {
                and(
                    MembershipTable.guildId eq membership.guildId,
                    MembershipTable.userId eq membership.userId
                )
            }
    }

}