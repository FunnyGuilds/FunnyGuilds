package net.dzikoysk.funnyguilds.user.model

import java.util.UUID
import com.dzikoysk.sqiffy.SqiffyDatabase
import com.dzikoysk.sqiffy.definition.Constraint
import com.dzikoysk.sqiffy.definition.ConstraintType.PRIMARY_KEY
import com.dzikoysk.sqiffy.definition.DataType.UUID_TYPE
import com.dzikoysk.sqiffy.definition.DataType.VARCHAR
import com.dzikoysk.sqiffy.definition.Definition
import com.dzikoysk.sqiffy.definition.DefinitionVersion
import com.dzikoysk.sqiffy.definition.Property
import com.dzikoysk.sqiffy.dsl.eq
import net.dzikoysk.funnyguilds.FunnyGuildsVersion.V_5_0_0
import net.dzikoysk.funnyguilds.server.entity.FunnyPlayer

@Definition([
    DefinitionVersion(
        version = V_5_0_0,
        name = "users",
        properties = [
            Property(name = "id", type = UUID_TYPE),
            Property(name = "name", type = VARCHAR, details = "48"),
        ],
        constraints = [
            Constraint(type = PRIMARY_KEY, name = "pk_users_id", on = ["id"]),
        ]
    )
])
object UserDefinition

@JvmInline
value class UserId(val value: UUID) {

    companion object {
        fun FunnyPlayer.toUserId() = UserId(uniqueId)
    }

}

interface UserRepository {

    fun createUser(playerId: UserId, name: String): User

    fun findUserById(id: UserId): User?

}

class SqlUserRepository(private val database: SqiffyDatabase) : UserRepository {

    override fun createUser(playerId: UserId, name: String): User =
        database
            .insert(UserTable) {
                it[UserTable.id] = playerId.value
                it[UserTable.name] = name
            }
            .execute()
            .let {
                User(
                    id = playerId.value,
                    name = name
                )
            }

    override fun findUserById(id: UserId): User? =
        database.select(UserTable)
            .where { UserTable.id eq id.value }
            .map {
                User(
                    id = it[UserTable.id],
                    name = it[UserTable.name]
                )
            }
            .firstOrNull()

}